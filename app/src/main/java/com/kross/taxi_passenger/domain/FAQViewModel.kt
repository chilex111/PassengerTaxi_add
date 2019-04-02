package com.kross.taxi_passenger.domain

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.server.pojo.response.FAQ
import com.kross.taxi_passenger.presentation.widget.SingleLiveEvent

class FAQViewModel(application: Application, private val repository: Repository) : BaseViewModel(application) {

    private val liveDataFAQ = MutableLiveData<List<FAQ>>()
    private val liveDateBecomeOwner = SingleLiveEvent<Boolean>()

    val progressLiveData = SingleLiveEvent<Boolean>()


    @SuppressLint("CheckResult")
    fun getFAQ(apiKey: String): MutableLiveData<List<FAQ>> {
        progressLiveData.value = true
        repository.getFaQList(apiKey)
                .subscribe({
                    liveDataFAQ.value = it
                    progressLiveData.value = false
                },
                        {
                            throwable: Throwable? -> throwable?.printStackTrace()
                        })

        return liveDataFAQ
    }


}