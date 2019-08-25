package com.ocram.qichwadic.presentation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.ocram.qichwadic.QichwaDicApplication;
import com.ocram.qichwadic.R;
import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.SearchResult;
import com.ocram.qichwadic.presentation.ui.adapters.DictLangAdapter;
import com.ocram.qichwadic.presentation.ui.adapters.ResultListAdapter;
import com.ocram.qichwadic.presentation.ui.adapters.SpinnerResultAdapter;
import com.ocram.qichwadic.presentation.ui.custom.EndlessRecyclerViewScrollListener;
import com.ocram.qichwadic.presentation.model.DictLang;
import com.ocram.qichwadic.framework.preferences.PreferencesHelper;
import com.ocram.qichwadic.presentation.viewmodel.SearchViewModel;
import com.ocram.qichwadic.presentation.viewmodel.viewstate.SearchParams;
import com.ocram.qichwadic.presentation.viewmodel.viewstate.SearchViewState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import io.reactivex.disposables.CompositeDisposable;

public class SearchActivity extends BaseActivity implements ResultListAdapter.DefinitionClickListener {

    @BindView(R.id.clSearch) CoordinatorLayout clSearch;
    @BindView(R.id.svSearch) SearchView svSearch;
    @BindView(R.id.spResults) Spinner spResults;
    @BindView(R.id.rvResults) RecyclerView rvResults;
    @BindView(R.id.searchProgressBar) ProgressBar searchProgressBar;
    @BindView(R.id.fetchMoreProgressBar) ProgressBar fetchMoreProgressBar;
    @BindView(R.id.llNoResults) LinearLayout llNoResults;
    @BindView(R.id.llSearchOptions) LinearLayout llSearchOptions;
    @BindView(R.id.spSearchTypes) Spinner spSearchTypes;
    @BindView(R.id.llResultsArea) RelativeLayout llResultsArea;
    @BindView(R.id.tvResultsTotal) TextView tvResultsTotal;
    @BindView(R.id.fabSearch) FloatingActionButton fabSearch;
    @BindView(R.id.ivSwapLanguages) ImageView ivSwapLanguages;
    @BindView(R.id.tvQichwaLang) TextView tvQichwaLang;
    @BindView(R.id.spDictLangs) Spinner spDictLangs;
    @BindView(R.id.vSeparator) View vSeparator;
    @BindView(R.id.btnNoResultsGetDictionaries) Button btnNoResultsGetDictionaries;
    private SpinnerResultAdapter spinnerResultAdapter;
    private ResultListAdapter resultListAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int searchType;
    private Map<String, String> placeholders;
    private String quechuaPlaceholder;
    private boolean isFromQuechua;
    private SearchParams searchParams;
    private CompositeDisposable compositeDisposable;

    private SearchViewModel viewModel;

