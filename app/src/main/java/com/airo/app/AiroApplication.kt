package com.airo.app

import android.app.Application
import com.airo.app.di.AppContainer

class AiroApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
