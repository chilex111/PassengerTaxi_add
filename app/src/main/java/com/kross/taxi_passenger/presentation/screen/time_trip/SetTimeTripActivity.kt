package com.kross.taxi_passenger.presentation.screen.time_trip

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.utils.DateHelper
import kotlinx.android.synthetic.main.activity_set_time_trip.*
import java.util.*
import android.app.TimePickerDialog
import android.text.format.DateUtils
import com.kross.taxi_passenger.utils.showSnack


class SetTimeTripActivity : BaseActivity(), View.OnClickListener {

    companion object {
        const val TIME_DATE_TRIP = "TIME_DATE_TRIP"

        fun start(fragment: Fragment, requestCode: Int) {
            val intent = Intent(fragment.context, SetTimeTripActivity::class.java)
            fragment.startActivityForResult(intent, requestCode)
        }

        fun start(activity: Activity, requestCode: Int) {
            val intent = Intent(activity, SetTimeTripActivity::class.java)
            activity.startActivityForResult(intent, requestCode)
        }

    }

    private var menuItemSave: MenuItem? = null

    private var date = Calendar.getInstance()

    private var time = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_time_trip)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        setListenersViews()
    }

    private fun setListenersViews(){
        txtSetTime.setOnClickListener(this)
        txtSetDate.setOnClickListener(this)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.menuActionSaveAddCard)?.isEnabled = false
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_card_screen, menu)
        menuItemSave = menu?.findItem(R.id.menuActionSaveAddCard)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> finish()
            R.id.menuActionSaveAddCard -> {
                date.set(Calendar.HOUR_OF_DAY, time[Calendar.HOUR_OF_DAY])
                date.set(Calendar.MINUTE, time[Calendar.MINUTE])
                deliverResult(DateHelper.format(date, DateHelper.sdf))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.txtSetTime -> showTimePickerDialog()
            R.id.txtSetDate -> showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog(){
        val calendar = Calendar.getInstance()
        DatePickerDialog(this,
                R.style.DatePickerDialogTheme,
                dateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).apply {
            datePicker.minDate = DateHelper.getCalendar().timeInMillis
            datePicker.maxDate = DateHelper.incrementDate(4).timeInMillis
            setTitle("")
            show()
        }
    }

    private val dateListener: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        checkDate(year, monthOfYear, dayOfMonth)
        enableSaveButton()
    }

    private fun checkDate(year: Int, monthOfYear: Int, dayOfMonth: Int){
        if(date[Calendar.HOUR_OF_DAY] <= time[Calendar.HOUR_OF_DAY]){
            setChosenDate(year, monthOfYear, dayOfMonth)
        }else{
            showSnack("Can't add current date with time later than now")
        }
    }

    private fun setChosenDate(year: Int, monthOfYear: Int, dayOfMonth: Int){
        DateHelper.setCalendarData(date, year, monthOfYear, dayOfMonth)
        txtSetDate.text = "${DateHelper.format(date, DateHelper.sdfWeekMonthDay)}"
    }

    private fun showTimePickerDialog(){
        val calendar = Calendar.getInstance()
        TimePickerDialog(this,
                R.style.TimePickerWidgetStyle,
                timeListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false).apply {
            setTitle("")
            show()
        }
    }

    var timeListener: TimePickerDialog.OnTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        if(DateUtils.isToday(date.timeInMillis)){
            checkTime(hourOfDay, minute)
        }else{
            setChosenTime(hourOfDay, minute)
        }
        enableSaveButton()
    }

    private fun checkTime(hourOfDay: Int, minute: Int){
        if(hourOfDay > date[Calendar.HOUR_OF_DAY]){
            setChosenTime(hourOfDay, minute)
        }else{
            showSnack("Can't add time earlier than now")
        }
    }

    private fun setChosenTime(hourOfDay: Int, minute: Int){
        time.set(Calendar.HOUR_OF_DAY, hourOfDay)
        time.set(Calendar.MINUTE, minute)
        txtSetTime.text = DateHelper.getTime(time.timeInMillis)
    }

    private fun enableSaveButton(){
        menuItemSave?.isEnabled = txtSetDate.text != getString(R.string.txt_today) && txtSetTime.text != getString(R.string.txt_now)
    }

    fun deliverResult(date: String) {
        Intent().apply {
            putExtra(TIME_DATE_TRIP, date)
            setResult(Activity.RESULT_OK, this)
        }
        finish()
    }

}