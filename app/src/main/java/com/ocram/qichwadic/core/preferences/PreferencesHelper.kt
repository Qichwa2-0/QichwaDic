package com.ocram.qichwadic.core.preferences

import android.content.SharedPreferences

import com.ocram.qichwadic.core.domain.model.SearchParams

class PreferencesHelper(private val sharedPreferences: SharedPreferences)  {

    fun isFirstStart(): Boolean {
        return sharedPreferences.getBoolean(FIRST_START_KEY, true)
    }

    fun lastSearchParams(): SearchParams {
        return SearchParams(
                this.searchTypePos(),
                this.isSearchFromQuechua(),
                this.nonQuechuaLangCode(),
                searchWord = this.searchWord()
        )
    }

    fun isOfflineSearchMode(): Boolean {
        return sharedPreferences.getBoolean(SEARCH_MODE_KEY, false)
    }

    private fun searchTypePos(): Int {
        return sharedPreferences.getInt(SEARCH_PARAM_TYPE_POS_KEY, 2)
    }

    private fun isSearchFromQuechua(): Boolean {
        return sharedPreferences.getBoolean(SEARCH_PARAM_IS_QUECHUA_KEY, true)
    }

    private fun nonQuechuaLangCode(): String {
        return sharedPreferences.getString(SEARCH_PARAM_NON_QUECHUA_CODE_KEY, "es") ?: "es"
    }

    private fun searchWord(): String {
        return sharedPreferences.getString(SEARCH_PARAM_TYPE_WORD_KEY, "") ?: ""
    }

    fun saveNotFirstStart() {
        this.putBoolean(FIRST_START_KEY, false)
    }

    fun saveSearchParams(searchParams: SearchParams) {
        this.saveNonQuechuaLangPos(searchParams.nonQuechuaLangCode)
        this.saveSearchFromQuechua(searchParams.isFromQuechua)
        this.saveSearchType(searchParams.searchTypePos)
        this.saveSearchWord(searchParams.searchWord)
    }

    fun saveNonQuechuaLangPos(targetLangCode: String) {
        this.putString(SEARCH_PARAM_NON_QUECHUA_CODE_KEY, targetLangCode)
    }

    fun saveSearchFromQuechua(fromQuechuaSearch: Boolean) {
        this.putBoolean(SEARCH_PARAM_IS_QUECHUA_KEY, fromQuechuaSearch)
    }

    fun saveSearchType(searchType: Int) {
        this.putInt(SEARCH_PARAM_TYPE_POS_KEY, searchType)
    }

    fun saveSearchWord(searchWord: String) {
        this.putString(SEARCH_PARAM_TYPE_WORD_KEY, searchWord)
    }

    fun saveOfflineSearchMode(offline: Boolean) {
        return this.putBoolean(SEARCH_MODE_KEY, offline)
    }

    private fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    private fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    private fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    companion object {

        private const val FIRST_START_KEY = "pref_firstStart"
        private const val SEARCH_PARAM_NON_QUECHUA_CODE_KEY = "pref_searchParamNonQuechuaLang"
        private const val SEARCH_PARAM_IS_QUECHUA_KEY = "pref_searchParamFromQuechua"
        private const val SEARCH_PARAM_TYPE_POS_KEY = "pref_searchParamType"
        private const val SEARCH_PARAM_TYPE_WORD_KEY = "pref_searchParamWord"
        private const val SEARCH_MODE_KEY = "pref_searchMode"
    }
}
