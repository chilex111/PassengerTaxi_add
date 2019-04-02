package com.kross.taxi_passenger.presentation.screen.waypoints

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.domain.MapViewModel
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.one_more_trip.OneMoreTripActivity
import com.kross.taxi_passenger.presentation.screen.waypoints.addPoint.AddPointFragment
import com.kross.taxi_passenger.utils.showToast
import kotlinx.android.synthetic.main.activity_container_toolbar.*
import org.jetbrains.anko.startActivity
import org.koin.android.viewmodel.ext.android.viewModel

class WaypointsActivity : BaseActivity() {

    companion object {
        const val WAYPOINTS_RESULT_EXTRA = "WAYPOINTS_RESULT_EXTRA"
        const val INITIAL_ADDRESS_EXTRA = "INITIAL_ADDRESS_EXTRA"
        const val IS_SECOND_ORDER_EXTRA = "IS_SECOND_ORDER_EXTRA"

        fun start(activity: Activity, address: StubWaypointAddress, requestCode: Int, isSecond: Boolean = false) {
            val intent = Intent(activity, WaypointsActivity::class.java)
            intent.putExtra(INITIAL_ADDRESS_EXTRA, address)
            intent.putExtra(IS_SECOND_ORDER_EXTRA, isSecond)
            activity.startActivityForResult(intent, requestCode)
            activity.overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
        }
    }

    private var isSecond: Boolean = false
    private val viewModel: MapViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarSettings()
        isSecond = intent.getBooleanExtra(IS_SECOND_ORDER_EXTRA, false)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, WaypointsFragment.newInstance(getArgOrThrow(INITIAL_ADDRESS_EXTRA)))
                    .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
        return super.onOptionsItemSelected(item)
    }

    // MARK: - Actions

    // MARK: - Helper methods

    private fun toolbarSettings() {
        setContentView(R.layout.activity_container_toolbar)
        setSupportActionBar(toolbar)
        val upArrow = resources.getDrawable(R.drawable.ic_back_black, null)
        supportActionBar?.setHomeAsUpIndicator(upArrow)
    }

    fun openAddPointFragment(type: AddPointFragment.Companion.Type) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, AddPointFragment.newInstance(getArgOrThrow<StubWaypointAddress>(INITIAL_ADDRESS_EXTRA).latLng, type))
                .addToBackStack(null)
                .commit()
    }

    fun deliverResult(items: MutableList<StubWaypointAddress>) {
        if (isSecond) {
            if (items.size == 1) {
                viewModel.insertOrder(distance = 0, listRoutePoint = items, polyline = "", priority = 1)
                startActivity<OneMoreTripActivity>()
                return
            }
            viewModel.getDirection(getString(R.string.google_maps_directions_key),
                    items.map { it.latLng },
                    {
                        viewModel.insertOrder(distance = it.first, listRoutePoint = items, polyline = it.second, priority = 1)
                        startActivity<OneMoreTripActivity>()
                    },
                    {
                        it?.let { showToast(it) }
                    })
        } else {
            Intent().apply {
                putParcelableArrayListExtra(WAYPOINTS_RESULT_EXTRA, ArrayList(items))
                setResult(Activity.RESULT_OK, this)
            }
            finish()
            overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
        }
    }

}