package com.kross.taxi_passenger.presentation.screen.main.bottom

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.OrderEntity
import com.kross.taxi_passenger.data.repository.server.pojo.request.Order
import com.kross.taxi_passenger.domain.entity.PointState
import com.kross.taxi_passenger.domain.entity.RoutePoint
import com.kross.taxi_passenger.utils.visibleOrGone
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.bottom_dialog_trip_on.*
import kotlinx.android.synthetic.main.item_on_trip_address.*
import org.jetbrains.anko.image

class OnTripAddressViewHolder(override  val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun configure(address: RoutePoint, isLast: Boolean, isNext: Boolean) {
        ivDots.visibleOrGone(!isLast)
        addressTextView.text = address.street
        timeTextView.text = if(isNext) "12 min." else ""
        iv_pin.image = containerView.context.getDrawable(address.pointState().pointIcon)
    }

}

class OnTripAdapter(private val list: List<RoutePoint>): RecyclerView.Adapter<OnTripAddressViewHolder>() {

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnTripAddressViewHolder {
        LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_on_trip_address, parent,false)
                .let {
                    return OnTripAddressViewHolder(it)
                }
    }

    override fun onBindViewHolder(holder: OnTripAddressViewHolder, position: Int) {
        if (list.isEmpty()) return
        holder.configure(list[position], list.size - 1 == position, list.indexOfFirst {
            it.pointState() == PointState.next
        } == position)
    }

}

class TripOnBottomDialog(view: ViewGroup, order: OrderEntity, ctx: Context):  BottomDialog(view, R.layout.bottom_dialog_trip_on) {

    var changeTripListener:((OrderEntity) -> Unit)? = null
    var tripDetailsListener: ((OrderEntity) -> Unit)? = null

    // fake - delete after demo
    var startDriveListener: ((OrderEntity) -> Unit)? = null
    var deleteOrderListener: ((OrderEntity) -> Unit)? = null

    private var context: Context
    private var entity: OrderEntity

    init {
        context = ctx
        entity = order

        initAdapter()
        tripTextView.text = String.format(context.getString(R.string.txt_trip_number), order.priority + 1)
        tripTextView.setOnClickListener {
            swapTrips()
        }
        tripDetailsTextView.setOnClickListener {
            showTripDetails()
        }
        driveButton.setOnClickListener {
            fakeDrive()
        }
        cancelButton.setOnClickListener {
            deleteOrder()
        }
    }

    // MARK: - Actions
    private fun fakeDrive() {
        startDriveListener?.invoke(entity)
    }

    private fun showTripDetails() {
        changeTripListener?.invoke(entity)
    }

    private fun swapTrips() {
        tripDetailsListener?.invoke(entity)
    }

    private fun deleteOrder() {
        deleteOrderListener?.invoke(entity)
    }

    // MARK: - Helper methods

    private fun initAdapter() {
        tableView.layoutManager = LinearLayoutManager(context)
        tableView.adapter = OnTripAdapter(entity.listRoutePoint)
    }

    fun updateList(order: OrderEntity) {
        entity = order
        tableView.adapter = OnTripAdapter(entity.listRoutePoint)
    }

}