package com.ocram.qichwadic.features.intro.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.common.AppLogo
import com.ocram.qichwadic.core.ui.theme.textStyleSmall

@Composable
fun SplashScreen() {
    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize()
        ) {
            AppLogo()
            Text(
                text = stringResource(id = R.string.versionName),
                style = textStyleSmall
            )
        }
    }

}

@Composable
@Preview
fun PreviewSplashScreen() {
    SplashScreen()
}