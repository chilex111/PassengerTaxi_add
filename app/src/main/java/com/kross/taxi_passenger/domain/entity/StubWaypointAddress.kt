package com.kross.taxi_passenger.domain.entity

import android.os.Parcelable
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.model.LatLng
import com.kross.taxi_passenger.data.repository.database.entity.UserPointEntity
import com.kross.taxi_passenger.data.repository.server.pojo.FavoriteAddress
import com.kross.taxi_passenger.data.repository.server.pojo.KrossRoutePoint
import com.kross.taxi_passenger.data.repository.server.pojo.toLatLng
import kotlinx.android.parcel.Parcelize

// TODO replace with unit data class for addresses, add mappers to domain layer,
// TODO remove duplicate properties, simplify logic
@Parcelize
data class StubWaypointAddress(val address: String, val latLng: LatLng,
                               val streetName: String? = null, val streetNum: String? = null, val state: PointState = PointState.next) : Parcelable {

    companion object {
        fun from (entity: UserPointEntity) = StubWaypointAddress(entity.name, entity.position)
        fun from(place: Place): StubWaypointAddress = StubWaypointAddress(place.address.toString(), place.latLng)
        fun from(place: FavoriteAddress): StubWaypointAddress = StubWaypointAddress(place.address, place.coords.toLatLng())
        fun from(it: KrossRoutePoint): StubWaypointAddress =
                StubWaypointAddress(" ${it.street}", it.coords.toLatLng(),
                        it.street)
    }
}