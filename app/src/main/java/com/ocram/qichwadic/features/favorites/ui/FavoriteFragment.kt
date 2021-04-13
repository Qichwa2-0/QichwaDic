package com.ocram.qichwadic.features.favorites.ui


import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.domain.model.DefinitionModel
import com.ocram.qichwadic.core.ui.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_favorite.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class FavoriteFragment : Fragment(), FavoriteAdapter.FavoriteClickListener {

    private val favoriteViewModel by viewModel<FavoriteViewModel>()
    private lateinit var favoriteAdapter: FavoriteAdapter
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()

        val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        val appBarConfiguration = AppBarConfiguration(navController.graph, drawer)

        view.findViewById<Toolbar>(R.id.mToolbar).setupWithNavController(navController, appBarConfiguration)
        (mToolbar as Toolbar).apply {
            inflateMenu(R.menu.menu_favorite)
            setupWithNavController(navController, appBarConfiguration)
            setOnMenuItemClickListener { item ->
                val clearFavorites = item.itemId == R.id.nav_clear_favorites
                if (clearFavorites) {
                    if (favoriteAdapter.itemCount > 0) {
                        alertDialog?.show()
                    }
                }
                clearFavorites
            }
        }

        createDialog()
        rvFavorites.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        setAdapters()
        favoriteViewModel.favorites.observe(this, Observer<List<DefinitionModel>> { this.onFavoritesLoaded(it) })
        favoriteViewModel.deleteFavoriteResult.observe(this, Observer<Boolean> { this.onFavoriteRemoved(it) })
        favoriteViewModel.clearFavoriteResult.observe(this, Observer<Boolean> { this.onFavoritesCleared(it) })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_favorite, menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        alertDialog?.let {
            if(it.isShowing) it.dismiss()
        }
        alertDialog = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.nav_clear_favorites) {
            if (favoriteAdapter.itemCount > 0) {
                alertDialog?.show()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemShare(definition: DefinitionModel) {
        val text = getString(
                R.string.share_definition_from_dictionary,
                definition.dictionaryName,
                definition.word,
                definition.meaning
        )
        (requireActivity() as MainActivity)
                .openShareIntent(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY).toString())
    }

    override fun onItemRemove(definition: DefinitionModel) {
        favoriteViewModel.removeFavorite(definition)
    }

    private fun setAdapters() {
        favoriteAdapter = FavoriteAdapter(ArrayList(), this)
        rvFavorites.adapter = favoriteAdapter
    }

    private fun createDialog() {
        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle(R.string.favorite_dialog_title)
        alert.setMessage(R.string.favorite_dialog_content)
        alert.setPositiveButton(R.string.favorite_dialog_clear_confirm) { _, _ ->
            favoriteViewModel.clearFavorites()
        }
        alert.setNegativeButton(R.string.favorite_dialog_clear_cancel) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
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
