package com.kross.taxi_passenger.presentation.screen.car_location


import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.facebook.accountkit.internal.AccountKitController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.google.maps.android.ui.IconGenerator
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.response.RoutePointsDetails
import com.kross.taxi_passenger.data.repository.server.pojo.response.Trip
import com.kross.taxi_passenger.data.repository.server.pojo.toLatLng
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.managers.LastPointManager
import com.kross.taxi_passenger.presentation.dialog.BottomDialogChoseTripOwner
import com.kross.taxi_passenger.presentation.screen.car_location.CarLocationActivity.Companion.LAT
import com.kross.taxi_passenger.presentation.screen.car_location.CarLocationActivity.Companion.LON
import com.kross.taxi_passenger.presentation.screen.car_location.CarLocationActivity.Companion.SCREEN_TYPE
import com.kross.taxi_passenger.utils.getLocationName
import com.sano.testdrive.main.BaseMapFragment


class CarLocationFragment : BaseMapFragment(), BottomDialogChoseTripOwner.OnClick, View.OnClickListener {
    override fun onClickItem(view: View) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    companion object {

        const val CHOOSE_PAYMENT_REQUEST_CODE: Int = 11204
        const val CHOOSE_DATE_TRIP_REQUEST_CODE: Int = 11205
        const val CHOOSE_NUMBER_REQUEST_CODE: Int = 11206
        const val LEAVE_NOTE_REQUEST_CODE: Int = 11207
        const val PICK_CONTACT_REQUEST_CODE: Int = 11208
        const val TRIP_CANCELED_REQUEST_CODE: Int = 11209

        fun newInstance(latLng: LatLng, type: Int, trip: Trip?) = CarLocationFragment().apply {
            arguments = Bundle().apply {
                putDouble(LAT, latLng.latitude)
                putDouble(LON, latLng.longitude)
                putSerializable("trip", trip)
                putInt(SCREEN_TYPE, type)
            }
        }
    }

    private lateinit var marker: Marker
    private lateinit var myAddress: StubWaypointAddress
    private lateinit var googleMap: GoogleMap
    var latLng: LatLng? = null
    private var type = 0
    private var trip: Trip? = null

    override fun getLayout() = R.layout.fragment_car_location


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        type = arguments?.getInt(SCREEN_TYPE)!!
        latLng = arguments?.getDouble(LAT)?.let { LatLng(it, arguments?.getDouble(LON)!!) }
        trip = arguments?.getSerializable("trip") as Trip?
        setListenersViews()
    }

    private fun putWayPointsOnMap(list: List<RoutePointsDetails>) {
        googleMap.clear()

        list.forEach {
            val generator = IconGenerator(AccountKitController.getApplicationContext())
            val markerOptions = MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(AccountKitController.getApplicationContext().resources, R.drawable.ic_pin_with_number)))
                    .position(it.coords.toLatLng())
                    .anchor(generator.anchorU, generator.anchorV)

            googleMap.addMarker(markerOptions)
        }
    }


    private fun drawLine(path: String) {
        val points = PolyUtil.decode(path)
        if (points.isEmpty()) return
        googleMap.addPolyline(PolylineOptions()
                .addAll(points)
                .width(6f)
                .color(ContextCompat.getColor(AccountKitController.getApplicationContext()!!, R.color.text_color_main)))
        val builder = LatLngBounds.builder()
        points.forEach {
            builder.include(it)
        }
//
//        android.os.Handler().postDelayed({
        googleMap.setOnMapLoadedCallback{
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 30))
        }
        //      }, 300)


    }


    private fun setListenersViews() {
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

        marker = googleMap.addMarker(MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin))
                .position(LatLng(.0, .0))
                .alpha(0f))

        if (type == 0) {
            latLng?.let { myLocationMakerOn(it) }
        } else{
            trip?.route?.routePoints?.let { putWayPointsOnMap(it) }
            trip?.route?.polyline?.let { drawLine(it) }
        }
    }


    override fun onLocationChanged(location: Location?) {
        location ?: return
        LastPointManager.currentPoint = LatLng(location.latitude, location.longitude)
        marker.position = latLng
        marker.setAnchor(0.5f, 0.5f)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.position, if (marker.alpha == 0f) 18f else googleMap.cameraPosition.zoom))
        if (marker.alpha == 0f) getCurrentAddress()
        marker.alpha = 1f

    }


    private fun myLocationMakerOn(latLng: LatLng) {
        googleMap.clear()
        googleMap.setPadding(0, 0, 0, 0)
        marker = googleMap.addMarker(MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car))
                .position(latLng)
                .alpha(1f))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, googleMap.cameraPosition.zoom))
    }

}