package com.ocram.qichwadic.framework.di.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.ocram.qichwadic.domain.model.Definition;
import com.ocram.qichwadic.domain.model.Dictionary;
import com.ocram.qichwadic.domain.model.SearchResult;
import com.ocram.qichwadic.framework.dao.AppDatabase;
import com.ocram.qichwadic.framework.dao.DictionaryDao;
import com.ocram.qichwadic.framework.dao.FavoriteDao;
import com.ocram.qichwadic.framework.dao.SearchDao;
import com.ocram.qichwadic.framework.net.client.RetrofitService;

import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {

    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return application;
    }

    @Provides
    Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        final String APP_PREF = "QICHWADIC";
        return application.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    AppDatabase provideAppDatabase() {
        return Room.databaseBuilder(application.getApplicationContext(), AppDatabase.class, "simitaqidb")
                .fallbackToDestructiveMigration()
                .addMigrations(AppDatabase.MIGRATION_1_2)
                .addMigrations(AppDatabase.MIGRATION_2_3)
                .addMigrations(AppDatabase.MIGRATION_3_4)
                .build();
    }

    @Provides
    @Singleton
    DictionaryDao provideDictionaryDao(AppDatabase appDatabase) {
        return appDatabase.getDictionaryDao();
    }

    @Provides
    @Singleton
    SearchDao provideSearchDao(AppDatabase appDatabase) {
        return appDatabase.getDefinitionDao();
    }

    @Provides
    @Singleton
    FavoriteDao provideFavoriteDao(AppDatabase appDatabase) {
        return appDatabase.getFavoriteDao();
    }

    @Provides
    @Singleton
    RetrofitService provideRetrofitService(Retrofit retrofit) {
        return retrofit.create(RetrofitService.class);
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(Gson gson) {
        return new Retrofit.Builder().baseUrl(RetrofitService.API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<Dictionary>>() {}.getType(), BaseDeserializer.<Dictionary>getDeserializer())
                .registerTypeAdapter(new TypeToken<List<Definition>>(){}.getType(), BaseDeserializer.<Definition>getDefinitionsDeserializer())
                .registerTypeAdapter(new TypeToken<List<SearchResult>>(){}.getType(), BaseDeserializer.<SearchResult>getDefinitionsDeserializer())
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    private static class BaseDeserializer {

        static <T> JsonDeserializer<T> getDeserializer() {
            return (json, typeOfT, context) -> {
                JsonElement array = json.getAsJsonArray();
                return new Gson().fromJson(array, typeOfT);
            };
        }

        static <T> JsonDeserializer<T> getDefinitionsDeserializer() {
            return (json, typeOfT, context) -> {
                JsonElement element =
                        json.isJsonArray()
                                ? json.getAsJsonArray()
                                : json.getAsJsonObject().getAsJsonArray("definitions");
                return new Gson().fromJson(element, typeOfT);
            };
        }
    }

}
