package com.kross.taxi_passenger.data.mappers

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.kross.taxi_passenger.data.repository.database.entity.CarEntity
import com.kross.taxi_passenger.data.repository.database.entity.DriverEntity
import com.kross.taxi_passenger.data.repository.database.entity.LocationEntity
import com.kross.taxi_passenger.data.repository.server.pojo.response.CarsList
import io.reactivex.functions.Function
import java.util.*

class CarsMapper : Function<CarsList, Triple<List<CarEntity>, List<DriverEntity>, List<LocationEntity>>> {

    override fun apply(cars: CarsList): Triple<List<CarEntity>, List<DriverEntity>, List<LocationEntity>> {
        val listCarEntity = ArrayList<CarEntity>()
        val listDriverEntity = ArrayList<DriverEntity>()
        val listLocationEntity = ArrayList<LocationEntity>()
        Log.d("CAAAR", "" + cars.cars.size)
        cars.cars.forEach {
            var year = it.year
            if (year == null) {
                year = 0
            }
       //     listCarEntity.add(CarEntity(it.id, it.make, it.model, year, it.photo, it.money_earned, it.trips_count, it.status, it.id, it.driver?.id,it.plateNumber))
            listCarEntity.add(CarEntity(it.id, it.make, it.model, year, it.photo, it.plateNumber, it.money_earned, it.trips_count, it.status, it.id, it.driver?.id))
            listLocationEntity.add(LocationEntity(it.id, LatLng(it.location?.current!!.lat, it.location.current.lng), LatLng(it.location.last.lat, it.location.last.lng)))

            if (it.driver != null)
                listDriverEntity.add(DriverEntity(it.driver?.id, it.driver?.firstName, it.driver?.lastName, it.driver?.phoneNumber, it.driver?.driver_since, it.driver?.photo,it.driver?.country,it.driver?.city,it.driver?.rating,it.driver?.driving_from))
        }
        val triple = Triple(listCarEntity, listDriverEntity, listLocationEntity)
        return triple
    }
}