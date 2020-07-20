package com.ocram.qichwadic.core.preferences

import android.content.SharedPreferences

import com.ocram.qichwadic.core.ui.SearchParams

class PreferencesHelper(private val sharedPreferences: SharedPreferences)  {

    fun isFirstStart(): Boolean {
        return sharedPreferences.getBoolean(FIRST_START_KEY, true)
    }

    fun lastSearchParams(): SearchParams {
        return SearchParams(
                this.searchTypePos(),
                this.isSearchFromQuechua(),
                this.nonQuechuaLangCode()
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
        return sharedPreferences.getString(SEARCH_PARAM_NON_QUECHUA_CODE_KEY, "es")!!
    }

    fun saveNotFirstStart() {
        this.putBoolean(FIRST_START_KEY, false)
    }

    fun saveSearchParams(targetLangCode: String, fromQuechua: Boolean, searchTypePos: Int) {
        this.putString(SEARCH_PARAM_NON_QUECHUA_CODE_KEY, targetLangCode)
        this.putBoolean(SEARCH_PARAM_IS_QUECHUA_KEY, fromQuechua)
        this.putInt(SEARCH_PARAM_TYPE_POS_KEY, searchTypePos)
    }

    fun saveNonQuechuaLangPos(targetLangCode: String) {
        this.putString(SEARCH_PARAM_NON_QUECHUA_CODE_KEY, targetLangCode)
    }

    fun saveSearchFromQuechua(fromQuechuaSearch: Boolean) {
        this.putBoolean(SEARCH_PARAM_IS_QUECHUA_KEY, fromQuechuaSearch)
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

        private val FIRST_START_KEY = "pref_firstStart"
        private val SEARCH_PARAM_NON_QUECHUA_CODE_KEY = "pref_searchParamNonQuechuaLang"
        private val SEARCH_PARAM_IS_QUECHUA_KEY = "pref_searchParamFromQuechua"
        private val SEARCH_PARAM_TYPE_POS_KEY = "pref_searchParamType"
        private val SEARCH_MODE_KEY = "pref_searchMode"
    }
}
