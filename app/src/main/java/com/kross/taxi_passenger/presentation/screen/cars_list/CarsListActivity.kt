package com.kross.taxi_passenger.presentation.screen.cars_list

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.CarEntity
import com.kross.taxi_passenger.domain.CarsListViewModel
import com.kross.taxi_passenger.presentation.screen.add_car_activity.AddCarActivity
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.car_details_screen.CarDetailsScreen
import com.kross.taxi_passenger.presentation.widget.BaseAdapter
import com.kross.taxi_passenger.utils.getStringPreference
import kotlinx.android.synthetic.main.activity_cars_list.*
import org.koin.android.viewmodel.ext.android.viewModel


class CarsListActivity : BaseActivity(), BaseAdapter.OnItemClickListener<FullCarInfo>, CarsListAdapter.OnItemClickListener {


    private val viewModel: CarsListViewModel by viewModel()
    private var carsListObj: ArrayList<FullCarInfo> = ArrayList()

    private val adapterCarsList: CarsListAdapter = CarsListAdapter(this, this)
    private var id: Int = 0
    private var ListcarId: Int = 0

    companion object {

        const val BECOME_OWNER: String = "car_list"

        fun start(activity: Activity, balance: String) {
            val intent = Intent(activity, CarsListActivity::class.java)
            intent.putExtra(BECOME_OWNER, balance.split("\n").last())
            activity.startActivity(intent)

        }
    }

    override fun onItemClick(view: View, item: FullCarInfo) {
        item.car?.id?.let {
            CarDetailsScreen.start(this@CarsListActivity, it)
            overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
        }
    }

    override fun onResume() {
        super.onResume()
                //  updateData()
    }

    private fun updateData() {
        carsListObj.clear()
        adapterCarsList.clear()
        viewModel.getCars(getStringPreference(R.string.token)).observe(this, Observer {
            setData(it)
        })
    }

    private fun initAppBar() {
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.my_cars_title)
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cars_list)
        initAppBar()
        recyclerCarsList.layoutManager = LinearLayoutManager(this)
        recyclerCarsList.adapter = adapterCarsList
        ViewCompat.setNestedScrollingEnabled(recyclerCarsList, false)
        adapterCarsList.setOnClickListener(this)
        updateData()


        swiperefresh.setOnRefreshListener {
            //            carsListObj =  ArrayList()
//            adapterCarsList.clear()
            updateData()
        }

        addCarBtn.setOnClickListener {
            AddCarActivity.start(this@CarsListActivity, "ADD Car")
            overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
        }
    }

    private fun setData(cars: List<CarEntity>?) {
        adapterCarsList.clear()
        val carsList: ArrayList<FullCarInfo> = ArrayList()
        var key = false
        cars?.forEach { car ->
            val carsObj = FullCarInfo(false)
            when {
               car.status() ==CarStatus.ACTIVE -> {
                    carsObj.car = car
                    layoutCarAproov.visibility = View.VISIBLE
                    layoutCarInChek.visibility = View.GONE
                    key = true
                    addCarBtn.visibility = View.VISIBLE
                    carsObj.driver = car.driverId?.let {
                        viewModel.getDrivers(it)
                    }
                    carsObj.locationEntity = car.locationId?.let {
                        viewModel.getLocation(it)
                    }
                    carsList.add(carsObj)
            setLayoutCar()
                }
                car.status() == CarStatus.DELETED -> {
                    setLayoutCarInCheck(key)
                    tv.text = getString(R.string.have_no_car)
                    addCarBtn.visibility = View.VISIBLE
               }
                else -> {
                   setLayoutCarInCheck(key)
                   addCarBtn.visibility = View.VISIBLE
                }
           }
        }
        adapterCarsList.clear()
        adapterCarsList.addAll(carsList)
        if (swiperefresh.isRefreshing) swiperefresh.isRefreshing = false
    }

    private fun setLayoutCarInCheck(key: Boolean) {
        if (!key) {
            layoutCarAproov.visibility = View.GONE
            layoutCarInChek.visibility = View.VISIBLE
        }
    }

    private fun setLayoutCar() {
        layoutCarAproov.visibility = View.VISIBLE
        layoutCarInChek.visibility = View.GONE
    }

}
