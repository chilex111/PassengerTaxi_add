package com.kross.taxi_passenger.presentation.screen.favorite.routes

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.KrossRoute
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_favorite_route.*
import org.jetbrains.anko.onClick

class FavoriteRouteViewHolder(override val containerView: View,
                              private val chooseListener: ((Int) -> Unit),
                              private val editListener: ((Int) -> Unit),
                              private val deleteListener: ((Int) -> Unit)) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    init {
        ivMore.onClick {
            PopupMenu(ivMore.context, ivMore)
                    .apply {
                        menuInflater.inflate(R.menu.menu_item_fav, menu)
                        setOnMenuItemClickListener {
                            when (it.itemId) {
                                R.id.action_edit -> invoke(editListener)
                                R.id.action_delete -> invoke(deleteListener)
                            }

                            return@setOnMenuItemClickListener true
                        }
                        show()
                    }
        }

        btnOrderTaxi.onClick { invoke(chooseListener) }
    }

    fun bind(point: KrossRoute, inflater: LayoutInflater) {

        layoutPoints1.removeAllViews()

        point.routePoints.forEachIndexed { index, krossRoutePoint ->
            val holder = FavoriteRouteSubItemViewHolder(
                    inflater.inflate(R.layout.item_favorite_route_subitem, layoutPoints1, false))

            holder.bind(krossRoutePoint,
                    isFirst = index == 0,
                    isLast = index == point.routePoints.lastIndex)

            layoutPoints1.addView(holder.containerView)
        }
    }

    private fun invoke(listener: ((Int) -> Unit)) = listener.invoke(adapterPosition)
}