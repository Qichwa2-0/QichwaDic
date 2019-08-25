package com.ocram.qichwadic.presentation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.ocram.qichwadic.R;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSkipText(getString(R.string.intro_skip));
        setDoneText(getString(R.string.intro_done));
        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle(getString(R.string.intro_multisearch_title));
        sliderPage1.setDescription(getString(R.string.intro_multisearch_description));
        sliderPage1.setImageDrawable(R.drawable.search3);
        sliderPage1.setBgColor(ContextCompat.getColor(this, R.color.qichwa_showcase_title));
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle(getString(R.string.intro_searchcriteria_title));
        sliderPage2.setDescription(getString(R.string.intro_searchcriteria_description));
        sliderPage2.setImageDrawable(R.drawable.searchtype2);
        sliderPage2.setBgColor(ContextCompat.getColor(this, R.color.qichwa_showcase_title));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle(getString(R.string.intro_offline_title));
        sliderPage3.setDescription(getString(R.string.intro_offline_description));
        sliderPage3.setImageDrawable(R.drawable.dictionaries2);
        sliderPage3.setBgColor(ContextCompat.getColor(this, R.color.qichwa_showcase_title));
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        showSkipButton(true);
        setProgressButtonEnabled(true);
        setFlowAnimation();
    }


    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        goToSearchActivity();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        goToSearchActivity();
    }

    private void goToSearchActivity(){
        Intent intent = new Intent(IntroActivity.this, SearchActivity.class);
        startActivity(intent);
        finish();
    }
}
