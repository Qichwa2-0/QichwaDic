package com.ocram.qichwadic.framework.preferences;

import android.content.SharedPreferences;

import com.ocram.qichwadic.presentation.viewmodel.viewstate.SearchParams;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class PreferencesHelper {

    private static final String FIRST_START_KEY= "pref_firstStart";
    private static final String SEARCH_PARAM_NON_QUECHUA_CODE_KEY = "pref_searchParamNonQuechuaLang";
    private static final String SEARCH_PARAM_IS_QUECHUA_KEY= "pref_searchParamFromQuechua";
    private static final String SEARCH_PARAM_TYPE_POS_KEY= "pref_searchParamType";
    private static final String SEARCH_MODE_KEY= "pref_searchMode";

    private SharedPreferences sharedPreferences;

    @Inject
    public PreferencesHelper(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public Flowable<Boolean> isFirstStart(){
        return Flowable.<Boolean>fromPublisher(
                e -> e.onNext(sharedPreferences.getBoolean(FIRST_START_KEY, true))
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public void saveFirstStart(){
        Completable.fromAction(() -> this.putBoolean(FIRST_START_KEY, false)
        ).subscribeOn(Schedulers.io()).subscribe();
    }

    public void saveSearchParams(String targetLangCode, boolean fromQuechua, int searchTypePos) {
        Completable.fromAction(() -> {
            this.putString(SEARCH_PARAM_NON_QUECHUA_CODE_KEY, targetLangCode);
            this.putBoolean(SEARCH_PARAM_IS_QUECHUA_KEY, fromQuechua);
            this.putInt(SEARCH_PARAM_TYPE_POS_KEY, searchTypePos);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public void saveNonQuechuaLangPos(String targetLangCode){
        Completable.fromAction(() ->
                this.putString(SEARCH_PARAM_NON_QUECHUA_CODE_KEY, targetLangCode)
        ).subscribeOn(Schedulers.io()).subscribe();
    }

    public void saveSearchFromQuechua(boolean fromQuechuaSearch){
        Completable.fromAction(() ->
                this.putBoolean(SEARCH_PARAM_IS_QUECHUA_KEY, fromQuechuaSearch)
        ).subscribeOn(Schedulers.io()).subscribe();
    }

    public Flowable<SearchParams> getLastSearchParams() {
        return Flowable.<SearchParams>fromPublisher(e -> {
            SearchParams searchParams = new SearchParams(
                    this.getSearchTypePos(),
                    this.isSearchFromQuechua(),
                    this.getNonQuechuaLangCode()
            );
            e.onNext(searchParams);
            e.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable saveOfflineSearchMode(boolean offline) {
        return Completable.fromAction(() ->
                this.putBoolean(SEARCH_MODE_KEY, offline)
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<Boolean> getSearchMode() {
        return Flowable.<Boolean>fromPublisher(
                e -> e.onNext(sharedPreferences.getBoolean(SEARCH_MODE_KEY, false)
        )).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private int getSearchTypePos() {
        return sharedPreferences.getInt(SEARCH_PARAM_TYPE_POS_KEY, 2);
    }

    private boolean isSearchFromQuechua() {
        return sharedPreferences.getBoolean(SEARCH_PARAM_IS_QUECHUA_KEY, true);
    }

    private String getNonQuechuaLangCode() {
        return sharedPreferences.getString(SEARCH_PARAM_NON_QUECHUA_CODE_KEY, "es");
    }

    private void putBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    private void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    private void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }
}
