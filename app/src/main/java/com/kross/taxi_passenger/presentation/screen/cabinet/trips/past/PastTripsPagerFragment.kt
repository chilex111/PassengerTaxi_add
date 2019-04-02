package com.kross.taxi_passenger.presentation.screen.cabinet.trips.past

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
import com.kross.taxi_passenger.presentation.screen.main.map.State
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_my_trips_past.*
import kotlinx.android.synthetic.main.item_past_trip.*
import org.jetbrains.anko.textColor
import org.koin.android.viewmodel.ext.android.viewModel

class PastOrderViewHolder(override val containerView: View,
                          private val onItemTapListener: (OrderEntity) -> Unit): RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun configure(orderEntity: OrderEntity) {
        dateTextView.text = orderEntity.getOrderDate()
        timeTextView.text = orderEntity.getOrderDuration()
        val state = orderEntity.getFinishedState()
        stateTextView.setTextColor(containerView.resources.getColor(state.color))
        stateTextView.setText(state.title)
        initAdapter(orderEntity.revertPoints())
        addListener(orderEntity)
    }

    private fun addListener(orderEntity: OrderEntity) {
        tableView.setOnClickListener {
            onItemTapListener.invoke(orderEntity)
        }
    }

    private fun initAdapter(items: List<StubWaypointAddress>) {
        tableView.layoutManager = LinearLayoutManager(containerView.context)
        tableView.adapter = AddressPassAdapter(items)
    }

}

class PastTripsPagerFragment: BaseFragment() {

    companion object {
        fun newInstance() = PastTripsPagerFragment()
    }

    private val viewModel: MapViewModel by viewModel()

    override fun getLayout() = R.layout.fragment_my_trips_past

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        descriptionTextView.setText(R.string.txt_past_trips_empty)
        initAdapter()
    }

    // MARK: - Actions

    private fun onOrderTap(orderEntity: OrderEntity) {
        // TODO: open activity for past trip
    }

    // MARK: - Helper methods

    private fun initAdapter() {
        if (activity == null) return
        recyclerView.layoutManager = LinearLayoutManager(activity)
        viewModel.getOrders(State.TRIP_FINISHED)?.observe(activity!!, Observer {
            val items = it?.toTypedArray()
            recyclerView.visibility = if (items == null || items.isEmpty()) View.GONE else View.VISIBLE
            recyclerView.adapter = TripsAdapter(items ?: arrayOf(), this::onOrderTap, null, null)
        })
    }

    // MARK: - Navigation

}