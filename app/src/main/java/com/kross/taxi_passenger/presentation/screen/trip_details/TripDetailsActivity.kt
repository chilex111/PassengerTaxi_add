package com.kross.taxi_passenger.presentation.screen.trip_details

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.CarEntity
import com.kross.taxi_passenger.data.repository.database.entity.DriverEntity
import com.kross.taxi_passenger.data.repository.database.entity.OrderEntity
import com.kross.taxi_passenger.domain.MapViewModel
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.main.MainActivity
import com.kross.taxi_passenger.presentation.screen.trip_details.adapter.TripAdapter
import com.kross.taxi_passenger.presentation.screen.waypoints.WaypointsActivity
import com.kross.taxi_passenger.utils.visibleOrGone
import kotlinx.android.synthetic.main.activity_trip_details.*
import kotlinx.android.synthetic.main.bottom_trip_driver_info.*
import org.koin.android.viewmodel.ext.android.viewModel

const val REQUEST_CALL: Int = 1

class TripDetailsActivity : BaseActivity() {
    private var order: OrderEntity? = null
    private var orderId = 0

    companion object {

        const val ORDER_ID: String = "orderId"

        fun start(fragment: Fragment, orderId: Int) {
            val intent = Intent(fragment.context, TripDetailsActivity::class.java)
            intent.putExtra(ORDER_ID, orderId)
            fragment.startActivity(intent)
        }

    }

    private lateinit var pointsAdapter: TripAdapter
    private val viewModel: MapViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_details)
        initAdapter()
        loadOrderData()
        toolbarSettings()
    }

    @SuppressLint("SetTextI18n")
    fun setDriverInfo(carEntity: CarEntity, driverEntity: DriverEntity) {
        txtDriversRating1.text = driverEntity.rating.toString()
        txtNameDriver1.text = driverEntity.firstName + " " + driverEntity.lastName
        txtCarDriver1.text = carEntity.make + " " + carEntity.model
        txtDriverCarNumber1.text = carEntity.plate_number
        priceTextView.text = "₦ " + order?.costRange

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
        return super.onOptionsItemSelected(item)
    }

    fun oneMoreTrip(v: View) {
        WaypointsActivity.start(this, pointsAdapter.list.first(), MainActivity.WAYPOINTS_REQUEST_CODE, true)
    }

    fun callToDriver(v: View) {
        viewModel.getDriver(orderId)?.callToDriver(this)
    }

    private fun toolbarSettings() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_black)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initAdapter() {
        tableView.layoutManager = LinearLayoutManager(this)
        pointsAdapter = TripAdapter(this::handleEditTapped)
        tableView.adapter = pointsAdapter
    }

    private fun loadOrderData() {
        val orderId = intent.getIntExtra(ORDER_ID, 1)
        viewModel.getOrder().observe(this, Observer { it ->
            if (it != null && it.isNotEmpty()) {
                val order = it.filter {
                    it.id == orderId
                }.first()
                moreFrame.visibleOrGone(it.size == 1)
                this.order = order
                this.orderId = order.id
                pointsAdapter.setItems(order.revertPoints())

                viewModel.getDriver(order.driverId!!)?.let { setDriverInfo(viewModel.getCar(order.carId!!)!!, it) }
            }
        })
    }

    private fun handleEditTapped(index: Int, point: StubWaypointAddress, isSeleckted: Boolean) {

    }

    // MARK: - Navigation

}