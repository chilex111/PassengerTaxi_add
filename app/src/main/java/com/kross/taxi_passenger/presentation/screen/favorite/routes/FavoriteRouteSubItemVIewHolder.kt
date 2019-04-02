package com.kross.taxi_passenger.presentation.screen.favorite.routes

import android.annotation.SuppressLint
import android.view.View
import com.kross.taxi_passenger.data.repository.server.pojo.KrossRoutePoint
import com.kross.taxi_passenger.utils.gone
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_favorite_route_subitem.*

class FavoriteRouteSubItemViewHolder(override val containerView: View): LayoutContainer {
    @SuppressLint("SetTextI18n")
    fun bind(routePoint: KrossRoutePoint, isFirst: Boolean, isLast: Boolean) {
        if(isFirst) vTopLine.gone()
        else if(isLast) vBottomLine.gone()

        tvPointName.text = " ${routePoint.street}"
    }
}