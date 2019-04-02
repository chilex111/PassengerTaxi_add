package com.kross.taxi_passenger.presentation.screen.cabinet.support

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.response.HelpList
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_faq.view.*
import kotlinx.android.synthetic.main.item_support.*
import android.text.method.TextKeyListener.clear



// TODO: replace String with News object

class HelpViewHolder(private val makeComplaint: (HelpList) -> Unit, override val containerView: View?)
    : RecyclerView.ViewHolder(containerView), LayoutContainer {

    private lateinit var globalData: HelpList

    init {
        cardHelp.setOnClickListener {

            makeComplaint(globalData)
        }

    }
    fun configure(data: HelpList) {
        globalData = data
    }

}

class SupportAdapter(private val complaintListener: (HelpList) -> Unit,
                     val context: Context) : RecyclerView.Adapter<HelpViewHolder>() {

    private var items: MutableList<HelpList> = ArrayList()
        set(value) {
            if (value != items) {
                field = value
                notifyDataSetChanged()
            }
        }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: HelpViewHolder, position: Int) {
        holder.configure(items[position])
        holder.itemView.txtTitle.text = items[position].title

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpViewHolder {
        LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_support, parent, false)
                .let {
                    return HelpViewHolder(complaintListener, it)
                }
    }

    fun addObjects(objects: List<HelpList>) {
        items.addAll(objects)
        //items.clear()
        notifyDataSetChanged()
    }

    fun clearAdapter() {
        items.clear()
        //selected.clear()
        notifyDataSetChanged()
    }
}