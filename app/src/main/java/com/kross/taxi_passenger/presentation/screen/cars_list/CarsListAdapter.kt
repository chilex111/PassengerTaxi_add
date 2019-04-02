package com.kross.taxi_passenger.presentation.screen.cars_list

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.widget.BaseAdapter
import kotlinx.android.synthetic.main.cars_list_item.view.*
import java.util.*


class CarsListAdapter(val itemClick: OnItemClickListener, context: Context) : BaseAdapter<ViewHolderCarsList, FullCarInfo>() {


    private val listCars = ArrayList<FullCarInfo>()
    private val context = context


    private val internalListener = View.OnClickListener {
        val item = it.tag as FullCarInfo
        itemClick.onItemClick(it, item)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCarsList {
        val view = LayoutInflater.from(parent.context).inflate(getLayout(), parent, false)
        return ViewHolderCarsList(view)
    }

  //  override fun getItemCount() = listCars.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderCarsList, position: Int) {
        super.onBindViewHolder(holder, position)
        val carObj = super.list.get(position)
        holder.bind(carObj, internalListener)
        Glide.with(context)
                .asBitmap()
                .load(carObj.car?.photo)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.itemView.carPhoto2)

        holder.itemView.carMakeAndModelTv1.text = carObj.car?.make + " " + carObj.car?.model
        holder.itemView.carPlateNumberTv1.text = carObj.car?.plate_number
        holder.itemView.money_earned_tv.text = "₦" +  carObj.car?.moneyEarned.toString()
        holder.itemView.trips_made_tv.text = carObj.car?.tripsCount.toString()
        if (carObj.driver != null) {
            holder.itemView.yearOfManufacture1.text = carObj.driver?.firstName + " " + carObj.driver?.lastName
        }

        if (carObj.locationEntity != null) {
            setLocation(carObj, holder)
        }
    }

    private fun setLocation(carObj: FullCarInfo, holder: ViewHolderCarsList) {
        carObj.locationEntity?.current?.let { latLng ->
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address>

            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude
                    , 1)
            if (!addresses.isEmpty()) holder.itemView.location_tv1.text = addresses[0].getAddressLine(0)
        }
    }

    override fun getLayout(): Int {
        return R.layout.cars_list_item
    }

    fun filter(text: String, list: List<FullCarInfo>): Boolean {
        listCars.clear()
        listCars.addAll(list)
        notifyDataSetChanged()
        val result = list.isNotEmpty()
        return result
    }

    interface OnItemClickListener {

        fun onItemClick(view: View, carModel: FullCarInfo)
    }

}
