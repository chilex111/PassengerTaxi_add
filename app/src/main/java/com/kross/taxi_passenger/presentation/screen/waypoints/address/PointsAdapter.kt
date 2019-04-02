package com.kross.taxi_passenger.presentation.screen.waypoints.address

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress

class PointsAdapter(private val addressTypeListener: (String, Int) -> Unit,
                    private val focusChangeListener: (Int) -> Unit,
                    private val itemsChangedListener: (Int) -> Unit) : RecyclerView.Adapter<WaypointAddressViewHolder>() {

    private val MAX_SIZE = 4

    private val addresses: MutableList<StubWaypointAddress> = mutableListOf()

    private val onCloseListener: (Int) -> Unit = {
        addresses.removeAt(it)
        notifyDataSetChanged()
        itemsChangedListener.invoke(addresses.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaypointAddressViewHolder {
        LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_waypoint_address, parent, false)
                .let { return WaypointAddressViewHolder(it, onCloseListener, addressTypeListener, focusChangeListener) }
    }

    override fun getItemCount() = if (addresses.size < MAX_SIZE) addresses.size + 1 else addresses.size

    override fun onBindViewHolder(holder: WaypointAddressViewHolder, position: Int) {
        holder.bind(addresses.elementAtOrNull(position), position == itemCount - 1)
    }

    fun setItem(position: Int, stubWaypointAddress: StubWaypointAddress) {
        if(position >= MAX_SIZE) return

        if(position == addresses.size) {
            addresses.add(stubWaypointAddress)
        } else if(position < addresses.size) {
            addresses[position] = stubWaypointAddress
        }
        notifyDataSetChanged()
        itemsChangedListener.invoke(addresses.size)
    }

    fun addItem(stubWaypointAddress: StubWaypointAddress){
        if (addresses.size < MAX_SIZE) setItem(itemCount - 1, stubWaypointAddress) else  setItem(itemCount, stubWaypointAddress)
    }

    fun addItems(list: List<StubWaypointAddress>) {
        list.forEach { addItem(it) }
    }

    fun getItems() = addresses

    fun setItems(list: List<StubWaypointAddress>) {
        addresses.clear()
        addItems(list)
        notifyDataSetChanged()
    }
}