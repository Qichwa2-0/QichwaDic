package com.ocram.qichwadic.features.intro.ui

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import com.ocram.qichwadic.R
import com.ocram.qichwadic.features.search.ui.SearchActivity

class IntroActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSkipText(getString(R.string.intro_skip))
        setDoneText(getString(R.string.intro_done))
        val sliderPage1 = SliderPage()
        sliderPage1.title = getString(R.string.intro_multisearch_title)
        sliderPage1.description = getString(R.string.intro_multisearch_description)
        sliderPage1.imageDrawable = R.drawable.search
        sliderPage1.bgColor = ContextCompat.getColor(this, R.color.qichwa_showcase_title)
        addSlide(AppIntroFragment.newInstance(sliderPage1))

        val sliderPage2 = SliderPage()
        sliderPage2.title = getString(R.string.intro_searchcriteria_title)
        sliderPage2.description = getString(R.string.intro_searchcriteria_description)
        sliderPage2.imageDrawable = R.drawable.searchtype2
        sliderPage2.bgColor = ContextCompat.getColor(this, R.color.qichwa_showcase_title)
        addSlide(AppIntroFragment.newInstance(sliderPage2))

        val sliderPage3 = SliderPage()
        sliderPage3.title = getString(R.string.intro_offline_title)
        sliderPage3.description = getString(R.string.intro_offline_description)
        sliderPage3.imageDrawable = R.drawable.dictionaries2
        sliderPage3.bgColor = ContextCompat.getColor(this, R.color.qichwa_showcase_title)
        addSlide(AppIntroFragment.newInstance(sliderPage3))

        showSkipButton(true)
        isProgressButtonEnabled = true
        setFlowAnimation()
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
        val intent = Intent(this@IntroActivity, SearchActivity::class.java)
        startActivity(intent)
        finish()
    }
}
