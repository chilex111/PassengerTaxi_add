package com.kross.taxi_passenger.data.repository.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.kross.taxi_passenger.data.repository.database.entity.*

const val TABLE_NAME_PASSENGERS: String = "passengers"

const val TABLE_NAME_WALLET: String = "wallets"

const val TABLE_NAME_USER_POINT: String = "user_point"

@Dao
interface PassengerDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPassenger(passengerEntity: PassengerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWallet(walletEntity: WalletEntity)

    @Query("select * from $TABLE_NAME_PASSENGERS where id = :id")
    fun getPassengerById(id: Int): LiveData<PassengerEntity>

    @Query("select * from $TABLE_NAME_WALLET where id = :id")
    fun getWalletById(id: Int): LiveData<WalletEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserPointEntity(userPointEntity: UserPointEntity)

    @Query("select * from $TABLE_NAME_USER_POINT")
    fun getUserPoints(): LiveData<List<UserPointEntity>>

}