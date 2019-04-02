package com.kross.taxi_passenger.presentation.screen.cabinet.wallet.adapter

import android.content.Context
import com.kross.taxi_passenger.R
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kross.taxi_passenger.presentation.screen.cabinet.wallet.BankEnum
import com.kross.taxi_passenger.presentation.screen.trip_finished.TripDetailsEnum
import com.kross.taxi_passenger.utils.visibleOrGone
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_list_disclosure.*

interface ListSuperClass {

    var title: Int
    var details: Int
    var titleString: String
    var index: Int

}

enum class WalletEnum: ListSuperClass {

    trips,
    bank,
    negative;

    override var titleString: String =""
    override var index: Int = ordinal

    override var title: Int = 0
    get() {
        when(this) {
            trips -> return R.string.txt_action_1
            bank -> return R.string.txt_action_2
            negative -> return R.string.txt_action_3
        }
    }

    override var details: Int = 0
    get() {
        when(this) {
            trips -> return R.string.txt_action_details_1
            bank -> return R.string.txt_action_details_2
            negative -> return R.string.txt_action_details_3
        }
    }

}

class ListViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun configure(type: ListSuperClass, context: Context, listener: ((Int) -> Unit)?) {
        textView.text = if (type.title == 0) type.titleString else context.getString(type.title)
        if (type is BankEnum || type is TripDetailsEnum) disclosure.visibleOrGone(false)
        textView.setOnClickListener {
            listener?.invoke(type.index)
        }
    }

}

class WXListAdapter( private val list: Array<ListSuperClass>,
                     private val listener: ((Int) -> Unit)?): RecyclerView.Adapter<ListViewHolder>() {

    private lateinit var context: Context

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.configure((list[position]), context, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        context = parent.context
        LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_list_disclosure, parent, false)
                .let {
                    return ListViewHolder(it)
                }
    }

}