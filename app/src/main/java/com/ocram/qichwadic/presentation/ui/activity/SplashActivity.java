package com.ocram.qichwadic.presentation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


import androidx.appcompat.app.AppCompatActivity;

import com.ocram.qichwadic.QichwaDicApplication;
import com.ocram.qichwadic.R;
import com.ocram.qichwadic.framework.preferences.PreferencesHelper;


import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class SplashActivity extends AppCompatActivity {

    private CompositeDisposable compositeDisposable;

    @Inject
    PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferencesHelper = QichwaDicApplication.get(this).getAppComponent().getPreferencesHelper();
        super.onCreate(savedInstanceState);
        this.compositeDisposable = new CompositeDisposable();
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Thread t = new Thread(() -> {
            compositeDisposable.add(
                    preferencesHelper.isFirstStart().subscribe(isFirstStart -> {
                        final Intent intent;
                        if(isFirstStart){
                            intent = new Intent(SplashActivity.this, IntroActivity.class);
                            preferencesHelper.saveFirstStart();
                        }else{
                            intent = new Intent(SplashActivity.this, SearchActivity.class);
                        }
                        runOnUiThread(() -> {
                            startActivity(intent);
                            overridePendingTransition(R.anim.enteranim, R.anim.exitanim);
                            SplashActivity.this.finish();
                        });
                    })
            );
        });
        new Handler().postDelayed(t::start, 1500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(this.compositeDisposable != null) {
            this.compositeDisposable.dispose();
        }
    }
}