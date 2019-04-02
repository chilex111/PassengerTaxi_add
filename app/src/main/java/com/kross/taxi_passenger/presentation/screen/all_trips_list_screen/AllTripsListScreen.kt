package com.kross.taxi_passenger.presentation.screen.all_trips_list_screen

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateFormat
import android.view.MenuItem
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.response.TripsStatistic
import com.kross.taxi_passenger.domain.AllTripsViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.utils.DateHelper
import com.kross.taxi_passenger.utils.getStringPreference
import kotlinx.android.synthetic.main.activity_all_trips_list_screen.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class AllTripsListScreen : BaseActivity(), View.OnClickListener {

    private var dateAndTime = Calendar.getInstance()


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.datePickIv -> {
                openDatePicker()
            }
        }
    }


    private fun openDatePicker() {
        val dialog = DatePickerDialog(this,
                dateListener,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
        dialog.datePicker.maxDate = System.currentTimeMillis()
        dialog.show()
    }

    private val dateListener: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        DateHelper.setCalendarData(dateAndTime, year, monthOfYear - 1, dayOfMonth)
        compareChosenDate(dateAndTime)
        dateAndTime = Calendar.getInstance()
    }

    private fun compareChosenDate(calendar: Calendar) {
        if (!DateHelper.compareLaterTwoMonth(calendar)) {
        } else {
            getChosenDate(dateAndTime.time)
        }
    }

    private fun getChosenDate(date: Date) {
        updateItems(DateHelper.simpleDateFormat.format(date))
        date_pick_tv.text = DateHelper.simpleDateFormat.format(date)
    }

    private val viewModel: AllTripsViewModel by viewModel()
    var id = 0
    private var adapter: TripsRouteAdapter? = null

    companion object {

        const val CAR_TRIPS: String = "car_trips"

        fun start(activity: Activity, carId: Int) {
            val intent = Intent(activity, AllTripsListScreen::class.java)
            intent.putExtra(CAR_TRIPS, carId)
            activity.startActivity(intent)
        }
    }

    private fun initAppBar() {
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Trips"
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun updateItems(date: String) {
        viewModel.getCarTripsStatistic(getStringPreference(R.string.token), id, date).observe(this, android.arch.lifecycle.Observer {
            if (it != null) {
                setUpViews(it)
                it.trips?.let { it1 ->
                    if (it1.isNotEmpty()) {
                        rvTripsAll.visibility = View.VISIBLE
                        tvNoTrips.visibility = View.GONE
                        adapter?.setItems(it1)
                    } else {
                        rvTripsAll.visibility = View.GONE
                        tvNoTrips.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun setLoading() {
        viewModel.getLiveDataLoading().observe(this, android.arch.lifecycle.Observer { it ->
            it?.let { progressBarTrips.visibility = it }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_trips_list_screen)
        initAppBar()
        id = intent.getIntExtra(CAR_TRIPS, -1)
        datePickIv.setOnClickListener(this)
        adapter = TripsRouteAdapter(layoutInflater, this)

        ViewCompat.setNestedScrollingEnabled(rvTripsAll, false)
        rvTripsAll.layoutManager = LinearLayoutManager(this)
        rvTripsAll.adapter = adapter

        setLoading()
        updateItems(getDate())

    }

    private fun setUpViews(tripsStatistic: TripsStatistic?) {
        dayCountTv.text = tripsStatistic?.tripsStatistic?.today.toString()
        weekCountTv.text = tripsStatistic?.tripsStatistic?.week.toString()
        monthCountTv.text = tripsStatistic?.tripsStatistic?.month.toString()
    }

    private fun getDate(): String {
        val calendar = Calendar.getInstance(TimeZone.getDefault())

        val date = calendar.time
        val monthNumber = DateFormat.format("MM", date) as String // 06
        val dayNumber = DateFormat.format("dd", date) as String // date
        val year = DateFormat.format("yyyy", date) as String // 2013

        return "$year-$monthNumber-$dayNumber"
    }
}
