package com.kross.taxi_passenger.presentation.screen.cabinet.trips.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.OrderEntity
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.presentation.screen.cabinet.trips.past.PastOrderViewHolder
import com.kross.taxi_passenger.presentation.screen.cabinet.trips.scheduled.ScheduleOrderViewHolder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_favorite_route_subitem.*

class AddressPassViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun configure(address: StubWaypointAddress, isFirst: Boolean, isLast: Boolean) {
        tvPointName.text = address.address
        vTopLine.visibility = if(isFirst) View.GONE else View.VISIBLE
        vBottomLine.visibility = if(isLast) View.GONE else View.VISIBLE
    }

}

class AddressPassAdapter(private val items: List<StubWaypointAddress>): RecyclerView.Adapter<AddressPassViewHolder>() {

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: AddressPassViewHolder, position: Int) {
        holder.configure(items[position], position == 0, position == items.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressPassViewHolder {
        LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_favorite_route_subitem, parent, false)
                .let {
                    return AddressPassViewHolder(it)
                }
    }

}

class TripsAdapter(private val items: Array<OrderEntity>,
                   private val onItemTapListener: (OrderEntity) -> Unit,
                   private val onCancelTapListener: ((OrderEntity) -> Unit)? = null,
                   private val onEditTapListener: ((OrderEntity) -> Unit)? = null,
                   private val isPast: Boolean = true): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(isPast) (holder as PastOrderViewHolder).configure(items[position])
        else (holder as ScheduleOrderViewHolder).configure(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        LayoutInflater
                .from(parent.context)
                .inflate(if(isPast) R.layout.item_past_trip else R.layout.item_scheduled_trip, parent, false)
                .let {
                    return if(isPast) PastOrderViewHolder(it, onItemTapListener)
                    else ScheduleOrderViewHolder(it, onItemTapListener, onCancelTapListener, onEditTapListener)
                }
    }

}