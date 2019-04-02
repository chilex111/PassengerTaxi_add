package com.kross.taxi_passenger.domain

import android.app.Application
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.database.entity.Card
import com.kross.taxi_passenger.utils.getIntPreference

class MyCardsViewModel(application: Application, val repository: Repository): BaseViewModel(application) {

    fun getUserCards() = repository.getUserCards(getContext().getIntPreference(R.string.user_id))

    fun setDefaultCard(card: Card) {
        repository.setDefaultCard(getContext().getIntPreference(R.string.user_id), card.apply { defaultFlag = true })
    }

    fun deleteCard(card: Card) = repository.deleteCard(card)
}
