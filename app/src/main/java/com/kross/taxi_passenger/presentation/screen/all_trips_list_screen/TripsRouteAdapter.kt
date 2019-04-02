package com.kross.taxi_passenger.presentation.screen.all_trips_list_screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.response.Trip
import kotlinx.android.synthetic.main.all_trips_item.view.*
import android.text.method.TextKeyListener.clear
import android.support.v7.widget.RecyclerView.ViewHolder
import com.google.android.gms.maps.model.LatLng
import com.kross.taxi_passenger.R.id.mapViewItem
import com.kross.taxi_passenger.presentation.screen.car_location.CarLocationActivity


class TripsRouteAdapter(private val layoutInflater: LayoutInflater, private var context: Activity) : RecyclerView.Adapter<TripsRouteViewHolder>() {


    private val items: MutableList<Trip> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripsRouteViewHolder {
        val view = layoutInflater
                .inflate(R.layout.all_trips_item, parent, false)
        return TripsRouteViewHolder(view, context)
    }

    override fun getItemCount(): Int {
        Log.d("COOOL_ITEM_COUNT", "DONE " + items.size)
        return items.size
    }



    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TripsRouteViewHolder, position: Int) {
        val item = items.get(position)

        holder.bind( item, layoutInflater)
        holder.itemView.tripMoneyAllTv.text =  "Trip: ₦" + item.financeData.trip
        holder.itemView.tripMonyMyTv.text =  "My earnings: ₦" + item.financeData.my_earings
        holder.itemView.tollTv.text =  "Toll: ₦" + item.financeData.toll
        if (holder.mapCurrent != null) {
            holder.mapCurrent!!.clear();
            holder.mapCurrent!!.mapType = GoogleMap.MAP_TYPE_NONE;
        }
        Log.d("COOL_FINANCE", " " + position + " " +  item.financeData.trip.toString())

    }



    fun setItems(it: List<Trip>) {
        items.clear()
        items.addAll(it)
        notifyDataSetChanged()

    }
}