package com.kross.taxi_passenger.data.repository.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.kross.taxi_passenger.data.repository.database.entity.ExampleEntity

const val TABLE_NAME: String = "example"

@Dao
interface ExampleDAO {

    @Query("select * from $TABLE_NAME")
    fun getAll(): LiveData<List<ExampleEntity>>

    @Insert(onConflict = REPLACE)
    fun addAll(all: List<ExampleEntity>)

    @Update(onConflict = REPLACE)
    fun updateAll(all: List<ExampleEntity>)

    @Delete
    fun deleteAll(all: List<ExampleEntity>)


    @Query("select * from $TABLE_NAME where id = :id")
    fun getById(id: Int): LiveData<ExampleEntity>

    @Update(onConflict = REPLACE)
    fun update(mainEntity: ExampleEntity)

    @Insert(onConflict = REPLACE)
    fun add(mainEntity: ExampleEntity)

    @Delete
    fun delete(mainEntity: ExampleEntity)

}