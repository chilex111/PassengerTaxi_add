package com.kross.taxi_passenger.data.repository.server.pojo

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class Coords(@Expose @SerializedName("lat") var lat: Float,
             @Expose @SerializedName("lng") var lng: Float) : Parcelable, Serializable
{

    companion object {
        fun from(latLng: LatLng) = Coords(latLng.latitude.toFloat(), latLng.longitude.toFloat())
    }
}

// TODO remove after BE add imageView
fun Coords?.toLatLng() =
        if (this == null) {
            LatLng(.0, .0)
        } else {
            LatLng(this.lat.toDouble(), this.lng.toDouble())
        }