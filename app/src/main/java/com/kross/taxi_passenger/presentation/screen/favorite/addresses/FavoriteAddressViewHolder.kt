package com.kross.taxi_passenger.presentation.screen.favorite.addresses

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.PopupMenu
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.FavoriteAddress
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_favorite_address.*
import org.jetbrains.anko.onClick

class FavoriteAddressViewHolder(override val containerView: View,
                                private val chooseListener: ((Int) -> Unit),
                                private val editListener: ((Int) -> Unit),
                                private val deleteListener: ((Int) -> Unit)): RecyclerView.ViewHolder(containerView), LayoutContainer {

    init {
        ivMore.onClick {
            PopupMenu(ivMore.context, ivMore)
                    .apply {
                        menuInflater.inflate(R.menu.menu_item_fav, menu)
                        setOnMenuItemClickListener {
                            when(it.itemId) {
                                R.id.action_edit -> invoke(editListener)
                                R.id.action_delete -> invoke(deleteListener)
                            }

                            return@setOnMenuItemClickListener true
                        }
                        show()
                    }
        }

        itemView.onClick { invoke(chooseListener) }
    }

    fun bind(favoriteAddress: FavoriteAddress) {
        if(favoriteAddress.name.isNullOrEmpty()) {
            tvFavoriteName.text = favoriteAddress.address
        } else {
            tvFavoriteName.text = favoriteAddress.name
            tvFavoriteAddress.text = favoriteAddress.address
        }
    }

    private fun invoke(listener: ((Int) -> Unit)) = listener.invoke(adapterPosition)
}