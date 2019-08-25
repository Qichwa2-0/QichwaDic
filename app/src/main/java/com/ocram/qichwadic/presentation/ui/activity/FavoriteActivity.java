package com.ocram.qichwadic.presentation.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.ocram.qichwadic.R;
import com.ocram.qichwadic.domain.model.Favorite;
import com.ocram.qichwadic.presentation.ui.adapters.FavoriteAdapter;
import com.ocram.qichwadic.presentation.viewmodel.FavoriteViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class FavoriteActivity extends BaseActivity implements FavoriteAdapter.FavoriteClickListener{

    @BindView(R.id.clFavorites) CoordinatorLayout clFavorites;
    @BindView(R.id.tvNoFavorites) TextView tvNoFavorites;
    @BindView(R.id.rvFavorites) RecyclerView rvFavorites;
    @BindView(R.id.pbFavoriteLoading) ProgressBar pbFavoriteLoading;
    private FavoriteViewModel favoriteViewModel;
    private FavoriteAdapter favoriteAdapter;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createDialog();
        setAdapters();
        favoriteViewModel = ViewModelProviders.of(this).get(FavoriteViewModel.class);
        favoriteViewModel.getFavoriteLiveData().observe(this, this::onFavoritesLoaded);
        favoriteViewModel.getIsLoading().observe(this, this::onLoadingChanged);
        favoriteViewModel.getDeleteFavoriteResult().observe(this, this::onFavoriteRemoved);
        favoriteViewModel.getClearFavoriteResult().observe(this, this::onFavoritesCleared);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_favorites;
    }

    @Override
    protected void initViews() {
        setTitle(getString(R.string.nav_favorites));
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvFavorites.setLayoutManager(layoutManager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(alertDialog != null && alertDialog.isShowing()){
            alertDialog.dismiss();
        }
        alertDialog = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }else if(id == R.id.nav_clear_favorites){
            if(favoriteAdapter.getItemCount() > 0){
                alertDialog.show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemShare(Favorite favorite) {
        String text = getString(R.string.share_definition_from_dictionary,
                favorite.getDictionaryName(), favorite.getWord(), favorite.getMeaning());
        openShareIntent(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY).toString());
    }

    @Override
    public void onItemRemove(Favorite favorite) {
        favoriteViewModel.removeFavorite(favorite);
    }

    private void setAdapters(){
        favoriteAdapter = new FavoriteAdapter(new ArrayList<>(), this);
        rvFavorites.setAdapter(favoriteAdapter);
    }

    private void createDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.favorite_dialog_title);
        alert.setMessage(R.string.favorite_dialog_content);
        alert.setPositiveButton(R.string.favorite_dialog_clear_confirm,
                (dialogInterface, i) -> favoriteViewModel.clearFavorites());
        alert.setNegativeButton(R.string.favorite_dialog_clear_cancel,
                (dialogInterface, i) -> dialogInterface.dismiss());
        alertDialog = alert.create();
    }

    private void onFavoritesLoaded(List<Favorite> favorites) {
        favoriteAdapter.setFavorites(favorites);
        favoriteAdapter.notifyDataSetChanged();
        if(favorites != null && !favorites.isEmpty()){
            rvFavorites.setVisibility(View.VISIBLE);
            tvNoFavorites.setVisibility(View.GONE);
        }else{
            rvFavorites.setVisibility(View.GONE);
            tvNoFavorites.setVisibility(View.VISIBLE);
        }
    }

    private void onLoadingChanged(Boolean isLoading) {
        pbFavoriteLoading.setVisibility(isLoading == null || !isLoading ? View.GONE : View.VISIBLE);
    }

    private void onFavoriteRemoved(Boolean result) {
        int stringId = Boolean.TRUE.equals(result) ? R.string.favorite_deleted_success : R.string.favorite_deleted_error;
        Snackbar.make(clFavorites, getString(stringId), Snackbar.LENGTH_SHORT).show();
    }

    private void onFavoritesCleared(Boolean result) {
        int stringId = Boolean.TRUE.equals(result) ? R.string.favorite_cleared_success : R.string.favorite_cleared_error;
        Snackbar.make(clFavorites, getString(stringId), Snackbar.LENGTH_SHORT).show();
    }
}