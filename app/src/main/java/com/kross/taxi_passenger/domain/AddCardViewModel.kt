package com.kross.taxi_passenger.domain

import android.app.Application
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.database.entity.Card

class AddCardViewModel(application: Application, private val repository: Repository) : BaseViewModel(application) {

    fun addCard(name: String, number: Long, date: String, cvv: Int, userId: Int){
        val card = Card(0, name, number, date, cvv, false, userId)
        repository.addCard(card)
    }
}