package com.kross.taxi_passenger.data.repository.database.converters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kross.taxi_passenger.domain.entity.RoutePoint

class RoutePointConverter {

    @TypeConverter
    fun toCell(parcels: List<RoutePoint>): String {
        return Gson().toJson(parcels)
    }

    @TypeConverter
    fun fromCell(cell: String): List<RoutePoint> {
        return Gson().fromJson(cell, object : TypeToken<List<RoutePoint>>() {}.type)
    }
}