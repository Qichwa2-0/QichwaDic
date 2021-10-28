package com.ocram.qichwadic.features.intro.ui

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.theme.textStyleSmall
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isFirstStart: Boolean? = false,
    goToMainScreen: () -> Unit
) {
    val context = LocalContext.current

    isFirstStart?.let { firstStart ->
        LaunchedEffect(key1 = true) {
            delay(1500L)
            goToMainScreen()
            if (firstStart) {
                context.startActivity(Intent(context, IntroActivity::class.java))
            }
        }

    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_q_scaled),
            contentDescription = stringResource(id = R.string.app_name),
            Modifier.wrapContentSize()
        )
        Text(
            text = stringResource(id = R.string.versionName),
            style = textStyleSmall
        )
    }
}

@Composable
@Preview
fun PreviewSplashScreen() {
    SplashScreen {}
}