package com.kross.taxi_passenger.data.repository.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import android.arch.persistence.room.Update
import com.kross.taxi_passenger.data.repository.database.entity.Card

@Dao
abstract class TransactionDAO {

    @Query("delete from card where id = :id")
    abstract fun deleteCard(id: Int)

    @Update
    abstract fun updateCard(card: Card)

    @Transaction
    open fun deleteAndUpdateDefaultCard(card: Card, idDeletedCard: Int){
        deleteCard(idDeletedCard)
        updateCard(card)
    }
}