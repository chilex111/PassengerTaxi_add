package com.kross.taxi_passenger.presentation.screen.all_trips_list_screen

import android.view.View
import com.kross.taxi_passenger.data.repository.server.pojo.response.RoutePointsDetails
import com.kross.taxi_passenger.utils.gone
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_favorite_route_subitem.*

class AllTripsRouteSubItemViewHolder(override val containerView: View) : LayoutContainer {


    fun bind(routePoint: RoutePointsDetails, isFirst: Boolean, isLast: Boolean) {
        if (isFirst) vTopLine.gone()
        else if (isLast) vBottomLine.gone()
        tvPointName.text = routePoint.address
    }

}