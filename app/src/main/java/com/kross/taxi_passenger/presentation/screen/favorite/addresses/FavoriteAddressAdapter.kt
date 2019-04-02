package com.kross.taxi_passenger.presentation.screen.favorite.addresses

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.FavoriteAddress

class FavoriteAddressAdapter(private val chooseListener: ((FavoriteAddress) -> Unit),
                             private val editListener: ((FavoriteAddress) -> Unit),
                             private val deleteListener: ((FavoriteAddress) -> Unit)): RecyclerView.Adapter<FavoriteAddressViewHolder>() {

    private val items: MutableList<FavoriteAddress> = mutableListOf()

    private val chooseLocalListener: ((Int) -> Unit) = { chooseListener.invoke(items[it]) }
    private val editLocalListener: ((Int) -> Unit) = { editListener.invoke(items[it]) }
    private val deleteLocalListener: ((Int) -> Unit) = { deleteListener.invoke(items[it]) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteAddressViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_favorite_address, parent, false)

        return FavoriteAddressViewHolder(view, chooseLocalListener, editLocalListener, deleteLocalListener)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: FavoriteAddressViewHolder, position: Int) =
            holder.bind(items[position])

    fun setItems(addresses: List<FavoriteAddress>) {
        items.clear()
        items.addAll(addresses)
        notifyDataSetChanged()
    }
}