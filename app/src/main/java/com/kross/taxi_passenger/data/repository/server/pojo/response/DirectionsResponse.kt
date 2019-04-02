package com.kross.taxi_passenger.data.repository.server.pojo.response

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.maps.android.PolyUtil
import com.kross.taxi_passenger.data.repository.server.pojo.Coords
import kotlinx.android.parcel.Parcelize
import java.util.*

class DirectionsResponse(@Expose @SerializedName("status") val status: String,
                         @Expose @SerializedName("routes") val routes: List<Routes>)

class Routes(@Expose @SerializedName("legs") val listLegs: List<Legs>) {
    fun buildFullPolyline(): String {
        val arrayLarLng = ArrayList<LatLng>()
        listLegs.forEach {
            arrayLarLng.addAll(it.getFullPolyline())
        }
        return PolyUtil.encode(arrayLarLng)
    }

    fun getTotalDistance(): Int {
        var distance = 0
        listLegs.forEach {
            it.steps.forEach {
                distance += it.distance.value
            }
        }

        return distance
    }
}

class Legs(@Expose @SerializedName("steps") val steps: List<Step>) {
    fun getFullPolyline(): ArrayList<LatLng> {
        val allPoints = ArrayList<LatLng>()
        for (step in steps) {
            allPoints.addAll(PolyUtil.decode(step.polyline.points))
        }
        return allPoints
    }
}

class Step(@Expose @SerializedName("start_location") val startLocation: Coords,
           @Expose @SerializedName("end_location") val endLocation: Coords,
           @Expose @SerializedName("maneuver") val maneuver: String,
           @Expose @SerializedName("polyline") val polyline: Polyline,
           @Expose @SerializedName("distance") val distance: Distance)

class Polyline(@Expose @SerializedName("points") val points: String)
class Distance(@Expose @SerializedName("value") val value: Int,
               @Expose @SerializedName("text") val text: String)


class Route(@Expose @SerializedName("polyline") val polyline: String,
            @Expose @SerializedName("route_points") val routePoints: List<RoutePoint>)


class RoutePoint(@Expose @SerializedName("street") val street: String,
                 @Expose @SerializedName("street_number") val streetNumber: Int,
                 @Expose @SerializedName("coords") val coords: Coords)
