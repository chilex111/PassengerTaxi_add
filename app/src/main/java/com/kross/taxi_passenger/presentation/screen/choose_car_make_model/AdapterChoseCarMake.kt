package com.test.client_taxi_kross_driver_android.presentation.ui.choose_car_make_model

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.widget.BaseAdapter
import com.test.client_taxi_kross_driver_android.data.server.pojo.response.Make
import kotlinx.android.synthetic.main.item_rv_car_make_model.view.*
import java.util.*
import java.util.regex.Pattern

class AdapterChoseCarMake() : BaseAdapter<ViewHolderCarMake, Make>(){


    private val listCopy = ArrayList<Make>()

    private var searchText = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCarMake {
        val view = LayoutInflater.from(parent.context).inflate(getLayout(), parent, false)
        return ViewHolderCarMake(view)
    }

    override fun onBindViewHolder(holder: ViewHolderCarMake, position: Int) {
        super.onBindViewHolder(holder, position)
        val make = super.list.get(position)
        holder.bind(make)
        highlightedSearchText(holder, make)
    }

    private fun highlightedSearchText(holder: ViewHolderCarMake, carMake: Make){
        val spannable = SpannableStringBuilder(carMake.makeName)
        val word = Pattern.compile(searchText.toLowerCase())
        val match = word.matcher(carMake.makeName.toLowerCase())

        while (match.find()) {
            // todo: set color highlight if we need
//            spannable.setSpan(ForegroundColorSpan(Color.MAGENTA), match.start(), match.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(StyleSpan(Typeface.BOLD), match.start(), match.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        holder.itemView.txtCarModelMake.setText(spannable)
    }

    override fun getLayout(): Int {
        return R.layout.item_rv_car_make_model
    }

    fun filter(text: String, listNotFilter: List<Make>?): Boolean{
        listNotFilter?.let { listCopy.addAll(it) }
        list.clear()
        list.addAll(listCopy.filter { it.makeName.contains(text, true) || it.makeName.contains(text, true) })
        listCopy.clear()
        searchText = text
        notifyDataSetChanged()
        val result = if (list.size == 0)  false else true
        return result
    }

}