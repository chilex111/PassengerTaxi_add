package com.kross.taxi_passenger.data.repository.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "locations")
class LocationEntity(
        @PrimaryKey
         val id: Int,
         val current: LatLng,
         val last: LatLng)
