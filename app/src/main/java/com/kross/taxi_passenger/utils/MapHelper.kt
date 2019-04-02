package com.kross.taxi_passenger.utils

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.kross.taxi_passenger.data.repository.server.pojo.response.DirectionsResponse
import java.util.ArrayList

object MapHelper {

    fun buildFullPolyline(directionsResponse: DirectionsResponse): String{
        val arrayLarLng = ArrayList<LatLng>()
        directionsResponse.routes[0].listLegs.forEach {
            arrayLarLng.addAll(it.getFullPolyline())
        }
        return PolyUtil.encode(arrayLarLng)
    }
}