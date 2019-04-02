package com.test.client_taxi_kross_driver_android.presentation.ui.choose_car_make_model

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.widget.BaseAdapter
import com.test.client_taxi_kross_driver_android.data.server.pojo.response.ModelCar
import kotlinx.android.synthetic.main.item_rv_car_make_model.view.*
import java.util.regex.Pattern


class AdapterChooseCarModel(val itemClick: OnItemClickListener) : BaseAdapter<ViewHolderCarModel, ModelCar>() {

    private val listCopy = ArrayList<ModelCar>()

    private var searchText = ""

    private val internalListener = View.OnClickListener {
        val item = it.tag as ModelCar
        itemClick.onItemClick(it, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCarModel {
        val view = LayoutInflater.from(parent.context).inflate(getLayout(), parent, false)
        return ViewHolderCarModel(view)
    }

    override fun onBindViewHolder(holder: ViewHolderCarModel, position: Int) {
        val carModel = super.list.get(position)
        holder.bind(carModel, internalListener)
        highlightedSearchText(holder, carModel)
    }

    private fun highlightedSearchText(holder: ViewHolderCarModel, carModel: ModelCar){
        val spannable = SpannableStringBuilder(carModel.modelName)
        val word = Pattern.compile(searchText.toLowerCase())
        val match = word.matcher(carModel.modelName.toLowerCase())

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

    fun filter(text: String, listNotFilter: List<ModelCar>?): Boolean{
        listNotFilter?.let { listCopy.addAll(it) }
        list.clear()
        list.addAll(listCopy.filter { it.modelName.contains(text, true) || it.modelName.contains(text, true) })
        listCopy.clear()
        searchText = text
        notifyDataSetChanged()
        val result = if (list.size == 0)  false else true
        return result
    }

    interface OnItemClickListener{

        fun onItemClick(view: View, carModel: ModelCar)
    }
}