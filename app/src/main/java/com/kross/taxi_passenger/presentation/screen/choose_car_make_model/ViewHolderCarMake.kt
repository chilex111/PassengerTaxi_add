package com.test.client_taxi_kross_driver_android.presentation.ui.choose_car_make_model

import android.support.v7.widget.RecyclerView
import android.view.View
import com.test.client_taxi_kross_driver_android.data.server.pojo.response.Make
import kotlinx.android.synthetic.main.item_rv_car_make_model.view.*

class ViewHolderCarMake (itemView: View): RecyclerView.ViewHolder(itemView){

    fun bind(make: Make){
        itemView.txtCarModelMake.text = make.makeName
    }
}