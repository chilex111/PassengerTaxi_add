package com.kross.taxi_passenger.presentation.screen.waypoints.address_by_pin


import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.managers.LastPointManager
import com.kross.taxi_passenger.presentation.dialog.BottomDialogChoseTripOwner
import com.kross.taxi_passenger.utils.getLocationName
import com.sano.testdrive.main.BaseMapFragment
import kotlinx.android.synthetic.main.fragment_address_by_map_pin.*


class AddressByMapPinFragment : BaseMapFragment(), BottomDialogChoseTripOwner.OnClick, View.OnClickListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener {
    override fun onClickItem(view: View) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    companion object {
        fun newInstance() = AddressByMapPinFragment()
    }

    private lateinit var marker: Marker
    private lateinit var myAddress: StubWaypointAddress
    private lateinit var googleMap: GoogleMap
    var latLng: LatLng? = null

    override fun getLayout() = R.layout.fragment_address_by_map_pin


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMainScreenView()
    }


    private fun setListenersViews() {
        googleMap.setOnCameraMoveListener(this)
        googleMap.setOnCameraIdleListener(this)
    }

    private fun getCurrentAddress() {
        val activityInstance = activity ?: return
        getLocationName(activityInstance, marker.position, {
            myAddress = StubWaypointAddress(it.getAddressLine(0), marker.position, it.thoroughfare, it.subThoroughfare)
        }, {
            showToast(it)
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_map_grey))
        setListenersViews()
        marker = googleMap.addMarker(MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin))
                .position(LatLng(48.467720, 35.051391))
                .alpha(0f))
        myLocationMakerOn(LatLng(48.467720, 35.051391))
    }


    override fun onCameraMove() {
        googleMap.clear()
        // display imageView
        imgLocationPinUp.visibility = View.VISIBLE
    }

    override fun onCameraIdle() {

        imgLocationPinUp.visibility = View.GONE
        val cameraPosition = googleMap.cameraPosition
        val currentCenter = cameraPosition.target

        val markerOptions = MarkerOptions().position(currentCenter)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_purple_copy))

        googleMap.addMarker(markerOptions)

    }

    override fun onLocationChanged(location: Location?) {
        location ?: return
        LastPointManager.currentPoint = LatLng(location.latitude, location.longitude)
        marker.position = LatLng(location.latitude, location.longitude)
        marker.setAnchor(0.5f, 0.5f)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.position, if (marker.alpha == 0f) 18f else googleMap.cameraPosition.zoom))
        if (marker.alpha == 0f) getCurrentAddress()
        marker.alpha = 1f

    }


    private fun myLocationMakerOn(latLng: LatLng) {
            googleMap.clear()
        googleMap.setPadding(0, 0, 0, 0)
        marker = googleMap.addMarker(MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_purple_copy))
                .position(latLng)
                .alpha(1f))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, googleMap.cameraPosition.zoom))
    }


    private fun setupMainScreenView() {
        view?.post {
            //    googleMap.setPadding(0, 0, 0, container_bottom.height)
        }
    }



}