package com.kross.taxi_passenger.data.repository.server.pojo

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class FavoriteRoute(@Expose @SerializedName("route") var route: KrossRoute) : Parcelable

@Parcelize
class KrossRoutePoint(@Expose @SerializedName("imageView") var street: String,
                      @Expose @SerializedName("coords") var coords: Coords) : Parcelable

@Parcelize
class KrossRoute(@Expose @SerializedName("polyline") var polyline: String,
                 @Expose @SerializedName("route_points") var routePoints: List<KrossRoutePoint>,
                 @Expose(serialize = false) val id: Int? = null) : Parcelable