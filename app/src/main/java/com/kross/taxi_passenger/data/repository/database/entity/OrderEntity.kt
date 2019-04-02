package com.kross.taxi_passenger.data.repository.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.converters.RoutePointConverter
import com.kross.taxi_passenger.domain.entity.PaymentMenthod
import com.kross.taxi_passenger.domain.entity.RoutePoint
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.presentation.screen.main.map.State
import java.util.*

enum class FinishedState {

    outstanding, cancel, toll, charged;

    val title: Int
        get() {
            when (this) {
                outstanding -> return R.string.txt_state_outstanding
                cancel -> return R.string.txt_state_cancelled
                toll -> return R.string.txt_state_toll
                charged -> return R.string.txt_state_charged
            }
        }

    val color: Int
        get() {
            when (this) {
                outstanding -> return R.color.stroke_error_color_login
                else -> return R.color.black_87
            }
        }

}

@Entity(tableName = "order_entity")
class OrderEntity(@PrimaryKey(autoGenerate = true)
                  val id: Int,
                  @ColumnInfo(name = "car_type") val carType: Int,
                  val date: String?,
                  val comment: String,
                  @ColumnInfo(name = "order_for") val orderFor: String?,
                  @ColumnInfo(name = "cost_range") val costRange: String,
                  @ColumnInfo(name = "payment_type") val paymentType: Int = 0,
                  @ColumnInfo(name = "card_number") val cardNumber: String?,
                  val polyline: String,
                  val distance: Int,
                  @ColumnInfo(name = "order_status")
                  val orderStatus: Int,
                  @TypeConverters(RoutePointConverter::class)
                  @ColumnInfo(name = "list_route_point")
                  val listRoutePoint: List<RoutePoint>,
                  val priority: Int = 0,
                  @ColumnInfo(name = "driver_id")   val driverId: Int?,
                  @ColumnInfo(name = "car_id")  val carId: Int?) {

    fun revertPoints(): List<StubWaypointAddress> {
        var points: MutableList<StubWaypointAddress> = mutableListOf()
        listRoutePoint.forEach {
            points.add(StubWaypointAddress(
                    it.street,
                    LatLng(it.lat, it.lng),
                    it.street.split(",").first(),
                    it.streetNumber.split(",")[1],
                    it.pointState()))
        }
        return points
    }

    fun paymentMethod(): PaymentMenthod {
        return PaymentMenthod.values()[paymentType]
    }

    fun status(): State {
        return State.values()[orderStatus]
    }

    fun getOrderDate(): String {
        return "date"
    }

    fun getOrderDuration(): String {
        return "from-to"
    }

    fun getFinishedState(): FinishedState {
        fun IntRange.random() = Random().nextInt((endInclusive + 1) - start) + start
        val randomNumber = (0..(FinishedState.values().size - 1)).random()
        return FinishedState.values()[randomNumber]
    }

}
