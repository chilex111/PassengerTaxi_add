package com.kross.taxi_passenger.data.repository.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.kross.taxi_passenger.data.repository.database.converters.DateConverter
import com.kross.taxi_passenger.data.repository.database.converters.LatLngConverter
import com.kross.taxi_passenger.data.repository.database.converters.RoutePointConverter
import com.kross.taxi_passenger.data.repository.database.dao.*
import com.kross.taxi_passenger.data.repository.database.entity.*


@Database(entities = [
    ExampleEntity::class,
    Card::class,
    PassengerEntity::class,
    WalletEntity::class,
    OrderEntity::class,
    BankAccount::class,
    UserPointEntity::class,
    CarEntity::class,
    DriverEntity::class,
    LocationEntity::class],
        version = 1,
        exportSchema = false)

@TypeConverters(DateConverter::class, LatLngConverter::class, RoutePointConverter::class)
abstract class Database : RoomDatabase() {

    abstract fun mainDao(): ExampleDAO
    abstract fun cardDao(): CardDAO
    abstract fun passengerDao(): PassengerDAO
    abstract fun orderDao(): OrderInfoDAO
    abstract fun bankDao(): BankDAO
    abstract fun transactionDao(): TransactionDAO
    abstract fun carDao(): CarsDao

}