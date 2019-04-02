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
import com.kross.taxi_passenger.presentation.widget.SingleLiveEvent

class CarsListViewModel(application: Application, private val repository: Repository) : BaseViewModel(application) {

    private val liveDataListCars = MutableLiveData<List<CarEntity>?>()

    private val loadingViewModel = SingleLiveEvent<Int>()

    @SuppressLint("CheckResult")
    fun getCars(apiKey: String): MutableLiveData<List<CarEntity>?> {

        repository.getCarsFromServer(apiKey).doOnSubscribe { loadingViewModel.value = View.VISIBLE }
                .doAfterTerminate { loadingViewModel.value = View.GONE }
                .subscribe({
                    liveDataListCars.value = repository.getCars()
                },
                        { throwable: Throwable? -> throwable?.printStackTrace() })
        return liveDataListCars
    }


    fun getDrivers(driverId: Int): DriverEntity? {
        return repository.getDriver(driverId)
    }

    fun getLocation(carId: Int): LocationEntity? {
        return repository.getLocation(carId)
    }


    fun getLiveDataLoading(): LiveData<Int> = loadingViewModel
}