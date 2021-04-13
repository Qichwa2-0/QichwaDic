package com.ocram.qichwadic.features.intro.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import com.github.appintro.AppIntroPageTransformerType
import com.ocram.qichwadic.R
import com.ocram.qichwadic.core.ui.activity.MainActivity

class IntroActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isSkipButtonEnabled = true

        setTransformer(AppIntroPageTransformerType.Depth)

        setSkipText(getString(R.string.intro_skip))
        setDoneText(getString(R.string.intro_done))

        addSlide(AppIntroFragment.newInstance(
                title = getString(R.string.intro_multisearch_title),
                description = getString(R.string.intro_multisearch_description),
                imageDrawable = R.drawable.search,
                backgroundDrawable = R.drawable.intro_bg_gradient
        ))

        addSlide(AppIntroFragment.newInstance(
                title = getString(R.string.intro_searchcriteria_title),
                description = getString(R.string.intro_searchcriteria_description),
                imageDrawable = R.drawable.searchtype2,
                backgroundDrawable = R.drawable.intro_bg_gradient
        ))

        addSlide(AppIntroFragment.newInstance(
                title = getString(R.string.intro_offline_title),
                description = getString(R.string.intro_offline_description),
                imageDrawable = R.drawable.dictionaries2,
                backgroundDrawable = R.drawable.intro_bg_gradient
        ))

    }


    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        goToSearchActivity()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        goToSearchActivity()
    }

    private fun goToSearchActivity() {
        val intent = Intent(this@IntroActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}