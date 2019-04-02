package com.kross.taxi_passenger.data.repository.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.kross.taxi_passenger.data.repository.database.entity.BankAccount

const val TABLE_NAME_BANK = "bank_account"

@Dao
interface BankDAO {

    @Insert
    fun insert(account: BankAccount)

    @Query("select * from $TABLE_NAME_BANK")
    fun getBankAccount(): List<BankAccount>

    @Update
    fun update(account: BankAccount)

}

