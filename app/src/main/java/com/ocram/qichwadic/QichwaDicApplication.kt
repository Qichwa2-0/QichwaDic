package com.ocram.qichwadic

import android.app.Application

import androidx.appcompat.app.AppCompatActivity
import com.ocram.qichwadic.core.di.appModule

import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class QichwaDicApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@QichwaDicApplication)
            modules(appModule)
        }
    }

    companion object {

        operator fun get(activity: AppCompatActivity): QichwaDicApplication {
            return activity.application as QichwaDicApplication
        }
    }
}
