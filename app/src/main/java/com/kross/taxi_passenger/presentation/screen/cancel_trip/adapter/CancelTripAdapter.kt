package com.kross.taxi_passenger.presentation.screen.cancel_trip.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kross.taxi_passenger.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_check.*

enum class CancelTripEnum {

    need, rate, route, price;

    var title: Int = R.string.txt_reason_1
    get() {
        when(this) {
            need -> return R.string.txt_reason_1
            rate -> return R.string.txt_reason_2
            route -> return R.string.txt_reason_3
            price -> return R.string.txt_reason_4
        }
    }

}

class CheckViewHolder(override val containerView: View,
                      private val context: Context,
                      private val onCheckListener: (Int, Boolean) -> Unit): RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun configure(item: CancelTripEnum, index: Int) {
        checkBox.text = context.getString(item.title)
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            onCheckListener.invoke(index, isChecked)
        }
    }

}

class CancelTripAdapter(private val onCheckListener: (Int, Boolean) -> (Unit)): RecyclerView.Adapter<CheckViewHolder>() {

    private val items = CancelTripEnum.values()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckViewHolder {
        LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_check, parent, false)
                .let {
                    return CheckViewHolder(it, parent.context, onCheckListener)
                }
    }

    override fun onBindViewHolder(holder: CheckViewHolder, position: Int) {
        holder.configure(items[position], position)
    }

}