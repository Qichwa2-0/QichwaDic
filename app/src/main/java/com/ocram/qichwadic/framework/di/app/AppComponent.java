package com.ocram.qichwadic.framework.di.app;

import android.app.Application;
import android.content.Context;

import com.ocram.qichwadic.QichwaDicApplication;
import com.ocram.qichwadic.framework.dao.DictionaryDao;
import com.ocram.qichwadic.framework.dao.FavoriteDao;
import com.ocram.qichwadic.framework.dao.SearchDao;
import com.ocram.qichwadic.framework.net.client.RetrofitService;
import com.ocram.qichwadic.framework.preferences.PreferencesHelper;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { AppModule.class })
public interface AppComponent {

    void inject(QichwaDicApplication qichwaDicApplication);

    @ApplicationContext
    Context getContext();

    Application getApplication();

    PreferencesHelper getPreferencesHelper();

    SearchDao getSearchDao();

    FavoriteDao getFavoriteDao();

    DictionaryDao getDictionaryDao();

    RetrofitService getRetrofitService();
}
