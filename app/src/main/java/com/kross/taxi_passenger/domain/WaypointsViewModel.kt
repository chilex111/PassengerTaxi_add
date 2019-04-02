package com.kross.taxi_passenger.domain

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.database.entity.OrderEntity
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress

class WaypointsViewModel(private val repository: Repository) : ViewModel() {

    fun getUserPoints() = repository.getUserPoints()

    fun getRecent(): LiveData<List<OrderEntity>> {
       return repository.getRecent()
    }
}