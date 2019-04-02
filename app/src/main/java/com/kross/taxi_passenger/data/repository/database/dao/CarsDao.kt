package com.kross.taxi_passenger.data.repository.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.kross.taxi_passenger.data.repository.database.entity.*
import java.sql.Driver


const val TABLE_NAME_CAR = "cars"
const val TABLE_NAME_DRIVER = "drivers"
const val TABLE_NAME_LOCATION = "locations"

@Dao
interface CarsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCars(cars: List<CarEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCar(car: CarEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDrivers(drivers: List<DriverEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDriver(driver: DriverEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocations(locations: List<LocationEntity>)


    @Query("select * from $TABLE_NAME_DRIVER where id = :driverId")
    fun getCarDriver(driverId: Int): DriverEntity

    @Query("select * from $TABLE_NAME_CAR")
    fun getCars(): List<CarEntity>

    @Query("select * from $TABLE_NAME_CAR  where id = :carId")
    fun getCar(carId: Int): CarEntity?

    @Query("select * from $TABLE_NAME_LOCATION where id = :carId")
    fun getCarLocation(carId: Int): LocationEntity?

    @Query("select driver_id from $TABLE_NAME_CAR where id = :carId")
    fun getDriverId(carId: Int): Long

    @Delete
    fun delete(carEntity: CarEntity)
}