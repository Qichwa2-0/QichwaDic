package com.ocram.qichwadic.core.di

import android.content.Context
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.ocram.qichwadic.core.data.model.DefinitionEntity
import com.ocram.qichwadic.core.data.model.DictionaryEntity
import com.ocram.qichwadic.core.data.local.dao.AppDatabase
import com.ocram.qichwadic.core.data.remote.RetrofitClient
import com.ocram.qichwadic.core.data.remote.RetrofitService
import com.ocram.qichwadic.core.preferences.PreferencesHelper
import com.ocram.qichwadic.core.data.model.SearchResultEntity
import com.ocram.qichwadic.features.dictionaries.data.datastore.DictionaryCloudDataStore
import com.ocram.qichwadic.features.dictionaries.data.datastore.DictionaryCloudDataStoreImpl
import com.ocram.qichwadic.features.dictionaries.data.datastore.DictionaryLocalDataStore
import com.ocram.qichwadic.features.dictionaries.data.datastore.DictionaryLocalDataStoreImpl
import com.ocram.qichwadic.features.dictionaries.data.repository.DefaultDictionaryRepository
import com.ocram.qichwadic.features.dictionaries.domain.DictionaryInteractor
import com.ocram.qichwadic.features.dictionaries.domain.DictionaryInteractorImpl
import com.ocram.qichwadic.features.dictionaries.domain.DictionaryRepository
import com.ocram.qichwadic.features.dictionaries.ui.DictionaryViewModel
import com.ocram.qichwadic.features.favorites.data.repository.DefaultFavoriteRepository
import com.ocram.qichwadic.features.favorites.domain.FavoriteInteractor
import com.ocram.qichwadic.features.favorites.domain.FavoriteInteractorImpl
import com.ocram.qichwadic.features.favorites.domain.FavoriteRepository
import com.ocram.qichwadic.features.favorites.ui.FavoriteViewModel
import com.ocram.qichwadic.features.intro.ui.SplashViewModel
import com.ocram.qichwadic.features.search.data.datastore.SearchCloudDataStore
import com.ocram.qichwadic.features.search.data.datastore.SearchCloudDataStoreImpl
import com.ocram.qichwadic.features.search.data.datastore.SearchLocalDataStore
import com.ocram.qichwadic.features.search.data.datastore.SearchLocalDataStoreImpl
import com.ocram.qichwadic.features.search.data.repository.SearchRepositoryImpl
import com.ocram.qichwadic.features.search.domain.SearchInteractor
import com.ocram.qichwadic.features.search.domain.SearchInteractorImpl
import com.ocram.qichwadic.features.search.domain.repository.SearchRepository
import com.ocram.qichwadic.features.search.ui.SearchViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type


val appModule = module {

    single {
        AppDatabase.getDatabase(androidApplication())
    }

    single {
        PreferencesHelper(androidApplication().getSharedPreferences("QICHWADIC", Context.MODE_PRIVATE))
    }

    single {
        fun <T> getListDeserializer(): JsonDeserializer<T> {
            return JsonDeserializer { json: JsonElement, typeOfT: Type?, _: JsonDeserializationContext? ->
                Gson().fromJson(json.asJsonArray, typeOfT)
            }
        }

        fun <T> getObjectDeserializer(): JsonDeserializer<T> {
            return JsonDeserializer { json: JsonElement, typeOfT: Type?, _: JsonDeserializationContext? ->
                Gson().fromJson(json.asJsonObject, typeOfT)
            }
        }


        GsonBuilder()
                .registerTypeAdapter(object: TypeToken<List<DictionaryEntity>>(){}.type, getListDeserializer<List<DictionaryEntity>>())
                .registerTypeAdapter(object: TypeToken<List<DefinitionEntity>>(){}.type, getObjectDeserializer<List<DefinitionEntity>>())
                .registerTypeAdapter(object: TypeToken<List<SearchResultEntity>>(){}.type,getObjectDeserializer<List<SearchResultEntity>>())
                .create()
    }

    single {
        fun buildRetrofit(): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(RetrofitService.API_URL)
                    .addConverterFactory(GsonConverterFactory.create(get()))
                    .build()
        }

        RetrofitClient(buildRetrofit().create(RetrofitService::class.java))
    }

    single { get<AppDatabase>().dictionaryDao() }
    single { get<AppDatabase>().definitionDao() }
    single { get<AppDatabase>().favoriteDao() }

    single<DictionaryLocalDataStore> { DictionaryLocalDataStoreImpl(get()) }
    single<SearchLocalDataStore> { SearchLocalDataStoreImpl(get()) }
    single<DictionaryCloudDataStore> { DictionaryCloudDataStoreImpl(get()) }
    single<SearchCloudDataStore> { SearchCloudDataStoreImpl(get()) }

    single<DictionaryRepository> { DefaultDictionaryRepository(get(), get()) }
    single<SearchRepository> { SearchRepositoryImpl(get(), get(), get()) }
    single<FavoriteRepository> { DefaultFavoriteRepository(get()) }

    single<DictionaryInteractor> { DictionaryInteractorImpl(get()) }
    single<SearchInteractor> { SearchInteractorImpl(get()) }
    single<FavoriteInteractor> { FavoriteInteractorImpl(get()) }

    viewModel { SplashViewModel(get()) }
    viewModel { DictionaryViewModel(get()) }
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { FavoriteViewModel(get()) }
}