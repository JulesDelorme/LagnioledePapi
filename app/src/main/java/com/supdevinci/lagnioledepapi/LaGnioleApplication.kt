package com.supdevinci.lagnioledepapi

import android.app.Application
import android.content.Context
import com.supdevinci.lagnioledepapi.data.AppContainer
import com.supdevinci.lagnioledepapi.data.DefaultAppContainer

class LaGnioleApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}

val Context.appContainer: AppContainer
    get() = (applicationContext as LaGnioleApplication).container
