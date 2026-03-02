package com.poetralabs.outoftopic

import android.app.Application
import com.poetralabs.outoftopic.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class OutOfTopicApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@OutOfTopicApp)
            modules(appModule)
        }
    }
}
