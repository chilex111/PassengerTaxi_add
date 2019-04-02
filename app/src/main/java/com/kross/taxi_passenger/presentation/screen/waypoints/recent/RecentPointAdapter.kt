package com.kross.taxi_passenger.presentation.screen.waypoints.recent

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress

class RecentPointAdapter(val list: List<StubWaypointAddress>,
                         listener: (StubWaypointAddress) -> Unit): RecyclerView.Adapter<RecentViewHolder>() {

    val onClickListener: (Int) -> Unit = { listener.invoke(list[it]) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        LayoutInflater.from(parent.context)
                .inflate(R.layout.item_waypoint_suggestion, parent, false)
                .let { return RecentViewHolder(it, onClickListener) }
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        holder.bind(list[position].address)
    }
}