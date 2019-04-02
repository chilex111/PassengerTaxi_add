package com.kross.taxi_passenger.presentation.screen.trip_details.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.domain.entity.PointState
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.presentation.screen.trip_finished.FavouriteAddressViewHolder
import com.kross.taxi_passenger.utils.visibleOrGone
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_address_trip.*
import org.jetbrains.anko.image

enum class AddressHolderEnum {

    address, favourite;

    var identifier: Int = 0
    get() {
        when(this) {
            address -> return R.layout.item_address_trip
            favourite -> return R.layout.item_add_to_fav_address
        }
    }

    fun holderIn(v: View, listener: (Int, StubWaypointAddress, Boolean) -> Unit): RecyclerView.ViewHolder {
        return when(this) {
            address -> AddressViewHolder(v, listener)
            favourite -> FavouriteAddressViewHolder(v, listener)
        }
    }

}

class AddressViewHolder(override val containerView: View,
                        editListener: (Int, StubWaypointAddress, Boolean) -> Unit): RecyclerView.ViewHolder(containerView), LayoutContainer {

    private lateinit var point: StubWaypointAddress

    init {
        buttonEdit.setOnClickListener {

            editListener(0, point, it.isSelected)
        }
    }

    fun configure(address: StubWaypointAddress, isLast: Boolean) {
        point = address
        addressTextView.text = address.address
        dots.visibleOrGone(!isLast)
        iv_pin.image = containerView.context.getDrawable(address.state.pointIcon)
        addressTextView.setTextColor(containerView.context.getColor(if (address.state == PointState.passed) R.color.light_grey else R.color.text_color_main))
        buttonEdit.visibleOrGone(address.state == PointState.next)
    }

}

class TripAdapter(private val editListener: (Int, StubWaypointAddress, Boolean) -> Unit,
                  private val type: AddressHolderEnum = AddressHolderEnum.address): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list: MutableList<StubWaypointAddress> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        LayoutInflater
                .from(parent.context)
                .inflate(type.identifier, parent, false)
                .let {
                    return type.holderIn(it, editListener)
                }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (type) {
             AddressHolderEnum.address -> (holder as AddressViewHolder).configure(list[position], list.size - 1 == position)
             AddressHolderEnum.favourite -> (holder as FavouriteAddressViewHolder).configure(list[position], list.size - 1 == position)
        }
    }

    fun setItems(array: List<StubWaypointAddress>) {
        list.clear()
        array.forEach {
            list.add(it)
        }
        notifyDataSetChanged()
    }

}