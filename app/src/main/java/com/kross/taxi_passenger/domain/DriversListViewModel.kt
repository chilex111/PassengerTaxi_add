package com.kross.taxi_passenger.domain

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.view.View
import com.facebook.stetho.inspector.protocol.module.Database
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.server.pojo.response.Cars
import com.kross.taxi_passenger.data.repository.server.pojo.response.DriversList
import com.kross.taxi_passenger.presentation.widget.SingleLiveEvent
import io.reactivex.Single

class DriversListViewModel (application: Application, private val repository: Repository) : BaseViewModel(application) {


    private val liveDataLoading = SingleLiveEvent<Int>()
    private val liveDataDrivers = MutableLiveData<DriversList>()

    @SuppressLint("CheckResult")
    fun getDrivers(apiKey: String, amount: Int): MutableLiveData<DriversList>{
        repository.getDriversFromServer(apiKey, amount).doOnSubscribe { liveDataLoading.value = View.VISIBLE }
                .doAfterTerminate { liveDataLoading.value = View.GONE }
                .subscribe(
                        { response: DriversList? -> response?.let { liveDataDrivers.value = it } },
                        { throwable: Throwable? -> throwable?.printStackTrace() })
        return liveDataDrivers
    }



    fun getLiveDataLoading(): LiveData<Int> = liveDataLoading
}