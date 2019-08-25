package com.ocram.qichwadic;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;

import com.ocram.qichwadic.framework.di.app.AppComponent;
import com.ocram.qichwadic.framework.di.app.AppModule;
import com.ocram.qichwadic.framework.di.app.DaggerAppComponent;
import com.ocram.qichwadic.framework.preferences.PreferencesHelper;
import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;

public class QichwaDicApplication extends Application {

    protected AppComponent appComponent;

    @Inject
    PreferencesHelper preferencesHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        if(LeakCanary.isInAnalyzerProcess(this)){
            return;
        }
        LeakCanary.install(this);

        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        appComponent.inject(this);
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public static QichwaDicApplication get(AppCompatActivity activity){
        return (QichwaDicApplication) activity.getApplication();
    }
}
