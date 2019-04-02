package com.kross.taxi_passenger.data.repository.server.pojo.request

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class Order(@Expose @SerializedName("route") val route: Route,
                 @Expose @SerializedName("car_type") val carType: Int,
                 @Expose @SerializedName("date") val date: String?,
                 @Expose @SerializedName("comment") val comment: String?,
                 @Expose @SerializedName("order_for") val orderFor: String?,
                 @Expose @SerializedName("cost_range") val costRange: String,
                 @Expose @SerializedName("payment_type") val paymentType: Int?)

@Parcelize
data class Route(@Expose @SerializedName("polyline") val polyline: String,
            @Expose @SerializedName("route_points") val routePoints: List<RoutePoint>): Parcelable

@Parcelize
data class RoutePoint(@Expose @SerializedName("coords") val coords: Coords,
                      @Expose @SerializedName("imageView") val address: String): Parcelable

@Parcelize
data class Coords(@Expose @SerializedName("lat") var lat: Double,
             @Expose @SerializedName("lng") var lng: Double) : Parcelable