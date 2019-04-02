package com.kross.taxi_passenger.data.repository.database.entity

import android.Manifest
import android.app.Activity
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import com.kross.taxi_passenger.KrossApp
import com.kross.taxi_passenger.presentation.screen.trip_details.REQUEST_CALL

@Entity(tableName = "drivers")
class DriverEntity(
        @PrimaryKey
        val id: Int?,
        @ColumnInfo(name = "first_name") val firstName: String?,
        @ColumnInfo(name = "last_name") val lastName: String?,
        @ColumnInfo(name = "phone_number") val phoneNumber: String?,
        @ColumnInfo(name = "driver_since") val driverSince: String?,
        @ColumnInfo(name = "photo") val photo: String?,
        @ColumnInfo(name = "country") val country: String?,
        @ColumnInfo(name = "city") val city: String?,
        @ColumnInfo(name = "rating") val rating: Float?,
        @ColumnInfo(name = "driving_from") val drivingFrom: String?) {

    fun callToDriver(activity: Activity) {
        val checkPermission = KrossApp.applicationContext.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CALL_PHONE) }
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            KrossApp.applicationContext.let {
                KrossApp.applicationContext.let { it1 ->
                    ActivityCompat.requestPermissions(
                           activity,
                            arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL)
                    KrossApp.applicationContext
                }
            }
        } else {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$phoneNumber")
            startActivity(KrossApp.applicationContext, callIntent, null)
        }
    }
}


