package com.ocram.qichwadic.core.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.theme.textStyleNormal
import com.ocram.qichwadic.core.ui.theme.textStyleSmall

sealed class DrawerItem(
    val title: String,
    val route: String,
    val buttonIcon: ImageVector? = null,
    @DrawableRes val drawableId: Int? = null,
) {
    object Splash
        : DrawerItem("Splash", "splash")
    object Home
        : DrawerItem("Home", "home", Icons.Filled.Home)
    object Dictionaries
        : DrawerItem("Dictionaries", "dictionaries", drawableId = R.drawable.ic_action_book)
    object Favorites
        : DrawerItem("Favorites", "favorites", Icons.Filled.Favorite)
    object Divider
        : DrawerItem("", "")
    object Help
        : DrawerItem("Help", "help", drawableId = R.drawable.ic_help_white_18dp)
    object Share
        : DrawerItem("Share", "share", Icons.Filled.Share)
    object About
        : DrawerItem("About", "about", Icons.Filled.Info)
}

private val drawerItems = listOf(
    DrawerItem.Dictionaries,
    DrawerItem.Favorites,
    DrawerItem.Divider,
    DrawerItem.Help,
    DrawerItem.Share,
    DrawerItem.About
)

@Composable
fun Drawer(onDestinationClicked: (route: DrawerItem) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .align(CenterHorizontally)
                .background(
                    Brush.linearGradient(
                        listOf(
                            colorResource(id = R.color.primary_color),
                            colorResource(id = android.R.color.holo_purple)
                        ),
                        /*TODO angle 45*/
                    )
                ),
        ) {
            Image(
                modifier = Modifier
                    .align(Center)
                    .padding(8.dp),
                painter = painterResource(id = R.drawable.logo_q_scaled),
                contentDescription = "App Icon",
                )
        }
        Column(
            Modifier
                .padding(start = 8.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())) {
            drawerItems.forEach { item ->
                if (item == DrawerItem.Divider) {
                    Divider(
                        color = Color.Gray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()

                            .clickable { onDestinationClicked(item) },
                        verticalAlignment = CenterVertically,
                    ) {
                        IconButton(
                            onClick = { onDestinationClicked(item) },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            item.drawableId?.let {
                                Icon(
                                    painterResource(item.drawableId),
                                    contentDescription = "",
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Gray
                                )
                            } ?: Icon(
                                item.buttonIcon ?: Icons.Filled.Info,
                                contentDescription = "",
                                tint = Color.Gray
                            )

                        }
                        Text(
                            text = item.title,
                            color = Color.Black,
                            fontSize = textStyleNormal.fontSize,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

    }
}

@Composable
@Preview
fun DrawerPreview() {
    Drawer {}
}