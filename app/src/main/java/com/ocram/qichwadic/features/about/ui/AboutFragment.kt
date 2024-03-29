package com.ocram.qichwadic.features.about.ui


import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.ocram.qichwadic.BuildConfig

import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.activity.MainActivity
import com.ocram.qichwadic.databinding.FragmentAboutBinding
import java.util.*

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null;
    private val binding get() = _binding!!

    private fun TextView.removeLinksUnderline() {
        val spannable = SpannableString(text)
        for (u in spannable.getSpans(0, spannable.length, URLSpan::class.java)) {
            spannable.setSpan(object : URLSpan(u.url) {
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            }, spannable.getSpanStart(u), spannable.getSpanEnd(u), 0)
        }
        text = spannable
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()

        val toolbar = view.findViewById<Toolbar>(R.id.mToolbar)
        val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)

        val appBarConfiguration = AppBarConfiguration(navController.graph, drawer)

        toolbar.setupWithNavController(navController, appBarConfiguration)

        binding.tvProjectCollab.text = HtmlCompat.fromHtml(getString(R.string.about_project_collaboration), HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.tvProjectCollab.removeLinksUnderline()
        binding.tvProjectCollab.movementMethod = LinkMovementMethod.getInstance()

        binding.tvCopyright.text = getString(R.string.about_copyright, Calendar.getInstance().get(Calendar.YEAR))
        bindEvents()
    }

    private fun bindEvents() {
        binding.btnRateApp.setOnClickListener {
            (requireActivity() as MainActivity).openMarketIntent()
        }

        val qichwaSiteUrl = "https://www.qichwa.net"
        binding.btnWebsite.setOnClickListener { (requireActivity() as MainActivity).openActionViewIntent(qichwaSiteUrl) }

        binding.btnContact.setOnClickListener {
            (requireActivity() as MainActivity)
                    .openEmailIntent(
                            BuildConfig.DEV_EMAIL,
                            "${getString(R.string.contact_subject)} ${getString(R.string.versionName)}"
                    )
        }

        binding.btnYoutube.setOnClickListener {
            val qichwa20YtUrl = "https://www.youtube.com/channel/UCZ5kIwvo7DlN9qdrQrjUOkg"
            (requireActivity() as MainActivity)
                    .openActionViewIntent(qichwa20YtUrl)
        }
    }
}
