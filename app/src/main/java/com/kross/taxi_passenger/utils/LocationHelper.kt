package com.kross.taxi_passenger.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.common.data.DataBufferUtils
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.domain.entity.StubTripRates
import java.io.IOException
import java.util.*

private const val TAG = "LocationHelper"

object LocationHelper {
    val addressAutocompleteFilter = AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).build()
}

data class TripCost(val min: Float, val max: Float)

fun getLocationName(context: Context, location: LatLng, onReceive: (Address) -> Unit,
                    onError: (String) -> Unit) {
    try {
        Geocoder(context, Locale.getDefault())
                .getFromLocation(
                        location.latitude,
                        location.longitude,
                        1)
                .let {
                    if (it.size == 0) return@let
                    onReceive.invoke(it[0])
                }

    } catch (ioException: IOException) {
        onError.invoke(context.getString(R.string.service_not_available))
        Log.e(TAG, "Service not available", ioException)

    } catch (illegalArgumentException: IllegalArgumentException) {
        onError.invoke(context.getString(R.string.invalid_lat_long_used))
        Log.e(TAG, "Invalid lat lng. Latitude = $location.latitude , " +
                "Longitude =  $location.longitude", illegalArgumentException)
    }
}

fun GeoDataClient.getPlaceById(placeId: String, onReceive: (Place?) -> Unit) {

    getPlaceById(placeId)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val placesList = DataBufferUtils.freezeAndClose(task.result)
                    onReceive.invoke(placesList.elementAtOrNull(0))
                } else {
                    onReceive.invoke(null)
                    Log.e(TAG, "!suggestion.isSuccessful", task.exception)
                }
            }
}

fun GeoDataClient.getAutocomplete(constraint: CharSequence, bounds: LatLngBounds?,
                                  filter: AutocompleteFilter?, listener: (List<AutocompletePrediction>) -> Unit) {

    getAutocompletePredictions(constraint.toString(), bounds, filter)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val autocompletePredictions = task.result
                    listener.invoke(DataBufferUtils.freezeAndClose(autocompletePredictions))
                } else {
                    listener.invoke(emptyList())
                    Log.e(TAG, "!suggestion.isSuccessful", task.exception)
                }
            }
}

fun LatLng.toBounds(radiusInMeters: Double = 10000.0): LatLngBounds {
    val distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0)
    val southwestCorner = SphericalUtil.computeOffset(this, distanceFromCenterToCorner, 225.0)
    val northeastCorner = SphericalUtil.computeOffset(this, distanceFromCenterToCorner, 45.0)
    return LatLngBounds(southwestCorner, northeastCorner)
}


fun calculateCost(distance: Int?, tripRates: StubTripRates): TripCost {
    if(distance == null || distance == 0) {
        return TripCost(0f, 0f)
    }

    val sum = distance / 1000 * tripRates.sumPerDistance

    return TripCost(sum * .8f, sum * 1.2f)
}