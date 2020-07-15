package com.ocram.qichwadic.features.intro.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ocram.qichwadic.core.preferences.PreferencesHelper
import kotlinx.coroutines.launch

class SplashViewModel(private val sharedPreferencesHelper: PreferencesHelper) : ViewModel() {

    var firstStart: MutableLiveData<Boolean> = MutableLiveData()

    init {
        loadFirstStart()
    }

    private fun loadFirstStart () {
        viewModelScope.launch {
            val prefFirstStart = sharedPreferencesHelper.isFirstStart()
            firstStart.postValue(prefFirstStart)

            if(prefFirstStart) {
                sharedPreferencesHelper.saveNotFirstStart()
            }
        }
    }
}