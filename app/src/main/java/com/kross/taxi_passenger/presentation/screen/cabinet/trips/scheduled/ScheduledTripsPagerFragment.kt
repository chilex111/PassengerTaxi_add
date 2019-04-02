package com.kross.taxi_passenger.presentation.screen.cabinet.trips.scheduled

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.OrderEntity
import com.kross.taxi_passenger.domain.MapViewModel
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.presentation.screen.base.BaseFragment
import com.kross.taxi_passenger.presentation.screen.cabinet.trips.adapter.AddressPassAdapter
import com.kross.taxi_passenger.presentation.screen.cabinet.trips.adapter.TripsAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_my_trips_past.*
import kotlinx.android.synthetic.main.item_scheduled_trip.*
import org.koin.android.viewmodel.ext.android.viewModel

class ScheduleOrderViewHolder(override val containerView: View,
                              private val onItemTapListener: (OrderEntity) -> Unit,
                              private val onCancelTapListener: ((OrderEntity) -> Unit)? = null,
                              private val onEditTapListener: ((OrderEntity) -> Unit)? = null): RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun configure(orderEntity: OrderEntity) {
        dateTextView.text = orderEntity.getOrderDate()
        timeTextView.text = orderEntity.getOrderDuration()
        priceTextView.text = orderEntity.costRange
        cardImageView.setImageResource(orderEntity.paymentMethod().image)
        initAdapter(orderEntity.revertPoints())
        addListeners(orderEntity)
    }

    private fun initAdapter(items: List<StubWaypointAddress>) {
        tableView.layoutManager = LinearLayoutManager(containerView.context)
        tableView.adapter = AddressPassAdapter(items)
    }

    private fun addListeners(orderEntity: OrderEntity) {
        tableView.setOnClickListener {
            onItemTapListener.invoke(orderEntity)
        }
        editButton.setOnClickListener {
            onEditTapListener?.invoke(orderEntity)
        }
        cancelButton.setOnClickListener {
            onCancelTapListener?.invoke(orderEntity)
        }
    }

}

class ScheduledTripsPagerFragment: BaseFragment() {

    companion object {
        fun newInstance() = ScheduledTripsPagerFragment()
    }

    private val viewModel: MapViewModel by viewModel()

    override fun getLayout() = R.layout.fragment_my_trips_past

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        descriptionTextView.setText(R.string.txt_scheduled_trips_empty)
        initAdapter()
    }

    // MARK: - Actions

    private fun onOrderTap(orderEntity: OrderEntity) {
        // TODO: - open current order screen
    }

    private fun onCancelTap(orderEntity: OrderEntity) {
        viewModel.cancelOrder(orderEntity.id)
    }

    private fun onEditTap(orderEntity: OrderEntity) {
        // TODO: - open time watcher, after changes reload adapter
    }

    // MARK: - Helper methods

    private fun initAdapter() {
        if(activity == null) return
        recyclerView.layoutManager = LinearLayoutManager(activity)
        viewModel.getOrders()?.observe(activity!!, Observer {
            val items = it?.toTypedArray()
            recyclerView.visibility = if (items == null || items.isEmpty()) View.GONE else View.VISIBLE
            recyclerView.adapter = TripsAdapter(items ?: arrayOf(), this::onOrderTap, this::onCancelTap, this::onEditTap, false)
        })
    }

    // MARK: - Navigation

}