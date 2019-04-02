package com.kross.taxi_passenger.presentation.screen.drivers_list

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.response.Driver
import com.kross.taxi_passenger.presentation.widget.BaseAdapter
import kotlinx.android.synthetic.main.driver_list_item.view.*
import kotlinx.android.synthetic.main.item_rv_car_make_model.view.*
import java.util.*
import java.util.Calendar.*
import java.util.regex.Pattern


class DriversListAdapter(val itemClick: OnItemClickListener, context: Context) : BaseAdapter<ViewHolderDriversList, Driver>() {


    private val listCars = ArrayList<Driver>()
    private val context = context

    private var searchText = ""
    private val internalListener = View.OnClickListener {
        val item = it.tag as Driver
        itemClick.onItemClick(it, item.id)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDriversList {
        val view = LayoutInflater.from(parent.context).inflate(getLayout(), parent, false)
        return ViewHolderDriversList(view)
    }

    // override fun getItemCount() = listCars.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderDriversList, position: Int) {
        super.onBindViewHolder(holder, position)
        val driverObj = super.list.get(position)
        holder.bind(driverObj, internalListener)
        highlightedSearchText(holder, driverObj)
        Glide.with(context)
                .asBitmap()
                .load(driverObj.photo)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.itemView.userPhoto)
        holder.itemView.userNameTv.text = driverObj.firstName + " " + driverObj.lastName
        //   holder.itemView.driverLocation.
        // holder.itemView.carPlateNumberTv1.text = carObj.car?.plateNumber
        if (driverObj.city == null && driverObj.country == null) {
            holder.itemView.driverLocation.text = context.getString(R.string.unknown_location)
        } else {
            holder.itemView.driverLocation.text = driverObj.country + ", " + driverObj.city
        }

        if (driverObj.rating!! > 0) {
            holder.itemView.layoutWithRating.visibility = View.VISIBLE
            holder.itemView.ratingNew.visibility = View.GONE
            holder.itemView.rating.text = String.format("%.1f", driverObj.rating)
        } else {
            holder.itemView.layoutWithRating.visibility = View.GONE
            holder.itemView.ratingNew.visibility = View.VISIBLE
        }


        holder.itemView.driverExerience.text = context.getString(R.string.drive_from) + driverObj.driving_from//getDiffYears(newDate, now).toString() + " years of experience"
    }


    fun getDiffYears(first: Date, last: Date): Int {
        val a = getCalendar(first)
        val b = getCalendar(last)
        var diff = b.get(YEAR) - a.get(YEAR)
        if (a.get(MONTH) > b.get(MONTH) || a.get(MONTH) === b.get(MONTH) && a.get(DATE) > b.get(DATE)) {
            diff--
        }
        return diff
    }

    fun getCalendar(date: Date): Calendar {
        val cal = Calendar.getInstance()
        cal.time = date
        return cal
    }

    override fun getLayout(): Int {
        return R.layout.driver_list_item
    }

    private fun highlightedSearchText(holder: ViewHolderDriversList, driver: Driver) {
        val spannable = SpannableStringBuilder(driver.firstName + " " + driver.lastName)
        val word = Pattern.compile(searchText.toLowerCase())
        val match = word.matcher(driver.firstName.toLowerCase() + " " + driver.lastName.toLowerCase())

        while (match.find()) {
            // todo: set color highlight if we need
            spannable.setSpan(ForegroundColorSpan(Color.BLACK), match.start(), match.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(StyleSpan(Typeface.BOLD_ITALIC), match.start(), match.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        holder.itemView.userNameTv.text = spannable
    }

    fun filter(text: String, listNotFilter: List<Driver>): Boolean {

        listNotFilter.let {
            listCars.addAll(it) }
        list.clear()
        list.addAll(listCars.filter { it.firstName.contains(text, true) || it.lastName.contains(text, true) })
        listCars.clear()
        searchText = text
        notifyDataSetChanged()

        return list.size != 0
    }

    interface OnItemClickListener {

        fun onItemClick(view: View, id: Int)
    }

}