package com.kross.taxi_passenger.data.repository.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "card")
class Card(@PrimaryKey(autoGenerate = true)
           val id: Int,
           val cardName: String,
           val cardNumber: Long,
           val expirationDate: String,
           val cvv: Int,
           @ColumnInfo(name = "default_flag")
           var defaultFlag: Boolean,
           @ColumnInfo(name = "user_id")
           val userId: Int): Parcelable {

    fun getShortNumber(): String {
        return cardNumber.toString().let {
            "****${it.substring(it.length - 4)}"
        }
    }

    fun getShortestNumber(): String {
        return cardNumber.toString().let {
            "**${it.substring(it.length - 4)}"
        }
    }
}