package com.kross.taxi_passenger.data.repository.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "example")
data class ExampleEntity(@PrimaryKey(autoGenerate = true)
                         var id: Int,
                         var someField: String?)
