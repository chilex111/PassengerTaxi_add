package com.kross.taxi_passenger.data.repository.database.converters

import android.arch.persistence.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import java.util.*

object LatLngConverter {

    @TypeConverter
    @JvmStatic
    fun fromLocation(latLng: LatLng?): String? {
        return latLng?.let { String.format(Locale.US, "%f,%f", it.latitude, it.longitude) }
    }

    @TypeConverter
    @JvmStatic
    fun toLocation(latlon: String?): LatLng? {
        if (latlon == null) {
            return null
        }

        val pieces = latlon.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        return LatLng(pieces[0].toDouble(), pieces[1].toDouble())
    }
}