package com.sano.testdrive.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.view.View
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.screen.base.BaseFragment
import com.kross.taxi_passenger.presentation.screen.main.BaseGoogleApiClientActivity
import com.kross.taxi_passenger.utils.doOnConnect
import kotlinx.android.synthetic.main.fragment_main.*

abstract class BaseMapFragment : BaseFragment(), OnMapReadyCallback, LocationListener {

    private lateinit var request: LocationRequest
    private val locationPermessions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION)

    companion object {
        val SETTINGS_REQUEST_ID = 1338
        val DIRECT_SETTINGS_REQUEST_ID = 1339
    }

    private var inRequestSettings: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map_view.onCreate(savedInstanceState)
        map_view.getMapAsync(this)

        requestSettings()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        request = LocationRequest()
                .setInterval(5000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    override fun onResume() {
        map_view.onResume()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        map_view.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        map_view?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map_view.onLowMemory()
    }

    protected fun requestSettings() {
        if (inRequestSettings) return

        inRequestSettings = true

        val permissionRequest = permissionsBuilder(*locationPermessions.toTypedArray()).build()
        permissionRequest.listeners {
            onAccepted {
                onLocationPermissionAccepted()
            }
            onDenied {
                onLocationPermissionDisabled()
            }
        }
        permissionRequest.send()
    }

    private fun getPlayServices(): GoogleApiClient {
        val services = (activity as BaseGoogleApiClientActivity).getPlayServices()
        services?.let {
            return services
        } ?: throw IllegalStateException()
    }

    @SuppressLint("MissingPermission")
    internal fun requestLocations() {
        val result = LocationServices.FusedLocationApi
                .requestLocationUpdates(getPlayServices(), request, this)

        onLocationChanged(LocationServices.FusedLocationApi
                .getLastLocation(getPlayServices()))


        inRequestSettings = false

        result.setResultCallback { status ->
            if (!status.isSuccess) {
                onLocationSettingsDisabled()
            }
        }
    }

    inline fun <reified T> Bundle?.getOrNull(key: String): T? {
        this?.get(key)?.let { return it as T } ?: return null
    }

    private fun onLocationSettingsDisabled() {
        inRequestSettings = false
        showLocationDisabledDialog {
            val viewIntent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(viewIntent, DIRECT_SETTINGS_REQUEST_ID)
        }
    }

    private fun onLocationPermissionAccepted() {
        val b = LocationSettingsRequest.Builder()
                .addLocationRequest(request)

        val result = LocationServices.SettingsApi
                .checkLocationSettings(getPlayServices(), b.build())

        result.setResultCallback {
            onCheckSettingsResult(it)
        }
    }

    private fun onLocationPermissionDisabled() {
        showLocationDisabledDialog { requestSettings() }
    }

    private fun onCheckSettingsResult(result: LocationSettingsResult) {
        when (result.status.statusCode) {
            LocationSettingsStatusCodes.SUCCESS -> {
                requestLocations()
                return
            }
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                result.status.startResolutionForResult(activity, SETTINGS_REQUEST_ID)
                return
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
            }
        }

        onLocationSettingsDisabled()
    }

    private fun showLocationDisabledDialog(listener: () -> Unit) {
        var dialog: AlertDialog? = null

        val dialogView = layoutInflater.inflate(R.layout.dlg_no_location, null)
        val btn = dialogView.findViewById<View>(R.id.btn_turn_on)

        btn.setOnClickListener {
            dialog?.cancel()
            listener.invoke()
        }

        dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SETTINGS_REQUEST_ID) {
            if (resultCode == Activity.RESULT_CANCELED) {
                onLocationSettingsDisabled()
            } else {
                requestLocations()
            }
        } else if (requestCode == DIRECT_SETTINGS_REQUEST_ID) {
            getPlayServices().doOnConnect { requestSettings() }
        }
    }
}