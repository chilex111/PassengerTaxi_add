package com.kross.taxi_passenger.presentation.screen.favorite.routes

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.KrossRoute

class FavoriteRouteAdapter(private val layoutInflater: LayoutInflater,
                           private val chooseListener: ((KrossRoute) -> Unit),
                           private val editListener: ((KrossRoute) -> Unit),
                           private val deleteListener: ((KrossRoute) -> Unit)) : RecyclerView.Adapter<FavoriteRouteViewHolder>() {

    private val chooseLocalListener: ((Int) -> Unit) = { chooseListener.invoke(items[it]) }
    private val editLocalListener: ((Int) -> Unit) = { editListener.invoke(items[it]) }
    private val deleteLocalListener: ((Int) -> Unit) = { deleteListener.invoke(items[it]) }

    private val items: MutableList<KrossRoute> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteRouteViewHolder {
        val view = layoutInflater
                .inflate(R.layout.item_favorite_route, parent, false)

        return FavoriteRouteViewHolder(view, chooseLocalListener, editLocalListener, deleteLocalListener)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: FavoriteRouteViewHolder, position: Int) = holder.bind(items[position], layoutInflater)

    fun setItems(it: List<KrossRoute>) {
        items.clear()
        items.addAll(it)
        notifyDataSetChanged()
    }
}