package com.kross.taxi_passenger.presentation.screen.car_details_screen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kross.taxi_passenger.R

class SpinnerArrayAdapter(context: Context, resource: Int, list: ArrayList<String>) : ArrayAdapter<String>(context, resource, list) {


    var resource: Int
    var list: ArrayList<String>
    var layoutInflater: LayoutInflater
    private val DATE_PICKER_POSITION = 4

    init {
        this.resource = resource
        this.list = list
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v: View? = null
        if (position === DATE_PICKER_POSITION ) {
            val tv = TextView(context)
            tv.visibility = View.GONE
            v = tv
        } else {
            v = super.getDropDownView(position, null, parent)
        }
        return v!!

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var holder: ViewHolder
        var retView: View

        if (convertView == null) {
            retView = layoutInflater.inflate(resource, null)
            holder = ViewHolder()

            holder.text = retView.findViewById(R.id.textOfSpintItem) as TextView?
            holder.text?.text = list.get(position)
            retView.tag = holder

        } else {
            holder = convertView.tag as ViewHolder
            retView = convertView
        }

        return retView
    }

     fun setItemText(text: String) {
        list[4] = text
        notifyDataSetChanged()
    }

    internal class ViewHolder {
        var text: TextView? = null
    }

}