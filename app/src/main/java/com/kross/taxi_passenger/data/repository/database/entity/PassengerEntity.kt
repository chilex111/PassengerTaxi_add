package com.kross.taxi_passenger.data.repository.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "passengers")
class PassengerEntity(@PrimaryKey
                      val id: Int,
                      val firstName: String,
                      val lastName: String,
                      val email: String,
                      val phoneNumber: String,
                      val emailVerified: Boolean,
                      val referralCode: String,
                      val type: Int,
                      val status: Int,
                      val photo: String?)