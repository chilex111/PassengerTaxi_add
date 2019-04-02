package com.kross.taxi_passenger.presentation.screen.driver_details

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.response.Driver
import com.kross.taxi_passenger.data.repository.server.pojo.response.DriversList
import com.kross.taxi_passenger.domain.DriverDetailsViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.trip_details.REQUEST_CALL
import com.kross.taxi_passenger.utils.AlertDialogHelper
import com.kross.taxi_passenger.utils.getStringPreference
import io.reactivex.functions.Action
import kotlinx.android.synthetic.main.activity_driver_details.*
import org.koin.android.viewmodel.ext.android.viewModel

class DriverDetailsActivity : BaseActivity(), View.OnClickListener {


    private val adapterDriverComments: DriverCommetnsAdapter = DriverCommetnsAdapter(this)
    private val viewModel: DriverDetailsViewModel by viewModel()
    var id = 0
    var carId = 0
    var releaseKey = false
    var changeKey = false

    companion object {

        const val DRIVERS_DETAILS: String = "drivers_details"
        const val CAR_ID: String = "car_id"
        const val CHANGE_KEY: String = "change_key"
        const val RELEASE_KEY: String = "release_key"

        fun start(activity: Activity, id: Int, carId: Int, releaseKey: Boolean, changeKey: Boolean, requestCode: Int) {
            val intent = Intent(activity, DriverDetailsActivity::class.java)
            intent.putExtra(DRIVERS_DETAILS, id)
            intent.putExtra(CAR_ID, carId)
            intent.putExtra(RELEASE_KEY, releaseKey)
            intent.putExtra(CHANGE_KEY, changeKey)
            if (changeKey) activity.startActivityForResult(intent, requestCode)
            else activity.startActivity(intent)
        }
    }

    fun callToDriverP(v: View) {
        val checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL)
        } else {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:" + driverPhoneTvDet.text)
            startActivity(callIntent)
        }
    }

    private fun sentResultPhotoPath(key: Boolean) {
        val intent = Intent(this, DriversList::class.java)
        intent.putExtra(getString(R.string.KEY_INTENT_PHOTO_PATH), key)
        setResult(Activity.RESULT_OK, intent)
        finish()
        overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_details)
        initAppBar()
        id = intent.getIntExtra(DRIVERS_DETAILS, 0)
        carId = intent.getIntExtra(CAR_ID, 0)
        changeKey = intent.getBooleanExtra(CHANGE_KEY, false)
        releaseKey = intent.getBooleanExtra(RELEASE_KEY, false)
        btnAssignDriver.setOnClickListener(this)
        comments.layoutManager = LinearLayoutManager(this)
        comments.adapter = adapterDriverComments

        ViewCompat.setNestedScrollingEnabled(comments, false)
        if (id != 0)
            viewModel.getDriver(getStringPreference(R.string.token), id, 10).observe(this, Observer {
                if (it != null) {
                    setUpViewsData(it)
                }
                it?.reviews?.items?.let { it1 ->
                    adapterDriverComments.addAll(it1)
                }
                Log.d("SIZEEEE", it?.reviews?.items?.size.toString())
            })

        viewModel.getLiveDataLoad().observe(this, Observer {
            it?.let { progressBarDriver.visibility = it }
        })
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAssignDriver -> {
                if (changeKey) {
                    showDialog()
                } else {
                    if (!releaseKey) {
                        assignDriver()
                    } else {
                        releaseDriver(false)
                    }
                }

            }
        }
    }

    private fun showDialog() {
        AlertDialogHelper.createDialog(this,
                null,
                R.string.change_alert,
                getString(R.string.no_no),
                resources.getString(R.string.txt_auth_screen_dialog_pos_btn),
                Action { releaseDriver(true) })
    }

    private fun releaseDriver(chageKey: Boolean) {
        var driverId: Int = 0;
        driverId = if (chageKey) {
            viewModel.getDriverId(carId).toInt()
        } else {
            id
        }

        viewModel.releaseDriver(getStringPreference(R.string.token), driverId, carId).observe(this, Observer {
            if (it!!) {
                if (!chageKey) {
                    onBackPressed()
                    overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
                    Toast.makeText(this, "The driver was released", Toast.LENGTH_SHORT).show()
                } else {
                    sentResultPhotoPath(true)
                    assignDriver()
                }
            }
        })

    }

    fun assignDriver() {
        viewModel.assignDriver(getStringPreference(R.string.token), id, carId).observe(this, Observer {
            if (it!!) {
                Toast.makeText(this, "Driver has been assigned", Toast.LENGTH_SHORT).show()
                onBackPressed()
                overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
            } else {
                Toast.makeText(this, "Car proposal already sent to the driver", Toast.LENGTH_SHORT).show()
                onBackPressed()
                overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
            }
        })
    }


    @SuppressLint("SetTextI18n")
    fun setUpViewsData(driver: Driver) {
        firstLastNameTv.text = driver.firstName + " " + driver.lastName
        if (driver.country == null && driver.city == null) {
            driverPlateTv.text = "unknown location"
        } else {
            driverPlateTv.text = driver.city + ", " + driver.country
        }
        driver.photo?.let { setImage(it) }
        driver.rating?.let { setRatingView(it) }
        registrationDateTv.text = "In system since " + driver.registrationDate?.split("\\s".toRegex())!![0]
        ridesCountTv.text = driver.totalRides + " Rides"
        driverPhoneTvDet.text = driver.phoneNumber
        if (driver.reviews?.total != 0) {
            rewiewsCountTv.text = driver.reviews?.total.toString() + " REVIEWS:"
        } else {
            rewiewsCountTv.visibility = View.GONE
        }
        if (releaseKey) {
            btnAssignDriver.text = getString(R.string.rekease_the_driver)
        }
    }

    private fun setRatingView(rating: Float) {
        if (rating > 0) {
            layoutWithRatingDetails.visibility = View.VISIBLE
            ratingNew.visibility = View.GONE
            ratingDetails.text = String.format("%.1f", rating)
        } else {
            layoutWithRatingDetails.visibility = View.GONE
            ratingNew.visibility = View.VISIBLE
        }
    }


    private fun setImage(url: String) {
        Glide.with(applicationContext)
                .asBitmap()
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .into(driverPhotoDetails)
    }


    private fun initAppBar() {
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.my_driver)
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
}
