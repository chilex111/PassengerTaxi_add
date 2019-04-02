package com.kross.taxi_passenger.presentation.widget

import android.support.v7.widget.RecyclerView
import android.view.View


abstract class BaseAdapter<VH : RecyclerView.ViewHolder, T> : RecyclerView.Adapter<VH>(){

    val list = ArrayList<T>()

    private lateinit var itemClick: OnItemClickListener<T>

    private val internalListener = View.OnClickListener {
        val item = it.tag as T
        itemClick.onItemClick(it, item)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.tag = list.get(position)
        holder.itemView.setOnClickListener(internalListener)
    }

    fun getItem(position: Int): T = list.get(position)

    override fun getItemCount(): Int = list.size

    open fun add(item: T){
        list.add(item)
        notifyDataSetChanged()
    }

    open fun addAll(list: List<T>){
        for (item in list){
            add(item)
        }
    }

    open fun remove(item: T){
        val position = list.indexOf(item)
        if(position > -1){
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    open fun clear(){
        list.clear()
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: OnItemClickListener<T>){
        itemClick = listener
    }

    interface OnItemClickListener<T>{

        fun onItemClick(view: View, item: T)
    }

    abstract fun getLayout(): Int

}