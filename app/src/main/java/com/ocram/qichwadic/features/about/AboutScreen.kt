package com.ocram.qichwadic.features.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.ocram.qichwadic.BuildConfig
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.common.AppLogo
import com.ocram.qichwadic.core.ui.common.HtmlText
import com.ocram.qichwadic.core.ui.common.TopBar
import com.ocram.qichwadic.core.ui.theme.textStyleNormal
import com.ocram.qichwadic.core.ui.theme.textStyleSmall
import java.util.*

const val ytChannelUrl = "https://www.youtube.com/channel/UCZ5kIwvo7DlN9qdrQrjUOkg"
const val websiteUrl = "https://www.qichwa.net"
const val appUrl = "market://details?id=${BuildConfig.APPLICATION_ID}"

private data class ActionButtons(@StringRes val strId: Int, val action: () -> Unit)
@Composable
fun AboutScreen(
    onBackPressed: () -> Unit,
    openActionWebview: (uri: String) -> Unit
) {
    val context = LocalContext.current
    val chooserTitle = stringResource(R.string.chooser_title)
    val mailSubject = "${stringResource(R.string.contact_subject)} ${stringResource(R.string.versionName)}"

    val sendContactMail = {
        val intent = Intent(
            Intent.ACTION_SENDTO, Uri.parse("mailto:${BuildConfig.DEV_EMAIL}")
        )
        intent.putExtra(Intent.EXTRA_SUBJECT, mailSubject)
        startActivity(context, Intent.createChooser(intent, chooserTitle), null)
    }

    val openMarketIntent = {
        try {
            startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse(appUrl)), null)
        } catch (e: ActivityNotFoundException) {
            println("Error")
        }
    }

    val actionButtons = listOf(
        ActionButtons(R.string.about_contact, sendContactMail),
        ActionButtons(R.string.about_rate_app, openMarketIntent),
        ActionButtons(R.string.about_visit_qichwa20) { openActionWebview(websiteUrl) },
        ActionButtons(R.string.about_visit_qichwa20_youtube) { openActionWebview(ytChannelUrl) },
    )

    Surface {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopBar(
                title = stringResource(id = R.string.nav_about),
                buttonIcon = Icons.Filled.ArrowBack,
                onButtonClicked = { onBackPressed() }
            )
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(all = 16.dp),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLogo()
                    Text(text = stringResource(id = R.string.author_app_name), style = textStyleNormal)
                    Text(
                        text = stringResource(id = R.string.versionName),
                        style = textStyleSmall,
                        color = MaterialTheme.colors.secondaryVariant
                    )
                    HtmlText(
                        text = stringResource(id = R.string.about_project_collaboration),
                        removeLinksUnderline = true
                    )

                }
                Column(
                    modifier = Modifier.padding(vertical = 8.dp).width(IntrinsicSize.Max),
                ) {
                    actionButtons.forEach {
                        Button(onClick = it.action, Modifier.fillMaxWidth()) {
                            Text(stringResource(it.strId), Modifier.padding(horizontal = 16.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }

                Text(
                    text = stringResource(
                        id = R.string.about_copyright,
                        Calendar.getInstance().get(Calendar.YEAR)
                    ),
                    style = textStyleSmall
                )
            }
        }
    }
}

@Composable
@Preview
fun PreviewAboutScreen() {
    AboutScreen({}) {}
}