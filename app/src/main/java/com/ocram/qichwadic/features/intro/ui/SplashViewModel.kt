package com.ocram.qichwadic.features.intro.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ocram.qichwadic.core.preferences.PreferencesHelper
import kotlinx.coroutines.launch

class SplashViewModel(private val sharedPreferencesHelper: PreferencesHelper) : ViewModel() {

    var firstStart by mutableStateOf<Boolean?>(null)
        private set

    init {
        loadFirstStart()
    }

    private fun loadFirstStart () {
        viewModelScope.launch {
            firstStart = sharedPreferencesHelper.isFirstStart()
            firstStart?.let {
                if(it) {
                    sharedPreferencesHelper.saveNotFirstStart()
                }
            }
        }
    }
}