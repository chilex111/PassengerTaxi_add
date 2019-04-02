package com.kross.taxi_passenger.presentation.screen.car_location

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.response.Trip
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.presentation.screen.main.BaseGoogleApiClientActivity
import com.kross.taxi_passenger.presentation.screen.main.map.MapFragment
import com.kross.taxi_passenger.presentation.screen.waypoints.WaypointsActivity
import kotlinx.android.synthetic.main.activity_container_toolbar.*
import org.jetbrains.anko.activityManager
import java.io.Serializable

class CarLocationActivity : BaseGoogleApiClientActivity() {

    companion object {
        const val WAYPOINTS_REQUEST_CODE: Int = 11203
        const val LAT: String ="lat"
        const val LON: String ="lon"
        const val SCREEN_TYPE: String = "screen_type"

        fun start(activity: Context, latLng: LatLng, type: Int, trip: Trip?) {
            val intent = Intent(activity, CarLocationActivity::class.java)
            intent.putExtra(LAT, latLng.latitude)
            intent.putExtra(LON, latLng.longitude)
            intent.putExtra( SCREEN_TYPE, type)
            intent.putExtra( "trip", trip)
            activity.startActivity(intent)
        }
    }

    private var openFragment = false
    private var latLng: LatLng? = null
    private var type: Int = 0
    private var trip: Trip? = null
    override fun onConnected(p0: Bundle?) {
        if (openFragment) {
            openFragment = false
            openMapFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_location)



        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)


        latLng = LatLng(intent.getDoubleExtra(LAT, 90.0), intent.getDoubleExtra(LON, 90.0))
        type = intent.getIntExtra(SCREEN_TYPE, 0)
       trip = intent.getSerializableExtra("trip") as? Trip?


        Log.d("LOCATIN_LOCATION", latLng.toString())
        openFragment = savedInstanceState == null
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    private fun openMapFragment() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.containeer1, latLng?.let { CarLocationFragment.newInstance(it, type, trip ) })
                .commit()
    }

    override fun configureApiClientBuilder(b: GoogleApiClient.Builder) = b.addApi(LocationServices.API)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            WAYPOINTS_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    deliverWaypoints(getArgOrThrow(data, WaypointsActivity.WAYPOINTS_RESULT_EXTRA))
                }
            }
            else -> {
                val f = supportFragmentManager.findFragmentById(R.id.containeer1) as CarLocationFragment
                f.onActivityResult(requestCode, resultCode, data)
            }
        }
        return super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun deliverWaypoints(waypoints: ArrayList<StubWaypointAddress>) {
        (supportFragmentManager
                .findFragmentById(R.id.containeer1)
                as MapFragment).setWaypoints(waypoints)
    }
}
