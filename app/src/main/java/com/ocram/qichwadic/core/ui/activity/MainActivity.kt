package com.ocram.qichwadic.core.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.ocram.qichwadic.BuildConfig
import com.ocram.qichwadic.R


class MainActivity : AppCompatActivity() {

    private val marketUrl = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_graph)

        val navView = findViewById<NavigationView>(R.id.nav_view)
        val shareItem = navView.menu.findItem(R.id.shareApp)
        shareItem.setOnMenuItemClickListener {
            openShareIntent(getString(R.string.share_app_message, marketUrl))
            true
        }
        navView.setupWithNavController(navController)

        findViewById<DrawerLayout>(R.id.drawer_layout).addDrawerListener(object: DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                hideSoftKeyboard()
            }
        })
    }

    private fun hideSoftKeyboard() {
        val inputMethodManager: InputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        this.currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun openEmailIntent(toEmail: String, subject: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$toEmail"))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        startActivity(Intent.createChooser(intent, getString(R.string.chooser_title)))
    }

    fun openActionViewIntent(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(uri)
        startActivity(intent)
    }

    fun openMarketIntent() {
        val appUrl = "market://details?id=${BuildConfig.APPLICATION_ID}"
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse(appUrl))
        if(intent.resolveActivity(packageManager) == null) {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse(marketUrl))
        }
        startActivity(intent)
    }

    fun openShareIntent(textToShare: String, subject: String? = null) {
        val TEXT_TYPE = "text/plain"
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare)
        sendIntent.type = TEXT_TYPE
        subject?.let { sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject) }
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_with)))
    }

}
