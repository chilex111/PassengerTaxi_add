package com.kross.taxi_passenger.presentation.screen.car_details_screen

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.format.DateFormat
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.PopupMenu
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.CarEntity
import com.kross.taxi_passenger.domain.CarInfoViewModel
import com.kross.taxi_passenger.presentation.screen.all_trips_list_screen.AllTripsListScreen
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.car_location.CarLocationActivity
import com.kross.taxi_passenger.presentation.screen.cars_list.FullCarInfo
import com.kross.taxi_passenger.presentation.screen.driver_details.DriverDetailsActivity
import com.kross.taxi_passenger.presentation.screen.drivers_list.DriversListActivity
import com.kross.taxi_passenger.utils.AlertDialogHelper
import com.kross.taxi_passenger.utils.DateHelper
import com.kross.taxi_passenger.utils.getStringPreference
import io.reactivex.functions.Action
import kotlinx.android.synthetic.main.activity_car_details_screen.*
import kotlinx.android.synthetic.main.activity_car_details_screen.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CarDetailsScreen : BaseActivity(), AdapterView.OnItemSelectedListener, View.OnClickListener {
    lateinit var adapter: SpinnerArrayAdapter

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.drivers_list_click -> {
                DriversListActivity.start(this@CarDetailsScreen, id, false)
                overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
            }

            R.id.layoutWithDriver -> {
                DriverDetailsActivity.start(this@CarDetailsScreen, idDriver, id, true, false, 1111)
                overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
                onBackPressed()
            }
            R.id.changeDriver -> {
                DriversListActivity.start(this@CarDetailsScreen, id, true)
                overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
                onBackPressed()
            }
            R.id.delete_menu -> {
                createPopUp()
            }
            R.id.prewIcon -> {
                monthNumber--
                setChartData(setMonthDate(monthNumber))
                nextIconVisibility()
            }
            R.id.nextIcon -> {
                if (monthNumber < 0) {
                    monthNumber++
                    nextIconVisibility()
                    setChartData(setMonthDate(monthNumber))
                }
            }
            R.id.allTripsTv -> {
                AllTripsListScreen.start(this@CarDetailsScreen, id)
                overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
            }
        }
    }

    private fun nextIconVisibility() {
        if (monthNumber >= 0) {
            nextIcon.visibility = View.INVISIBLE
        } else {
            nextIcon.visibility = View.VISIBLE
        }
    }


    var id: Int = 0
    var monthNumber: Int = 0
    private var idDriver: Int = 0
    private var carsObj: FullCarInfo? = null
    private val viewModel: CarInfoViewModel by viewModel()
    private val FILTER_ALL = 0
    private val FILTER_MONTH = 1
    private val FILTER_WEEK = 2
    private val FILTER_CHOOSE_DATE = 3
    private var dateAndTime = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_details_screen)
        initAppBar()
        id = intent.getIntExtra(CAR_DETAILS, -1)

        viewModel.getCar(id)?.let { setData(it) }

        setUpText()
        location_click.setOnClickListener {
            carsObj?.locationEntity?.last?.let { it1 -> CarLocationActivity.start(this@CarDetailsScreen, it1, 0, null) }
            overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
        }
        nextIconVisibility()
        setListenersViews()
        setChartData(setMonthDate(monthNumber))
    }


    private fun setMonthDate(amoutn: Int): String {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.add(Calendar.MONTH, amoutn)

        val date = calendar.time
        val monthNumber = DateFormat.format("MM", date) as String // 06
        val year = DateFormat.format("yyyy", date) as String // 2013

        return "$year-$monthNumber"
    }

    private fun butSpinnerItemSelection() {
        spinner.setSelection(4)
    }

    private fun createPopUp() {
        val popup = PopupMenu(this, delete_menu)
        popup.inflate(R.menu.menu_car_details)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.deleteAction -> {
                    AlertDialogHelper.createDialog(this,
                            null,
                            R.string.delete_car_text,
                            getString(R.string.no),
                            getString(R.string.yes),
                            Action {
                                viewModel.deleteCar(getStringPreference(R.string.token), id)
                                finish()
                            })
                }
            }
            false
        }
        //displaying the popup
        popup.show()
    }


    private fun setChartData(date: String) {

        month_name.text = getMonth(date)
        chart.clear()


        viewModel.getCarChart(getStringPreference(R.string.token),
                id,
                date).observe(this, android.arch.lifecycle.Observer{
            val map: Map<String, Int>? = it?.body()
            val r = Random()
            var count = 0
            val entries = ArrayList<Entry>()
            map?.forEach { (key, _) ->
                entries.add(Entry(key.toFloat(), 0 + r.nextFloat() * (4000 - 0)))
                count++
            }
            if (count < 31) {
                for (i in count..31) {
                    entries.add(Entry(i.toFloat(), 0f))
                }
            }
            setUpChart(entries)
        })

    }


    @SuppressLint("SimpleDateFormat")
    private fun getMonth(date: String): String {
        val d = SimpleDateFormat("yyyy-MM", Locale.ENGLISH).parse(date)
        val cal = Calendar.getInstance()
        cal.time = d
        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH).toUpperCase() + " " + SimpleDateFormat("yyyy").format(cal.time).toUpperCase()
    }


    private fun setUpChart(entries: ArrayList<Entry>) {
        chart.axisRight.isEnabled = false
        chart.axisRight.spaceBottom = 0f
        setBottomXAxisUi()
        setLeftYAxisUi()
        val dataSet = LineDataSet(entries, null)
        dataSet.setDrawCircleHole(false)
        dataSet.setCircleColor(Color.parseColor("#75a6ff"))
        dataSet.lineWidth = 4f
        dataSet.color = Color.parseColor("#75a6ff")
        dataSet.valueTextSize = 0f
        dataSet.axisDependency = YAxis.AxisDependency.RIGHT
        val lineData = LineData(dataSet)
        chart.isScaleXEnabled = false
        chart.isScaleXEnabled = false
        chart.data = lineData
        chart.description = null
        chart.notifyDataSetChanged()
        chart.invalidate()
    }

    fun callToDriverD(v: View) {
        carsObj?.driver?.callToDriver(this)
//        val checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
//        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL)
//        } else {
//            val callIntent = Intent(Intent.ACTION_CALL)
//            callIntent.data = Uri.parse("tel:" + driverPhone.text)
//            startActivity(callIntent)
//        }
    }


    private fun setLeftYAxisUi() {
        val left = chart.axisLeft
        left.removeAllLimitLines()
        left.setLabelCount(10, false)
        left.axisMinimum = 0f
        left.spaceBottom = 0f
        left.axisMaximum = 4000f
        left.textSize = 7f
        left.setDrawAxisLine(false)
        left.gridLineWidth = 2f
        left.gridColor = Color.parseColor("#d7dee7")
    }

    private fun setBottomXAxisUi() {
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(true)
        xAxis.setLabelCount(31, false)
        xAxis.gridLineWidth = 2f
        xAxis.textSize = 7f
        xAxis.setDrawLabels(true)
        xAxis.gridColor = Color.parseColor("#d7dee7")
    }

    private fun setListenersViews() {
        spinner.onItemSelectedListener = this
        var filliste = resources.getStringArray(R.array.dates_arrays)
        adapter = SpinnerArrayAdapter(this, R.layout.custom_spinner_item, castArrayToStringList(filliste))
        spinner.adapter = adapter
        adapter.notifyDataSetChanged()
        drivers_list_click.setOnClickListener(this)
        changeDriver.setOnClickListener(this)
        layoutWithDriver.setOnClickListener(this)
        delete_menu.setOnClickListener(this)
        prewIcon.setOnClickListener(this)
        nextIcon.setOnClickListener(this)
        allTripsTv.setOnClickListener(this)
    }

    private fun castArrayToStringList(array: Array<String>): ArrayList<String> {
        val list: ArrayList<String> = ArrayList()
        for (item in array) {
            list.add(item)
        }
        return list;
    }

    @SuppressLint("SetTextI18n")
    private fun setUpText() {
        firstLastNameTv1.text = carsObj?.car?.make + " " + carsObj?.car?.model
        driverPlateTv1.text = carsObj?.car?.plate_number
        supportActionBar?.title = "" + carsObj?.car?.make + " " + carsObj?.car?.model
        carsObj?.car?.year.let { yearOfExperienceTv1.text = "" + carsObj?.car?.year + " year of manufacture" }
        tripsMade.text = carsObj?.car?.tripsCount.toString()
        money.text = "₦" + carsObj?.car?.moneyEarned.toString()


        setImage(carsObj?.car?.photo)
        Glide.with(this)
                .asBitmap()
                .load(carsObj?.driver?.photo)
                .apply(RequestOptions.circleCropTransform())
                .into(carPhoto)
        if (setUpVisibility()) {
            driver_first_last_name.text = carsObj?.driver?.firstName + " " + carsObj?.driver?.lastName
            driverPhone.driverPhone.text = carsObj?.driver?.phoneNumber
            sinceDate.text = "Your driver since " + carsObj?.driver?.driverSince
        }

        money.text = "₦" + carsObj?.car?.moneyEarned
        tripsMade.text = "" + carsObj?.car?.tripsCount
    }

    private fun setImage(url: String?) {
        Glide.with(this)
                .asBitmap()
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .into(driverPhotoDetails1)
    }


    private fun setUpVisibility(): Boolean {
        if (carsObj?.driver != null) {
            layoutWithDriver.visibility = View.VISIBLE
            layoutWithoutDriver.visibility = View.GONE
            changeDriver.visibility = View.VISIBLE
            return true
        }
        return false
    }


    private fun setLoading() {
        viewModel.getLiveDataLoading().observe(this, android.arch.lifecycle.Observer{ it ->
            it?.let { progressBarDate.visibility = it }
        })
    }

    private fun initAppBar() {
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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


    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            FILTER_ALL -> {
                setUpText()   //   getWalletWeekStatistic() setUpText()
            }
            FILTER_MONTH -> {
                getCarStatistic(DateHelper.getStartOfMonthString(),
                        DateHelper.getCurrentDate())
            }
            FILTER_WEEK -> {
                getCarStatistic(DateHelper.getStartOfWeekString(),
                        DateHelper.getCurrentDate())
            }
            FILTER_CHOOSE_DATE -> {
                openDatePicker()
                butSpinnerItemSelection()
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

    val dateListener: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        DateHelper.setCalendarData(dateAndTime, year, monthOfYear, dayOfMonth)
        compareChosenDate(dateAndTime, monthOfYear)
        dateAndTime = Calendar.getInstance()
    }

    private fun compareChosenDate(calendar: Calendar, monthOfYear: Int) {
        if (!DateHelper.compareLaterTwoMonth(calendar)) {
        } else {
            DateHelper.setCalendarMonthAfterCompair(dateAndTime, monthOfYear)
            spinner.prompt = DateHelper.simpleDateFormat.format(dateAndTime.time)
            getWalletStatisticChosenDate(dateAndTime.time, calendar)
        }
    }

    private fun getWalletStatisticChosenDate(date: Date, calendar: Calendar) {
        getCarStatistic(DateHelper.simpleDateFormat.format(date), DateHelper.simpleDateFormat.format(date))
        adapter.setItemText(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + ", " + DateHelper.simpleDateFormatForSpinner.format(date))
    }


    private fun getCarStatistic(dateFrom: String, dateTo: String) {
        setLoading()
        progressBarDate.visibility = View.VISIBLE
        viewModel.getCarStatistic(getStringPreference(R.string.token),
                id,
                dateFrom,
                dateTo).observe(this, android.arch.lifecycle.Observer { it?.trips_count?.let { it1 -> setStatData(it1, it.money_earned) } })
    }


    private fun setStatData(tripsCount: Int, moneyEarned: Float) {
        tripsMade.text = tripsCount.toString()
        money.text = "₦" + moneyEarned.toString()

        Log.d("DDD", tripsCount.toString())
    }

    private fun setData(car: CarEntity) {
        carsObj = FullCarInfo(false)
        carsObj?.car = car
        carsObj?.driver = car.driverId?.let { viewModel.getDrivers(it) }
        carsObj?.locationEntity = car.locationId?.let { viewModel.getLocation(it) }
        if (carsObj?.driver != null) idDriver = carsObj?.driver?.id!!
//        tripsMade.text = carsObj?.car?.tripsCount.toString()
//        money.text = carsObj?.car?.moneyEarned.toString()
        Log.d("DDD", carsObj?.car?.tripsCount.toString())
        // progressBarDate.visibility = View.GONE
        setUpText()
    }

    companion object {

        const val CAR_DETAILS: String = "car_details"

        fun start(activity: Activity, carId: Int) {
            val intent = Intent(activity, CarDetailsScreen::class.java)
            intent.putExtra(CAR_DETAILS, carId)
            activity.startActivity(intent)
        }
    }
}
