package com.kross.taxi_passenger.domain

import android.arch.lifecycle.ViewModel
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.Place
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.database.entity.UserPointEntity

class AddPointViewModel(private val repository: Repository, val geoDataClient: GeoDataClient) : ViewModel() {

    fun saveHomePoint(it: Place) {
        repository.saveUserPoint(UserPointEntity.Companion.ID.HOME, it.latLng, it.address.toString())
    }

    fun saveWorkPoint(it: Place) {
        repository.saveUserPoint(UserPointEntity.Companion.ID.WORK, it.latLng, it.address.toString())
    }

}