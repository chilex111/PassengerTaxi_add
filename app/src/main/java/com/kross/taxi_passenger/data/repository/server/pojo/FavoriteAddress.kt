package com.kross.taxi_passenger.data.repository.server.pojo

import android.os.Parcelable
import com.google.android.gms.location.places.Place
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class GetFavoritAddressResponse(@Expose @SerializedName("total") val total: Int,
                                @Expose @SerializedName("addresses") val addresses: List<FavoriteAddress>)

// TODO cords not null by default after BE fix
@Parcelize
data class FavoriteAddress(
        @Expose(deserialize = true, serialize = false)
        @SerializedName("id") var id: Int? = null,
        @Expose @SerializedName("imageView") var address: String,
        @Expose @SerializedName("name") var name: String?,
        @Expose @SerializedName("coords") var coords: Coords? = null) : Parcelable {

    // TODO cords not null by default after BE fix
    fun fill(it: Place) {
        address = it.address.toString()
        coords = null
//        coords = Coords.from(it.latLng)
    }
}