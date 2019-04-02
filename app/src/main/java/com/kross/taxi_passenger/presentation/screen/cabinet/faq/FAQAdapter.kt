package com.kross.taxi_passenger.presentation.screen.cabinet.faq

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.response.FAQ
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_faq.*
import kotlinx.android.synthetic.main.item_faq.view.*

// TODO: replace String with News object

class FAQViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    private lateinit var globalData: FAQ



    fun configure(data: FAQ) {
        globalData = data
    }

}

class FAQAdapter(val context: Context) : RecyclerView.Adapter<FAQViewHolder>() {
    private var disclose: Boolean = true
    private var items: MutableList<FAQ> = ArrayList()
        set(value) {
            if (value != items) {
                field = value
                notifyDataSetChanged()
            }
        }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        holder.configure(items[position])
        holder.itemView.txtTitle.text = items[position].title
        holder.itemView.txtDetail.text = items[position].text
        holder.itemView.cardFAQ.setOnClickListener {
            if (disclose){
                holder.disclosure.visibility = View.VISIBLE
                holder.hide.visibility = View.GONE
                holder.itemView.txtDetail.visibility = View.VISIBLE
                disclose = false
            }else{
                holder.disclosure.visibility = View.GONE
                holder.hide.visibility = View.VISIBLE
                holder.itemView.txtDetail.visibility = View.GONE
                disclose = true
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_faq, parent, false)
                .let {
                    return FAQViewHolder(it)
                }
    }

    fun addObjects(objects: List<FAQ>) {
        items.addAll(objects)
        notifyDataSetChanged()
    }

}