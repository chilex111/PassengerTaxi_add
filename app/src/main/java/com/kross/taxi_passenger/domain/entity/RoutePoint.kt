package com.kross.taxi_passenger.domain.entity

import android.arch.persistence.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.kross.taxi_passenger.R

enum class PointState(rawValue: Int) {

    next(0),
    on(1),
    passed(2);

    val icon: Int
        get() {
        return when(this) {
            on -> R.drawable.ic_pin_purple_copy
            passed -> R.drawable.ic_pin_light_copy
            next -> R.drawable.ic_pin_dark_copy
        }
    }

    val pointIcon: Int
        get() {
        return when(this) {
            on -> R.drawable.ic_pin_purple
            passed -> R.drawable.ic_pin_light
            next -> R.drawable.ic_waypoint_pin
        }
    }

}

class RoutePoint(@PrimaryKey(autoGenerate = true)
                    val id: Long = 0,
                    val street: String,
                    val streetNumber: String,
                    val lat: Double,
                    val lng: Double,
                    var state: Int = 0) {

    fun pointState(): PointState {
        return PointState.values()[state]
    }

    fun position(): LatLng {
        return  LatLng(lat, lng)
    }

}