    @Inject
    PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        preferencesHelper = QichwaDicApplication.get(this).getAppComponent().getPreferencesHelper();
        super.onCreate(savedInstanceState);
        this.compositeDisposable = new CompositeDisposable();
        loadPlaceholders();
        setAdapters();
        setSearchView();
        viewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        viewModel.getResultLiveData().observe(this, this::onResultsChanged);
        viewModel.getSearchViewState().observe(this, this::onSearchViewStateChanged);
        viewModel.getExtraDefinitions().observe(this, this::onFetchExtraDefinitions);
        viewModel.getLoadFetchMore().observe(this, this::onFetchMoreLoadingChanged);
        viewModel.getSaveFavoriteResult().observe(this, this::onSaveWordResult);
        this.initSavedSearchPrefs();
    }

    private void initSavedSearchPrefs() {
        if(this.searchParams == null) {
            compositeDisposable.add(
                    preferencesHelper.getLastSearchParams()
                            .doOnNext(this::setSearchParams)
                            .doOnTerminate(() -> svSearch.setQuery(svSearch.getQueryHint(), true))
                            .subscribe()
            );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scrollListener = null;
        if(compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_dictionaries) {
            openDictionariesGallery();
            return true;
        } else if(id == R.id.nav_favorites){
            Intent intent = new Intent(SearchActivity.this, FavoriteActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.nav_about){
            Intent intent = new Intent(SearchActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViews() {
        setToolbar();
        setRecyclerViews();
    }

    private void setToolbar(){
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    private void setRecyclerViews(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvResults.setLayoutManager(layoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                SearchResult currentSearchResult = (SearchResult)spResults.getSelectedItem();
                int totalToFetch = currentSearchResult.getTotal();
                if(totalToFetch > 20 && resultListAdapter.getItemCount() < totalToFetch){
                    int dictionaryId = currentSearchResult.getDictionaryId();
                    if(view != null){
                        viewModel.fetchMoreResults(dictionaryId, searchType, svSearch.getQuery().toString(), page + 1);
                    }
                }
            }
        };
        rvResults.addOnScrollListener(scrollListener);
    }

    @Override
    public void onItemShareClick(Definition definition) {
        String text = getString(R.string.share_definition_from_dictionary,
                definition.getDictionaryName(), definition.getWord(), definition.getMeaning());
        openShareIntent(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY).toString());
    }

    @Override
    public void onItemFavoriteClick(Definition definition) {
        viewModel.saveFavorite(definition);
    }

    @OnClick(R.id.fabSearch)
    public void openFavorites(View v){
        if(svSearch.getQuery().length() > 0){
            svSearch.setQuery(svSearch.getQuery(), true);
        }
    }

    @OnClick(R.id.btnNoResultsGetDictionaries)
    public void openDictionariesGallery(){
        Intent intent = new Intent(SearchActivity.this, DictionaryActivity.class);
        startActivity(intent);
    }

    @OnItemSelected(R.id.spResults)
    void onItemSelected(int pos) {
        Log.d(getClass().getName(), "item selected " + pos);
        SearchResult result = (SearchResult) spResults.getItemAtPosition(pos);
        selectResult(result);
    }

    @OnClick(R.id.ivSwapLanguages)
    public void swapLanguages(View v){
        this.isFromQuechua = !this.isFromQuechua;
        this.updateViewsOnLangChange();
        preferencesHelper.saveSearchFromQuechua(this.isFromQuechua);
    }

    @OnItemSelected(R.id.spDictLangs)
    void onDictLangSelected() {
        updateQueryHint();
        preferencesHelper.saveNonQuechuaLangPos(((DictLang)spDictLangs.getSelectedItem()).getCode());
    }

    private void setSearchParams(SearchParams searchParams) {
        this.searchParams = searchParams;
        this.isFromQuechua = searchParams.isFromQuechua();
        if(!this.isFromQuechua) {
            this.updateViewsOnLangChange();
        }
        spSearchTypes.setSelection(searchParams.getSearchTypePos());
        int dictLangPos = ((DictLangAdapter)spDictLangs.getAdapter()).getPosByValue(searchParams.getNonQuechuaLangCode());
        spDictLangs.setSelection(Math.max(0, dictLangPos));
        updateQueryHint();
    }

    private void updateViewsOnLangChange() {
        llSearchOptions.removeAllViews();
        List<View> innerViews = Arrays.asList(tvQichwaLang, ivSwapLanguages, spDictLangs);
        if(!isFromQuechua) {
            Collections.reverse(innerViews);
        }
        llSearchOptions.addView(spSearchTypes);
        llSearchOptions.addView(vSeparator);
        for(View view : innerViews) {
            llSearchOptions.addView(view);
        }
        updateQueryHint();
    }

    private void updateQueryHint() {
        if(isFromQuechua) {
            svSearch.setQueryHint(quechuaPlaceholder);
        } else {
            DictLang dictLang = (DictLang) spDictLangs.getSelectedItem();
            svSearch.setQueryHint(placeholders.get(dictLang.getCode()));
        }
    }

    private void onResultsChanged(List<SearchResult> searchResults){
        llResultsArea.setVisibility(View.VISIBLE);
        if(searchResults == null  || searchResults.isEmpty()){
            llNoResults.setVisibility(View.VISIBLE);
            spResults.setVisibility(View.GONE);
            tvResultsTotal.setVisibility(View.GONE);
            rvResults.setVisibility(View.GONE);
            spinnerResultAdapter.setSearchResults(new ArrayList<>());
            resultListAdapter.setDefinitions(new ArrayList<>());
        }else{
            llNoResults.setVisibility(View.GONE);
            spResults.setVisibility(View.VISIBLE);
            tvResultsTotal.setVisibility(View.VISIBLE);
            renderResults(searchResults);
        }
    }

    private void onSearchViewStateChanged(SearchViewState searchViewState){
        if (searchViewState != null) {
            if(searchViewState.isLoading()){
                searchProgressBar.setVisibility(View.VISIBLE);
                llResultsArea.setVisibility(View.GONE);
            }else{
                searchProgressBar.setVisibility(View.GONE);
            }
        }
    }

    private void onFetchExtraDefinitions(List<Definition> definitions){
        if(definitions != null && !definitions.isEmpty()){
            if(resultListAdapter != null){
                int currentLength = resultListAdapter.getItemCount();
                resultListAdapter.pushMoreDefinitions(definitions);
                resultListAdapter.notifyItemRangeInserted(currentLength, definitions.size());
            }
        }
    }

    private void onFetchMoreLoadingChanged(Boolean isLoading){
        fetchMoreProgressBar.setVisibility(isLoading == null || !isLoading ? View.GONE : View.VISIBLE);
    }

    private void onSaveWordResult(Boolean isSuccess){
        int stringId = (isSuccess != null && isSuccess) ? R.string.favorite_added_success : R.string.favorite_added_error;
        Snackbar.make(clSearch, getString(stringId), Snackbar.LENGTH_SHORT).show();
    }

    private void renderResults(List<SearchResult> searchResults) {
        searchType = spSearchTypes.getSelectedItemPosition();
        int previousSelection = spResults.getSelectedItemPosition();

        spinnerResultAdapter.setSearchResults(searchResults);
        spinnerResultAdapter.notifyDataSetChanged();
        spResults.setSelection(0, true);
        if(previousSelection == 0){
            selectResult(Objects.requireNonNull(spinnerResultAdapter.getItem(0)));
        }
        Log.d(getClass().getName(), "set results, position = " + previousSelection);
    }

    private void selectResult(SearchResult result){
        rvResults.scrollToPosition(0);
        scrollListener.resetState();
        rvResults.setVisibility(View.VISIBLE);
        tvResultsTotal.setText(getResources().getQuantityString(R.plurals.total_results, result.getTotal(),
                result.getTotal()));
        resultListAdapter.setDefinitions(result.getDefinitions());
        resultListAdapter.notifyDataSetChanged();
    }

    private void loadPlaceholders(){
        this.placeholders = new HashMap<>();
        this.quechuaPlaceholder = getString(R.string.search_quechua_placeholder);
        this.placeholders.put("qu", quechuaPlaceholder);
        this.placeholders.put("es", getString(R.string.search_spanish_placeholder));
        this.placeholders.put("en", getString(R.string.search_english_placeholder));
        this.placeholders.put("fr", getString(R.string.search_french_placeholder));
        this.placeholders.put("de", getString(R.string.search_german_placeholder));
        this.placeholders.put("it", getString(R.string.search_italian_placeholder));
    }

    private void setAdapters(){
        ArrayAdapter<CharSequence> searchTypesAdapter = ArrayAdapter.createFromResource(this, R.array.searchTypes, R.layout.item_spinner_search_type);
        searchTypesAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown_search_type);
        spSearchTypes.setAdapter(searchTypesAdapter);
        spSearchTypes.setSelection(2);

        spDictLangs.setAdapter(new DictLangAdapter(this));
        spinnerResultAdapter = new SpinnerResultAdapter(this, R.layout.item_spinner_result, new ArrayList<>());
        resultListAdapter = new ResultListAdapter(new ArrayList<>(), this);
        rvResults.setAdapter(resultListAdapter);
        spResults.setAdapter(spinnerResultAdapter);
    }

    private String getCurrentLang(){
        return ((DictLang)spDictLangs.getSelectedItem()).getCode();
    }



    private void setSearchView(){
        svSearch.setQuery(svSearch.getQueryHint(), false);
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(getClass().getName(), "onquerysubmit " + query);
                viewModel.searchWord(isFromQuechua ? 1 : 0, getCurrentLang(), spSearchTypes.getSelectedItemPosition(), query);
                svSearch.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }


}
