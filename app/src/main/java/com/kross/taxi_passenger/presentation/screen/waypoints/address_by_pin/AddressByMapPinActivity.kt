package com.kross.taxi_passenger.presentation.screen.waypoints.address_by_pin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.screen.main.BaseGoogleApiClientActivity

class AddressByMapPinActivity : BaseGoogleApiClientActivity() {

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, AddressByMapPinActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private var openFragment = false
    private var latLng: LatLng? = null
    override fun onConnected(p0: Bundle?) {
        if (openFragment) {
            openFragment = false
            openMapFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_by_map_pin)
        latLng = LatLng(intent.getDoubleExtra("lat", 90.0), intent.getDoubleExtra("lon", 90.0))

        Log.d("LOCATIN_LOCATION", latLng.toString())
        openFragment = savedInstanceState == null
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    private fun openMapFragment() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.containeer2, AddressByMapPinFragment.newInstance())
                .commit()
    }

    override fun configureApiClientBuilder(b: GoogleApiClient.Builder) = b.addApi(LocationServices.API)

}
