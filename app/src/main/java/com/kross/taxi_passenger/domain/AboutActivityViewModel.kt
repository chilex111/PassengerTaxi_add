package com.kross.taxi_passenger.domain

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.view.View
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.database.entity.CarEntity
import com.kross.taxi_passenger.presentation.widget.SingleLiveEvent

class AboutActivityViewModel (application: Application, private val repository: Repository) : BaseViewModel(application) {

    private val liveDataText = MutableLiveData<String>()
    private val liveDateBecomeOwner = SingleLiveEvent<Boolean>()


    @SuppressLint("CheckResult")
    fun getAboutText(apiKey: String): MutableLiveData<String> {
        repository.getAboutText(apiKey).doOnSubscribe { }
                .doAfterTerminate {}
                .subscribe({ it ->
                    liveDataText.value = it.text
                },
                        { throwable: Throwable? -> throwable?.printStackTrace() })
        return liveDataText
    }

}