package com.kross.taxi_passenger.data.repository.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.kross.taxi_passenger.data.repository.database.entity.Card

const val TABLE_NAME_CARD = "card"

@Dao
interface CardDAO {

    @Insert
    fun insert(card: Card)

    @Query("select * from $TABLE_NAME_CARD where user_id = :userId")
    fun getUserCard(userId: Int): LiveData<List<Card>>

    @Query("select * from $TABLE_NAME_CARD where user_id = :userId")
    fun getUserCardsSync(userId: Int): List<Card>

    @Query("delete from $TABLE_NAME_CARD where id = :id")
    fun deleteCard(id: Int)

    @Query("UPDATE $TABLE_NAME_CARD SET default_flag = :isDefault WHERE user_id = :userId")
    fun resetDefaults(userId: Int, isDefault: Boolean = false)

    @Update
    fun updateCard(card: Card)
}