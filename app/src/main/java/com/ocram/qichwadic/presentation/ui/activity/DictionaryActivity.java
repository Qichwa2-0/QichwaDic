package com.ocram.qichwadic.presentation.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.ocram.qichwadic.R;
import com.ocram.qichwadic.domain.model.Dictionary;
import com.ocram.qichwadic.presentation.ui.adapters.DictionaryAdapter;
import com.ocram.qichwadic.presentation.model.DictLang;
import com.ocram.qichwadic.presentation.viewmodel.DictionaryViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnItemSelected;

public class DictionaryActivity extends BaseActivity implements  DictionaryAdapter.DefinitionDownloadListener {

    @BindView(R.id.clDictionaries) CoordinatorLayout clDictionaries;
    @BindView(R.id.rvDictionaries) RecyclerView rvDictionaries;
    @BindView(R.id.pbDictionariesLoading) ProgressBar pbDictionariesLoading;
    @BindView(R.id.tvNoDictionaries) TextView tvNoDictionaries;
    @BindView(R.id.spTargetLanguages) Spinner spTargetLanguages;
    private DictionaryAdapter dictionaryAdapter;
    private DictionaryViewModel viewModel;
    private Map<String, List<Dictionary>> dictLangMap;
    private List<DictLang> dictLangs;
    private String currentLangCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] codeTextLangs = getResources().getStringArray(R.array.dictLangs);
        this.dictLangs = new ArrayList<>();
        for(String codeTextLang : codeTextLangs) {
            this.dictLangs.add(new DictLang(codeTextLang));
        }
        setRecyclerView();
        setSpinnerAdapters();
        viewModel = ViewModelProviders.of(this).get(DictionaryViewModel.class);
        viewModel.getDictionariesByLang().observe(this, this::onDictionaryListChanged);
        viewModel.getNotifyMessage().observe(this, this::onToastMessageChanged);
        viewModel.getLocalLoading().observe(this, this::onLocalDictionariesLoadingChanged);
        viewModel.getDicActionFailedPosition().observe(this, this::onDicActionFailed);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dictionary;
    }

    @Override
    protected void initViews() {
        setTitle(getString(R.string.nav_dictionaries));
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void downloadDefinitions(int pos, Dictionary dictionary) {
        viewModel.downloadDictionary(pos, dictionary);
    }

    @Override
    public void removeDictionary(int pos, Dictionary dictionary) {
        viewModel.removeDictionary(pos, dictionary);
    }

    @OnItemSelected(R.id.spTargetLanguages)
    void onItemSelected(int pos) {
        Log.d(getClass().getName(), "item selected " + pos);
        DictLang dictLang = (DictLang) spTargetLanguages.getItemAtPosition(pos);
        this.currentLangCode = dictLang.getCode();
        dictionaryAdapter.setDictionaries(dictLangMap.get(dictLang.getCode()));
        dictionaryAdapter.notifyDataSetChanged();
    }

    private void onDictionaryListChanged(Map<String, List<Dictionary>> dictionariesMap){
        this.dictLangMap = dictionariesMap;
        if(dictionariesMap == null || dictionariesMap.isEmpty()) {
            showNoDictionariesMessage();
        } else {
            String code = (currentLangCode != null) ? currentLangCode : dictLangs.get(0).getCode();
            showDictionaries(dictionariesMap.get(code));
            spTargetLanguages.setVisibility(View.VISIBLE);
        }
    }

    private void showNoDictionariesMessage() {
        spTargetLanguages.setVisibility(View.GONE);
        tvNoDictionaries.setVisibility(View.VISIBLE);
        rvDictionaries.setVisibility(View.GONE);
    }

    private void showDictionaries(List<Dictionary> dictionaries) {
        dictionaryAdapter.setDictionaries(dictionaries);
        dictionaryAdapter.notifyDataSetChanged();
        spTargetLanguages.setVisibility(View.VISIBLE);
        tvNoDictionaries.setVisibility(View.GONE);
        rvDictionaries.setVisibility(View.VISIBLE);
    }

    private void onToastMessageChanged(String message){
        if(message != null && !message.isEmpty()){
            Snackbar.make(clDictionaries, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void onLocalDictionariesLoadingChanged(Boolean isLoading){
        if(isLoading != null){
            pbDictionariesLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void onDicActionFailed(int position){
        if(dictionaryAdapter.getItemCount() > 0 && position < dictionaryAdapter.getItemCount()){
            dictionaryAdapter.getDictionaries().get(position).setDownloading(false);
            dictionaryAdapter.notifyItemChanged(position);
        }
    }

    private void setRecyclerView(){
        dictionaryAdapter = new DictionaryAdapter(this, new ArrayList<>(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvDictionaries.setLayoutManager(linearLayoutManager);
        rvDictionaries.setNestedScrollingEnabled(false);
        rvDictionaries.setAdapter(dictionaryAdapter);
    }

    private void setSpinnerAdapters(){
        ArrayAdapter<DictLang> langAdapter = new ArrayAdapter<>(this, R.layout.item_spinner_dict_lang_white, dictLangs);
        langAdapter.setDropDownViewResource(R.layout.item_spinner_dict_lang_white);
        spTargetLanguages.setAdapter(langAdapter);
        spTargetLanguages.setSelection(0);
    }
}
