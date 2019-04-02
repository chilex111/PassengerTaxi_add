package com.kross.taxi_passenger.presentation.screen.driver_rate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.InputFilter
import android.util.Log
import android.view.MenuItem
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.request.DetailedRate
import com.kross.taxi_passenger.data.repository.server.pojo.request.RateDriver
import com.kross.taxi_passenger.domain.MapViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.driver_rate.adapter.DriverRate
import com.kross.taxi_passenger.presentation.screen.driver_rate.adapter.RateAdapter
import com.kross.taxi_passenger.presentation.screen.trip_finished.ORDER_ENTITY
import com.kross.taxi_passenger.utils.textObserver
import kotlinx.android.synthetic.main.activity_driver_rate.*
import org.jetbrains.anko.enabled
import org.koin.android.viewmodel.ext.android.viewModel

class FinishRate {

    var overall: Int = 0
    var air: Int = 0
    var car: Int = 0
    var attitude: Int = 0

    fun valueAt(index: DriverRate): Int {
        return when (index) {
            DriverRate.overall -> overall
            DriverRate.air -> air
            DriverRate.car -> car
            DriverRate.driver -> attitude
            else -> 0
        }
    }

    fun changeValueAt(index: DriverRate, to: Int) {
        when (index) {
            DriverRate.overall -> overall = to
            DriverRate.air -> air = to
            DriverRate.car -> car = to
            DriverRate.driver -> attitude = to
        }
    }

}

class DriverRateActivity : BaseActivity() {

    companion object {

        fun start(context: Context, orderId: Int) {
            val intent = Intent(context, DriverRateActivity::class.java)
            intent.putExtra(ORDER_ENTITY, orderId)
            context.startActivity(intent)
        }

    }

    private var rate: FinishRate = FinishRate()
    private val viewModel: MapViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_rate)
        initAppBar()
        initAdapter()
        observeChanges()
        sendButton.setOnClickListener {
            viewModel.rateDriver(intent.getIntExtra(ORDER_ENTITY, 0), RateDriver(rate.overall, textInput.text.toString(), DetailedRate(rate.air, rate.car, rate.attitude))) {
                finish()
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    // MARK: - Actions

    // MARK: - Helper methods

    private fun initAdapter() {
        tableView.layoutManager = LinearLayoutManager(this)
        tableView.adapter = RateAdapter(rate) {
            indicateButton()
            (tableView.adapter as RateAdapter).changeUI(it)
        }
    }

    private fun initAppBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)
    }

    private fun observeChanges() {
        textInput.filters = arrayOf(InputFilter.LengthFilter(40))
        textInput.textObserver()
                .subscribe({
                    indicateButton()
                }, {
                    print(it.stackTrace)
                })
    }

    private fun indicateButton() {
        sendButton.enabled = rate.overall != 0 && textInput.text.count() < 41
        Log.d("RATE_RATE", "overall: " + rate.overall)
        Log.d("RATE_RATE", "air: " + rate.air)
        Log.d("RATE_RATE", "car: " + rate.car)
        Log.d("RATE_RATE", "attitude: " + rate.attitude)
    }

}