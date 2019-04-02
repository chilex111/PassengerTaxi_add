package com.test.client_taxi_kross_driver_android.presentation.ui.choose_car_make_model

import android.support.v7.widget.RecyclerView
import android.view.View
import com.test.client_taxi_kross_driver_android.data.server.pojo.response.ModelCar
import kotlinx.android.synthetic.main.item_rv_car_make_model.view.*

class ViewHolderCarModel(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(carModel: ModelCar, internalListener: View.OnClickListener){
        itemView.tag = carModel
        itemView.setOnClickListener(internalListener)
        itemView.txtCarModelMake.text = carModel.modelName
    }
}