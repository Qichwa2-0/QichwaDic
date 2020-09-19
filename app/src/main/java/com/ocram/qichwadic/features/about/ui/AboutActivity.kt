package com.ocram.qichwadic.features.about.ui

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar

import androidx.core.text.HtmlCompat

import com.ocram.qichwadic.BuildConfig
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.activity.BaseActivity

import kotlinx.android.synthetic.main.activity_about.*
import java.util.*

class AboutActivity : BaseActivity() {

    override val layoutId = R.layout.activity_about

    override fun getToolbar(): Toolbar? {
        return mToolbar as Toolbar?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindEvents()
        tvProjectCollab.text = HtmlCompat.fromHtml(getString(R.string.about_project_collaboration), HtmlCompat.FROM_HTML_MODE_LEGACY)
        tvProjectCollab.movementMethod = LinkMovementMethod.getInstance()
        tvCopyright.text = getString(R.string.about_copyright, Calendar.getInstance().get(Calendar.YEAR))
    }

    private fun bindEvents() {
        ivQichwa20Fb.setOnClickListener { openFbPageIntent(QICHWA_FB_ID) }

        listOf(ivQichwa20Fb, tvWebsite, tvRateApp).forEach {
            it.setOnClickListener { view -> openWebItem(view) }
        }

        ivQichwa20Mail.setOnClickListener { openMailSender() }
    }

    private fun openWebItem(v: View) {
        val uri = when (v.id) {
            R.id.ivQichwa20Yt -> QICHWA_YT_URL
            R.id.tvRateApp -> QICHWA_ANDROID_MARKET_URL
            else -> QICHWA_SITE_URL
        }
        openActionViewIntent(uri)
    }

    private fun openMailSender() {
        openEmailIntent(BuildConfig.DEV_EMAIL, getString(R.string.contact_subject))
    }

    override fun initViews() {
        setTitle(getString(R.string.author_app_name))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val QICHWA_FB_ID = "556776684487945"
        private const val QICHWA_YT_URL = "https://www.youtube.com/channel/UCZ5kIwvo7DlN9qdrQrjUOkg"
        private const val QICHWA_ANDROID_MARKET_URL = "market://details?id=com.ocram.qichwadic"
        private const val QICHWA_SITE_URL = "https://www.dic.qichwa.net"
    }
}