package com.kross.taxi_passenger.domain

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.view.View
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.server.pojo.response.Cars
import com.kross.taxi_passenger.data.repository.server.pojo.response.Driver
import com.kross.taxi_passenger.presentation.widget.SingleLiveEvent
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response

class DriverDetailsViewModel(application: Application, private val repository: Repository) : BaseViewModel(application) {

    private val liveDataLoading = SingleLiveEvent<Int>()
    private val liveDataCarStatistic = MutableLiveData<Cars>()
    private val liveDataDrivers = MutableLiveData<Driver>()
    private val liveDataAssignDriver = SingleLiveEvent<Boolean>()
    private val liveDataReleaseDriver = SingleLiveEvent<Boolean>()

    @SuppressLint("CheckResult")
    fun getDriver(apiKey: String,id: Int, amount: Int): MutableLiveData<Driver> {
        repository.getDriverByIdFomServer(apiKey,id, amount).doOnSubscribe { liveDataLoading.value = View.VISIBLE }
                .doAfterTerminate { liveDataLoading.value = View.GONE }
                .subscribe(
                        { response: Driver? -> response?.let { liveDataDrivers.value = it } },
                        { throwable: Throwable? -> throwable?.printStackTrace() })
        return liveDataDrivers
    }


    fun getLiveDataLoad(): LiveData<Int> = liveDataLoading


    @SuppressLint("CheckResult")
    fun assignDriver(apiKey: String, driverId: Int, carId: Int)  : LiveData<Boolean>{
        repository.assignDriver(apiKey,driverId,carId).doOnSubscribe { liveDataLoading.value = View.VISIBLE }
                .doAfterTerminate { liveDataLoading.value = View.GONE }
                .subscribe(
                        {response: Response<ResponseBody>? ->  response?.let { liveDataAssignDriver.value = it.isSuccessful }},
                        {throwable: Throwable? -> throwable?.printStackTrace() }
                )
        return liveDataAssignDriver
    }

    @SuppressLint("CheckResult")
    fun releaseDriver(apiKey: String, driverId: Int, carId: Int)  : LiveData<Boolean>{
        repository.releaseDriver(apiKey,driverId,carId).doOnSubscribe { liveDataLoading.value = View.VISIBLE }
                .doAfterTerminate { liveDataLoading.value = View.GONE }
                .subscribe(
                        {response: Response<ResponseBody>? ->  response?.let { liveDataReleaseDriver.value = it.isSuccessful }},
                        {throwable: Throwable? -> throwable?.printStackTrace() }
                )
        return liveDataReleaseDriver

    }

    @SuppressLint("CheckResult")
    fun getDriverId(id: Int): Long {
        return repository.getDriverId(id)
    }

}