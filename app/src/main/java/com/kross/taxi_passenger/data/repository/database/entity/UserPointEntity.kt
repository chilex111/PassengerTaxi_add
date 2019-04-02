package com.kross.taxi_passenger.data.repository.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.kross.taxi_passenger.data.repository.database.dao.TABLE_NAME_USER_POINT


@Entity(tableName = TABLE_NAME_USER_POINT)
class UserPointEntity(@PrimaryKey val id: Int,
                      val position: LatLng,
                      val name: String) {

    companion object {
        enum class ID {
            HOME,
            WORK
        }
    }

}