package com.kross.taxi_passenger.presentation.screen.payment.mycards

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.Card

class MyCardsViewHolder(view: View,
                        defaultlistener: (Int) -> Unit,
                        deleteListener: (Int) -> Unit,
                        chooseListener: (Int) -> Unit): RecyclerView.ViewHolder(view) {

    private val cardTitle: TextView = view.findViewById(R.id.tvCardName)
    private val cardIcon: ImageView = view.findViewById(R.id.ivCard)
    private val ivMore: View = view.findViewById(R.id.ivMore)
    private var isDefaultEnabled: Boolean = true

    init {
        ivMore.setOnClickListener {
            PopupMenu(ivMore.context, ivMore)
                    .apply {
                        menuInflater.inflate(R.menu.menu_my_cards_item, menu)
                        menu.findItem(R.id.action_default).isEnabled = isDefaultEnabled
                        setOnMenuItemClickListener {
                            when(it.itemId) {
                                R.id.action_default -> defaultlistener.invoke(adapterPosition)
                                R.id.action_delete -> deleteListener.invoke(adapterPosition)
                            }

                            return@setOnMenuItemClickListener true
                        }
                        show()
                    }
        }

        itemView.setOnClickListener { chooseListener.invoke(adapterPosition) }
    }

    fun bind(card: Card) {
        cardTitle.text = "${card.cardName} (${card.getShortNumber()})"
        isDefaultEnabled = !card.defaultFlag

        if(card.defaultFlag) {
            cardIcon.setImageResource(R.drawable.ic_default_card)
        } else {
            cardIcon.setImageResource(R.drawable.ic_card)
        }
    }
}