package com.kross.taxi_passenger.presentation.screen.cars_list

import android.support.v7.widget.RecyclerView
import android.view.View

class ViewHolderCarsList(itemView: View): RecyclerView.ViewHolder(itemView){

    fun bind(car: FullCarInfo, internalListener: View.OnClickListener){
        itemView.tag = car
        itemView.setOnClickListener(internalListener)
    }
}