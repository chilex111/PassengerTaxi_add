package com.kross.taxi_passenger.presentation.screen.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.SocketCommunicator
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.presentation.screen.main.map.MapFragment
import com.kross.taxi_passenger.presentation.screen.waypoints.WaypointsActivity
import org.koin.android.ext.android.inject

class MainActivity : BaseGoogleApiClientActivity() {
    val socketCommunicator by inject<SocketCommunicator>()
    var pushType = 0

    companion object {
        const val WAYPOINTS_REQUEST_CODE: Int = 11203

        fun start(activity: Activity) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private var openFragment = false

    override fun onConnected(p0: Bundle?) {
        if(openFragment) {
            openFragment = false
            openMapFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openFragment = savedInstanceState == null

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        pushType = intent.getIntExtra(getString(R.string.PUSH_TYPE), -1)
    }

    private fun openMapFragment() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, MapFragment.newInstance(pushType))
                .commit()
    }

    override fun configureApiClientBuilder(b: GoogleApiClient.Builder) = b.addApi(LocationServices.API)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            WAYPOINTS_REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK && data != null) {
                    deliverWaypoints(getArgOrThrow(data, WaypointsActivity.WAYPOINTS_RESULT_EXTRA))
                }
            }
            else -> {
                val f = supportFragmentManager.findFragmentById(R.id.container) as MapFragment
                f.onActivityResult(requestCode, resultCode, data)
            }
        }
        return super.onActivityResult(requestCode, resultCode, data)
    }

    private fun deliverWaypoints(waypoints: ArrayList<StubWaypointAddress>) {
        (supportFragmentManager
                .findFragmentById(R.id.container)
                as MapFragment).setWaypoints(waypoints)
    }

}