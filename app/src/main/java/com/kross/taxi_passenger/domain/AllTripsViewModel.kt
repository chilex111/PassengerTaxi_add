package com.kross.taxi_passenger.domain

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.view.View
import com.google.android.gms.location.places.GeoDataClient
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.server.pojo.response.TripsStatistic
import com.kross.taxi_passenger.presentation.widget.SingleLiveEvent

class AllTripsViewModel(private val repository: Repository, val geoDataClient: GeoDataClient) : ViewModel() {
    private val liveDataLoading = SingleLiveEvent<Int>()
    private val liveDataTripsStatistic = MutableLiveData<TripsStatistic>()

    @SuppressLint("CheckResult")
    fun getCarTripsStatistic(apiKey: String, id: Int, date: String): LiveData<TripsStatistic> {

        repository.getCarTtipsStatisticFromServer(apiKey, id, date).doOnSubscribe { liveDataLoading.value = View.VISIBLE }
                .doAfterTerminate { liveDataLoading.value = View.GONE }.subscribe({ response: TripsStatistic -> response.let { liveDataTripsStatistic.value = it } },
                { throwable: Throwable? ->
                    throwable?.printStackTrace()
                })
        return liveDataTripsStatistic
    }

    fun getLiveDataLoading(): LiveData<Int> = liveDataLoading
}