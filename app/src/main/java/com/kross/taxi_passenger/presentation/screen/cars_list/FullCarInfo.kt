package com.kross.taxi_passenger.presentation.screen.cars_list

import android.os.Parcel
import android.os.Parcelable
import com.kross.taxi_passenger.data.repository.database.entity.CarEntity
import com.kross.taxi_passenger.data.repository.database.entity.DriverEntity
import com.kross.taxi_passenger.data.repository.database.entity.LocationEntity

data class FullCarInfo(var key: Boolean){
    var car: CarEntity? = null
    var driver: DriverEntity?  = null
    var locationEntity: LocationEntity?  = null
}