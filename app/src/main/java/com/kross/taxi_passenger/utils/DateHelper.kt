package com.kross.taxi_passenger.utils

import android.annotation.SuppressLint
import java.sql.Time
import java.text.Format
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


object DateHelper {

    var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    val simpleDateFormatForSpinner = SimpleDateFormat("dd")

    val sdfForPhotoScan= SimpleDateFormat("dd-MM-yyyy")

    val sdfSimple= SimpleDateFormat("dd.MM.yy")

    val sdfWeekMonthDay= SimpleDateFormat("E, MMM d")

    fun getHourFromString(time: String): Int{
        var hour = 0
        val dateFormatter = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
        try {
            val c = Calendar.getInstance()
            c.time = dateFormatter.parse(time)
            hour = c.get(Calendar.HOUR_OF_DAY)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return hour
    }

    fun setCalendarData(calendar: Calendar, year: Int, monthOfYear: Int, dayOfMonth: Int){
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
    }

    fun incrementDate(numberIncrement: Int): Calendar{
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, numberIncrement)
        return calendar
    }

    fun compareLaterTwoMonth(calendar: Calendar): Boolean{
        val currentCalendar = Calendar.getInstance()
        currentCalendar.add(Calendar.MONTH, -1)
        calendar.add(Calendar.MONTH, 1)
        return calendar.after(currentCalendar)
    }

    fun setCalendarMonthAfterCompair(calendar: Calendar, monthOfYear: Int){
        calendar.set(Calendar.MONTH, monthOfYear)
    }

    private fun getStartOfWeek(): Calendar{
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
        calendar.clear(Calendar.MINUTE)
        calendar.clear(Calendar.SECOND)
        calendar.clear(Calendar.MILLISECOND)
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        return calendar
    }

    private fun getStartOfMonth(): Calendar{
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0) // ! clear would not reset the hour of day !
        calendar.clear(Calendar.MINUTE)
        calendar.clear(Calendar.SECOND)
        calendar.clear(Calendar.MILLISECOND)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return calendar
    }

    fun getStartOfWeekString(): String{
        return simpleDateFormat.format(getStartOfWeek().time)
    }

    fun getStartOfMonthString(): String{
        return simpleDateFormat.format(getStartOfMonth().time)
    }

    fun getCurrentDate(): String{
        val calendar = Calendar.getInstance()
        return simpleDateFormat.format(calendar.time)
    }

    fun compareOrdersTimeOfObtained(timeObtained: Long, timeCompare: Long = 30000): Boolean{
        return (getCalendar().timeInMillis - timeObtained) <= timeCompare
    }

    fun differenceBetweenDate(timeObtained: Long, timeWait: Long): Long{
        return timeWait - (TimeUnit.MILLISECONDS.toSeconds(getCalendar().timeInMillis - timeObtained) * 1000)
    }

    fun calculateTimeWait(startTimeWait: Long): Long{
        return (TimeUnit.MILLISECONDS.toSeconds(getCalendar().timeInMillis - startTimeWait) * 1000)
    }

    fun getCalendar(): Calendar{
        return Calendar.getInstance()
    }

    fun parseIssueExpiryLicenceDate(listDate: List<String>): List<Calendar>{
        val listCalendar = ArrayList<Calendar>()
        listDate.forEach {
            val calendar = Calendar.getInstance()
            try {
                calendar.time = sdfForPhotoScan.parse(it)
                listCalendar.add(calendar)
            }catch (exception: ParseException){
                errorLog(this, "Can't parse date")
            }
        }
        return listCalendar
    }

    private fun getPeriodOfDay(calendar: Calendar): String{
        val timeOfDay = if (calendar.get(Calendar.AM_PM) == Calendar.PM) {
            "pm"
        }else{
            "am"
        }
        return timeOfDay
    }

    fun getDateOrder(calendar: Calendar, simpleDateFormat: SimpleDateFormat, simpleDateFormatView: SimpleDateFormat,dateOrder: String?): String{
        calendar.time = simpleDateFormat.parse(dateOrder)
        return simpleDateFormatView.format(calendar.time)
    }

    fun getPeriodOrder(calendar: Calendar, simpleDateFormat: SimpleDateFormat,startTrip: String?, endTrip: String?): String{
        calendar.time = simpleDateFormat.parse(startTrip)
        val start = "${getCompleteFormatDate(calendar.get(Calendar.HOUR))}:${getCompleteFormatDate(calendar.get(Calendar.MINUTE))}"
        calendar.time = simpleDateFormat.parse(endTrip)
        return "${start}-${getCompleteFormatDate(calendar.get(Calendar.HOUR))}:${getCompleteFormatDate(calendar.get(Calendar.MINUTE))} ${getPeriodOfDay(calendar)}"
    }

    private fun getCompleteFormatDate(number: Int): String{
        if(number < 10) return String.format("%02d", number)
        return number.toString()
    }

    @SuppressLint("SimpleDateFormat")
    fun getTime(time: Long): String {
        val tme = Time(time)
        val formatter: Format
        formatter = SimpleDateFormat("h:mm a")
        return formatter.format(tme)
    }

    fun format(calendar: Calendar, simpleDateFormat: SimpleDateFormat): String{
        return simpleDateFormat.format(calendar.time)
    }
}