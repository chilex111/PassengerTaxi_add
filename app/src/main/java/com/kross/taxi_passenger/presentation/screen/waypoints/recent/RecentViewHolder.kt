package com.kross.taxi_passenger.presentation.screen.waypoints.recent

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.utils.gone

class RecentViewHolder(itemView: View, onClickListener: (Int) -> Unit): RecyclerView.ViewHolder(itemView) {
    private val addressLine1: TextView = itemView.findViewById(R.id.tv_address_line_1)

    init {
        itemView.findViewById<View>(R.id.tv_address_line_2).gone()
        itemView.setOnClickListener { onClickListener.invoke(adapterPosition) }
    }

    fun bind(name: String) {
        addressLine1.text = name
    }
}