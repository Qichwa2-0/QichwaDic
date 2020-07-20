package com.ocram.qichwadic.features.favorites.ui

import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer

import com.google.android.material.snackbar.Snackbar
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.activity.BaseActivity
import com.ocram.qichwadic.core.domain.model.DefinitionModel

import java.util.ArrayList

import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.android.synthetic.main.activity_favorites.*

class FavoriteActivity : BaseActivity(), FavoriteAdapter.FavoriteClickListener {

    private val favoriteViewModel by viewModel<FavoriteViewModel>()
    private lateinit var favoriteAdapter: FavoriteAdapter
    private var alertDialog: AlertDialog? = null

    override val layoutId: Int = R.layout.activity_favorites

    override fun getToolbar(): Toolbar? {
        return mToolbar as Toolbar?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createDialog()
        setAdapters()
        favoriteViewModel.favorites.observe(this, Observer<List<DefinitionModel>> { this.onFavoritesLoaded(it) })
        favoriteViewModel.deleteFavoriteResult.observe(this, Observer<Boolean> { this.onFavoriteRemoved(it) })
        favoriteViewModel.clearFavoriteResult.observe(this, Observer<Boolean> { this.onFavoritesCleared(it) })
    }

    override fun initViews() {
        setTitle(getString(R.string.nav_favorites))
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvFavorites.layoutManager = layoutManager
    }

    override fun onDestroy() {
        super.onDestroy()
        alertDialog?.let {
            if(it.isShowing) it.dismiss()
        }
        alertDialog = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_favorite, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        } else if (id == R.id.nav_clear_favorites) {
            if (favoriteAdapter.itemCount > 0) {
                alertDialog!!.show()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemShare(definition: DefinitionModel) {
        val text = getString(R.string.share_definition_from_dictionary,
                definition.dictionaryName, definition.word, definition.meaning)
        openShareIntent(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY).toString())
    }

    override fun onItemRemove(definition: DefinitionModel) {
        favoriteViewModel.removeFavorite(definition)
    }

    private fun setAdapters() {
        favoriteAdapter = FavoriteAdapter(ArrayList(), this)
        rvFavorites.adapter = favoriteAdapter
    }

    private fun createDialog() {
        val alert = AlertDialog.Builder(this)
        alert.setTitle(R.string.favorite_dialog_title)
        alert.setMessage(R.string.favorite_dialog_content)
        alert.setPositiveButton(R.string.favorite_dialog_clear_confirm
        ) { dialogInterface, i -> favoriteViewModel.clearFavorites() }
        alert.setNegativeButton(R.string.favorite_dialog_clear_cancel
        ) { dialogInterface, i -> dialogInterface.dismiss() }
        alertDialog = alert.create()
    }

    private fun onFavoritesLoaded(favorites: List<DefinitionModel>) {
        favoriteAdapter.setFavorites(favorites)
        favoriteAdapter.notifyDataSetChanged()
        pbFavoriteLoading.visibility = View.GONE
        if (favorites.isNotEmpty()) {
            rvFavorites.visibility = View.VISIBLE
            tvNoFavorites.visibility = View.GONE
        } else {
            rvFavorites.visibility = View.GONE
            tvNoFavorites.visibility = View.VISIBLE
        }
    }

    private fun onFavoriteRemoved(result: Boolean?) {
        val stringId = if (java.lang.Boolean.TRUE == result) R.string.favorite_deleted_success else R.string.favorite_deleted_error
        Snackbar.make(clFavorites, getString(stringId), Snackbar.LENGTH_SHORT).show()
    }

    private fun onFavoritesCleared(result: Boolean?) {
        val stringId = if (java.lang.Boolean.TRUE == result) R.string.favorite_cleared_success else R.string.favorite_cleared_error
        Snackbar.make(clFavorites, getString(stringId), Snackbar.LENGTH_SHORT).show()
    }
}