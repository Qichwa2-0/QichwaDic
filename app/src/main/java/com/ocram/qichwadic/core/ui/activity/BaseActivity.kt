package com.ocram.qichwadic.core.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import com.ocram.qichwadic.BuildConfig
import com.ocram.qichwadic.R


abstract class BaseActivity : AppCompatActivity() {

    protected abstract val layoutId: Int
    protected abstract fun getToolbar(): Toolbar?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUGGABLE) {
            this.activateStrictMode()
        }
        setContentView(layoutId)
        setupToolbar()
        initViews()
    }

    protected abstract fun initViews()

    protected fun activateStrictMode() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build())
    }

    protected fun setTitle(title: String) {
        if (supportActionBar != null) {
            supportActionBar!!.title = title
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(getToolbar())
        if(supportActionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    protected fun openEmailIntent(toEmail: String, subject: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$toEmail"))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        startActivity(Intent.createChooser(intent, getString(R.string.chooser_title)))
    }

    protected fun openActionViewIntent(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(uri)
        startActivity(intent)
    }

    protected fun openFbPageIntent(fbPageId: String) {
        var intent: Intent
        try {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/$fbPageId"))
        } catch (e: Exception) {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/$fbPageId"))
        }

        startActivity(intent)
    }

    protected fun openShareIntent(textToShare: String) {
        val TEXT_TYPE = "text/plain"
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare)
        sendIntent.type = TEXT_TYPE
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_with)))
    }
}
