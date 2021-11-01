package com.ocram.qichwadic.core.ui.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import com.google.android.material.composethemeadapter.MdcTheme
import com.ocram.qichwadic.features.intro.ui.IntroScreen
import com.ocram.qichwadic.features.intro.ui.SplashScreen
import com.ocram.qichwadic.features.intro.ui.SplashViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.getViewModel


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MdcTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: SplashViewModel = getViewModel()
) {
    var showingSplash by remember { mutableStateOf(true) }
    val isFirstTime = viewModel.firstStart

    var showFirstTime by remember { mutableStateOf(isFirstTime ?: true) }

    LaunchedEffect(key1 = true) {
        delay(1500L)
        showingSplash = false
    }
    Crossfade(targetState = showingSplash) { splash ->
        if (splash) {
            SplashScreen()
        } else {
            if (showFirstTime) {
                IntroScreen(finishIntro = { showFirstTime = false },)
            } else {
                MainAppScreen()
            }
        }
    }
}