package com.ocram.qichwadic.features.intro.ui

import android.content.Intent
import android.os.Bundle


import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.activity.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.concurrent.schedule


class SplashActivity : AppCompatActivity() {

    private val splashViewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        splashViewModel.firstStart.observe(this, Observer { onFirstStartLoaded(it) })
    }

    private fun onFirstStartLoaded (isFirstStart: Boolean) {
        Timer("", false).schedule(1500) {
            val targetCls = if (isFirstStart) IntroActivity::class.java else MainActivity::class.java
            startActivity(Intent(this@SplashActivity, targetCls))
            overridePendingTransition(R.anim.enteranim, R.anim.exitanim)
            this@SplashActivity.finish()
        }

    }
}