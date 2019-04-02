package com.kross.taxi_passenger.data.repository.server.pojo.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kross.taxi_passenger.data.repository.server.pojo.request.RoutePoint
import kotlinx.android.parcel.Parcelize

class OrderInfo(@SerializedName("date_created") val dateCreated: String,
                @SerializedName("date_accepted") val dateAccepted: String?,
                @SerializedName("date_finished") val dateFinished: String?,
                @SerializedName("status") val status: Int,
                @SerializedName("changed") val changed: Boolean,
                @SerializedName("payment") val payment: Payment,
                @SerializedName("route") val route: RouteInfo,
                @SerializedName("driver") val driver: DriverInfo?,
                @SerializedName("car") val car: CarInfo?)

class CarInfo(@SerializedName("id") val id: Int?,
              @SerializedName("make") val make: String?,
              @SerializedName("model") val model: String?,
              @SerializedName("year") val year: Int?,
              @SerializedName("color") val color: String?,
              @SerializedName("status") val status: Int?,
              @SerializedName("licence_plate_number") val plateNumber: String?)



class Payment(@SerializedName("type") val type: String,
              @SerializedName("card_number") val cardNumber: String?,
              @SerializedName("finance_data") val financeData: FinanceData)


//TODO: change to DriverClass from CarsList after merge with feature/car_owner_drivers
class DriverInfo(@SerializedName("id") val id: Int?,
                 @SerializedName("first_name") val firstName: String?,
                 @SerializedName("last_name") val lastName: String?,
                 @SerializedName("photo") val photo: String?,
                 @SerializedName("city") val city: String?,
                 @SerializedName("rating") val rating: Float?,
                 @SerializedName("driving_from") val driving_from: String?,
                 @SerializedName("phone_number") val phoneNumber: String)

@Parcelize
data class RouteInfo(@SerializedName("polyline") val polyline: String,
                     @SerializedName("points") val routePoints: List<RoutePoint>) : Parcelable