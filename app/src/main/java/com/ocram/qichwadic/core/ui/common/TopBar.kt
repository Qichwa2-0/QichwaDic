package com.ocram.qichwadic.core.ui.common

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.ocram.qichwadic.R

@Composable
fun TopBar(
    title: String = stringResource(id = R.string.app_name),
    buttonIcon: ImageVector = Icons.Filled.ArrowBack,
    onButtonClicked: () -> Unit,
    actions: @Composable () -> Unit = {}
) {
    TopAppBar (
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(onClick = onButtonClicked) {
                Icon(buttonIcon, contentDescription = "")
            }
        },
        actions = { actions() }
    )
}