package com.kross.taxi_passenger.presentation.screen.main.bottom

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.CarEntity
import com.kross.taxi_passenger.data.repository.database.entity.DriverEntity
import com.kross.taxi_passenger.utils.getLongPreference
import kotlinx.android.synthetic.main.bottom_dialog_driver.*
import org.jetbrains.anko.runOnUiThread
import java.text.SimpleDateFormat
import java.util.*


class DriverFoundBottomDialog(viewGroup: ViewGroup, val context: Context?, private val driverEntity: DriverEntity, private val carEntity: CarEntity, private val isArrived: Boolean) : BottomDialog(viewGroup, R.layout.bottom_dialog_driver) {

    val TIME_FOR_WAIT = 1000000 * 60
    var cancelTripListener: (() -> Unit)? = null
    var phoneListener: (() -> Unit)? = null

    init {
        txtCancelDriverFound.setOnClickListener { cancelTripListener?.invoke() }
        icPhone.setOnClickListener { phoneListener?.invoke() }
        setupLayoutForDriver()
    }


    @SuppressLint("SetTextI18n")
    private fun setupLayoutForDriver() {
        if (context != null) {
//            Glide.with(context)
//                    .load(this)
//                    .into(icDriversPhoto)
        }
        txtDriversRating.text = driverEntity.rating.toString()
        txtNameDriver.text = driverEntity.firstName + " " + driverEntity.lastName
        txtCarDriver.text = carEntity.make + " " + carEntity.model
        txtDriverCarNumber.text = carEntity.plate_number

        if (isArrived) {
            setWaitingForPassangerLayout()
        }
    }


    private fun setWaitingForPassangerLayout() {
        txtDriverOnWay.text = context?.getString(R.string.minutes_waiting)
        timerTv.visibility = View.VISIBLE

        val calendar: Calendar = Calendar.getInstance()
        val fromDate = context?.getLongPreference(R.string.arrived_key)
        calendar.timeInMillis = fromDate!!
        startTimer(calendar.time, 1000)
    }

    private fun startTimer(fromDate: Date, period: Long) {
        val everySunday = Timer()
        val timertask = object : TimerTask() {
            override fun run() {
                val calendar = Calendar.getInstance()
                    showMessage(timeString(calendar.timeInMillis - fromDate.time))
            }
        }
        everySunday.scheduleAtFixedRate(timertask, fromDate, period)

    }

    fun showMessage(time: String) {
        context?.runOnUiThread {
            timerTv.text = time
        }
    }


    // Method to get days hours minutes seconds from milliseconds
    @SuppressLint("SimpleDateFormat")
    private fun timeString(millis: Long): String {
        return (SimpleDateFormat("mm:ss")).format(Date(millis));
    }

}



