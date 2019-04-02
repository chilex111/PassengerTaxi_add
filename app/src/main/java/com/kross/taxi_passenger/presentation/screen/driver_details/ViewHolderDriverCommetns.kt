package com.kross.taxi_passenger.presentation.screen.driver_details

import android.support.v7.widget.RecyclerView
import android.view.View
import com.kross.taxi_passenger.data.repository.server.pojo.response.Driver
import com.kross.taxi_passenger.data.repository.server.pojo.response.ReviewItem
import org.w3c.dom.Comment

class ViewHolderDriverCommetns(itemView: View): RecyclerView.ViewHolder(itemView){

    fun bind(comment: ReviewItem){
        itemView.tag = comment
    }
}