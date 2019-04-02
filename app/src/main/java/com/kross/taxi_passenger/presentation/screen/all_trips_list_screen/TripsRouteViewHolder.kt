package com.kross.taxi_passenger.presentation.screen.all_trips_list_screen

import android.app.Activity
import android.graphics.BitmapFactory
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import com.facebook.accountkit.internal.AccountKitController.getApplicationContext
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.google.maps.android.ui.IconGenerator
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.response.RoutePointsDetails
import com.kross.taxi_passenger.data.repository.server.pojo.response.Trip
import com.kross.taxi_passenger.data.repository.server.pojo.toLatLng
import com.kross.taxi_passenger.presentation.screen.car_location.CarLocationActivity
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.all_trips_item.*
import kotlinx.android.synthetic.main.item_favorite_route.*


class TripsRouteViewHolder(override val containerView: View, val context: Activity) : RecyclerView.ViewHolder(containerView), LayoutContainer, OnMapReadyCallback {

    lateinit var point: Trip
    var mapCurrent: GoogleMap? = null
    var map: MapView? = null

    override fun onMapReady(p0: GoogleMap?) {
        MapsInitializer.initialize(getApplicationContext())
        mapCurrent = p0
        mapCurrent?.setOnMapClickListener {
            CarLocationActivity.start(context, LatLng(0.0, 0.0), 1, point)
            context.overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
        }
        mapCurrent!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.style_map_grey))
        putWayPointsOnMap(point.route.routePoints)
        drawLine(point.route.polyline)
    }

    fun bind(point: Trip, inflater: LayoutInflater) {
        this.point = point
        layoutPoints.removeAllViews()
        mapInit()
        point.route.routePoints.forEachIndexed { index, krossRoutePoint ->
            val holder = AllTripsRouteSubItemViewHolder(
                    inflater.inflate(R.layout.item_favorite_route_subitem, layoutPoints1, false))

            holder.bind(krossRoutePoint,
                    isFirst = index == 0,
                    isLast = index == point.route.routePoints.lastIndex)

            layoutPoints.addView(holder.containerView)
        }
    }

    fun mapInit() {
        map = mapViewItem
        if (map != null) {
            map!!.onCreate(null)
            map!!.onResume()
            map!!.getMapAsync(this)
        }
    }

    private fun putWayPointsOnMap(list: List<RoutePointsDetails>) {
        mapCurrent?.clear()

        list.forEach {
            val generator = IconGenerator(getApplicationContext())
            val markerOptions = MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getApplicationContext().resources, R.drawable.ic_pin_with_number)))
                    .position(it.coords.toLatLng())
                    .anchor(generator.anchorU, generator.anchorV)

            mapCurrent?.addMarker(markerOptions)
        }
    }


    private fun drawLine(path: String) {
        val points = PolyUtil.decode(path)
        if (points.isEmpty()) return
        mapCurrent?.addPolyline(PolylineOptions()
                .addAll(points)
                .width(6f)
                .color(ContextCompat.getColor(getApplicationContext()!!, R.color.text_color_main)))
        val builder = LatLngBounds.builder()
        points.forEach {
            builder.include(it)
        }

        mapCurrent?.setOnMapLoadedCallback {
            mapCurrent?.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 30))
        }
    }
}