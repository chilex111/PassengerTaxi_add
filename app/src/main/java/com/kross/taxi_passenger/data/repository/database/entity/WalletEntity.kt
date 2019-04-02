package com.kross.taxi_passenger.data.repository.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "wallets")
class WalletEntity(@PrimaryKey
                   val id: Int,
                   val total: Float)