package com.kross.taxi_passenger.domain

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.view.View
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.database.entity.CarEntity
import com.kross.taxi_passenger.data.repository.database.entity.DriverEntity
import com.kross.taxi_passenger.data.repository.database.entity.LocationEntity
import com.kross.taxi_passenger.data.repository.server.pojo.response.Cars
import com.kross.taxi_passenger.presentation.widget.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Response

class CarInfoViewModel(application: Application, private val repository: Repository) : BaseViewModel(application) {

    private val loadingViewModel = SingleLiveEvent<Int>()
    private val liveDataCarStatistic = MutableLiveData<Cars>()
    private val liveDataCarChart = MutableLiveData<Response<Map<String, Int>>>()
    val progressLiveData = SingleLiveEvent<Boolean>()

    @SuppressLint("CheckResult")
    fun getCar(id: Int): CarEntity? {
        return repository.getCar(id)
    }


    fun getDrivers(driverId: Int): DriverEntity? {
        return repository.getDriver(driverId)
    }

    fun getLocation(carId: Int): LocationEntity? {
        return repository.getLocation(carId)
    }

    @SuppressLint("CheckResult")
    fun getCarStatistic(apiKey: String, id: Int, dateStart: String, dateEnd: String): LiveData<Cars> {

        repository.getCarStatisticFromServer(apiKey, id, dateStart, dateEnd).doOnSubscribe { loadingViewModel.value = View.VISIBLE }
                .doAfterTerminate { loadingViewModel.value = View.GONE }
                .subscribe(
                        { response: Cars? -> response?.let { liveDataCarStatistic.value = it } },
                        { throwable: Throwable? -> throwable?.printStackTrace() })
        return liveDataCarStatistic
    }


    @SuppressLint("CheckResult")
    fun getCarChart(apiKey: String, id: Int, dateStart: String): LiveData<Response<Map<String, Int>>> {

        repository.getCarChart(apiKey, id, dateStart.toString()).doOnSubscribe { loadingViewModel.value = View.VISIBLE }
                .doAfterTerminate { loadingViewModel.value = View.GONE }
                .subscribe({ it: Response<Map<String, Int>>? -> liveDataCarChart.value = it },
//                        {t: List<Response<Map<String, Int>>>? -> liveDataCarChart.value = t },
                        { throwable: Throwable? -> throwable?.printStackTrace() })
        return liveDataCarChart
    }

    @SuppressLint("CheckResult")
    fun deleteCar(apiKey: String, id: Int) {
        progressLiveData.value = true
        repository.deleteCar(apiKey, id).subscribe({
            progressLiveData.value = false
        }, {
            progressLiveData.value = false
        })

    }

    fun getLiveDataLoading(): LiveData<Int> = loadingViewModel
}