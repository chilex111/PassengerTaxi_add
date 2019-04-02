package com.kross.taxi_passenger.data.repository.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "bank_account")
class BankAccount(@PrimaryKey(autoGenerate = true)
                  var id: Long,
                  @ColumnInfo(name = "bank_name") var bankName: String,
                  @ColumnInfo(name = "account_number") var accountNumber: String,
                  @ColumnInfo(name = "account_name") var accountName: String
) {

    fun getShortNumber(): String {
        return "****" + accountNumber.substring(accountNumber.count() - 4)
    }

}