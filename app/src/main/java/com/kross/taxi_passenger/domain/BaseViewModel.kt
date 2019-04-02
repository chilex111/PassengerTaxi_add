package com.kross.taxi_passenger.domain

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.support.annotation.StringRes
import com.kross.taxi_passenger.KrossApp
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.utils.getStringPreference

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    fun getContext() = getApplication<KrossApp>()

    fun getString(@StringRes id: Int): String = getContext().getString(id)

    val contentType
        get() = getContext().getString(R.string.CONTENT_TYPE)

    val token
        get() = getContext().getStringPreference(R.string.token)

    val cacheControl
        get() = getContext().getString(R.string.CACHE_CONNTROL)

}