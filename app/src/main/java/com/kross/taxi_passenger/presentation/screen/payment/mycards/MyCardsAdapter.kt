package com.kross.taxi_passenger.presentation.screen.payment.mycards

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.Card

class MyCardsAdapter(private val defaultCardListener: (Card) -> Unit,
                     private val deleteCardListener: (Card) -> Unit,
                     private val chooseListener: (Card) -> Unit): RecyclerView.Adapter<MyCardsViewHolder>() {

    private val defaultLocalListener: (Int) -> Unit = { defaultCardListener.invoke(myCards[it]) }
    private val deleteLocalListener: (Int) -> Unit = { deleteCardListener.invoke(myCards[it]) }
    private val chooseLocalListener: (Int) -> Unit = { chooseListener.invoke(myCards[it]) }

    private val myCards: MutableList<Card> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCardsViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.item_my_cards, parent, false)
                .let { return MyCardsViewHolder(it, defaultLocalListener, deleteLocalListener, chooseLocalListener) }
    }

    override fun getItemCount(): Int {
        return myCards.size
    }

    override fun onBindViewHolder(holder: MyCardsViewHolder, position: Int) {
        holder.bind(myCards[position])
    }

    fun setCards(cards: List<Card>) {
        myCards.clear()
        myCards.addAll(cards)
        notifyDataSetChanged()
    }
}