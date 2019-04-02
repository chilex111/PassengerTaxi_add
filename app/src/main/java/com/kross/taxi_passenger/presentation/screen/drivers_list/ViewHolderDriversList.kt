package com.kross.taxi_passenger.presentation.screen.drivers_list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.kross.taxi_passenger.data.repository.server.pojo.response.Driver

class ViewHolderDriversList(itemView: View): RecyclerView.ViewHolder(itemView){

    fun bind(driver: Driver, internalListener: View.OnClickListener){
        itemView.tag = driver
        itemView.setOnClickListener(internalListener)
    }
}