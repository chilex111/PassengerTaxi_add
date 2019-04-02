package com.kross.taxi_passenger.domain

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.server.pojo.response.News
import com.kross.taxi_passenger.presentation.widget.SingleLiveEvent

class NewsViewModel(application: Application, private val repository: Repository) : BaseViewModel(application) {

    private val liveDataNews = MutableLiveData<News>()
    private val liveDateBecomeOwner = SingleLiveEvent<Boolean>()

    val progressLiveData = SingleLiveEvent<Boolean>()


    @SuppressLint("CheckResult")
    fun getNews(amount: Int, offset: Int,apiKey: String): MutableLiveData<News> {
        progressLiveData.value = true
        repository.getAppNews(amount,offset,apiKey)
                .subscribe({
                    liveDataNews.value = it
                    progressLiveData.value = false
                },
                        {
                            throwable: Throwable? -> throwable?.printStackTrace()
                        })

        return liveDataNews
    }


}