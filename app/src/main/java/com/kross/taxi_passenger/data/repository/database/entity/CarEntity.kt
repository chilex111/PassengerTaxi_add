package com.kross.taxi_passenger.data.repository.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kross.taxi_passenger.presentation.screen.cars_list.CarStatus
import com.kross.taxi_passenger.presentation.screen.main.map.State


@Entity(tableName = "cars")
class CarEntity(
        @PrimaryKey
        @ColumnInfo(name = "id") val id: Int,
        @ColumnInfo(name = "make") val make: String?,
        @ColumnInfo(name = "model") val model: String?,
        @ColumnInfo(name = "year") val year: Int?,
        @ColumnInfo(name = "photo") val photo: String?,
        @ColumnInfo(name = "plate_number") val plate_number: String?,
        @ColumnInfo(name = "money_earned") val moneyEarned: Float,
        @ColumnInfo(name = "trips_count") val tripsCount: Int,
        @ColumnInfo(name = "status") val status: Int,
        @ColumnInfo(name = "location_id") val locationId: Int?,
        @ColumnInfo(name = "driver_id") val driverId: Int?){


        fun status(): CarStatus {
                return CarStatus.values()[status]
        }
}
