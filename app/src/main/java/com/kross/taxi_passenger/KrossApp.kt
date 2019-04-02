package com.kross.taxi_passenger

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatDelegate
import com.facebook.stetho.Stetho
import com.kross.taxi_passenger.koin.repositoryModule
import com.kross.taxi_passenger.koin.viewModelModule
import org.koin.android.ext.android.startKoin

class KrossApp : Application() {

    init {
        instance = this
    }

    companion object {
        var instance: KrossApp? = null
        val applicationContext : Context
            get() = instance!!.applicationContext
    }



    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        startKoin(this, listOf(viewModelModule, repositoryModule))

        Stetho.initializeWithDefaults(this)
    }
}