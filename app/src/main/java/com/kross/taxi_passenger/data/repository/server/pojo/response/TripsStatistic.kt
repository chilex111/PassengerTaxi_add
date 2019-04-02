package com.kross.taxi_passenger.data.repository.server.pojo.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kross.taxi_passenger.data.repository.server.pojo.Coords
import java.io.Serializable

class TripsStatistic(@SerializedName("statistic") val tripsStatistic: Statistic?,
                     @SerializedName("trips") val trips: List<Trip>?)

class Statistic(
        @SerializedName("today") val today: Int,
        @SerializedName("week") val week: Int,
        @SerializedName("month") val month: Int
)

class Trip(@SerializedName("date_create")  val  dateCreate: String,
           @SerializedName("date_finish") val dateFinish: String,
           @SerializedName("finance_data") val financeData: FinanceData,
           @SerializedName("route") val route: RouteDetails): Serializable


class RouteDetails(@Expose @SerializedName("polyline") val polyline: String,
            @Expose @SerializedName("route_points") val routePoints: List<RoutePointsDetails>): Serializable


class RoutePointsDetails(@Expose @SerializedName("street") val street: String,
                 @Expose @SerializedName("street_number") val streetNumber: Int,
                 @Expose @SerializedName("imageView") val address: String,
                 @Expose @SerializedName("coords") val coords: Coords): Serializable

class FinanceData(@SerializedName("trip") val trip: Float,
                  @SerializedName("my_earnings") val my_earings: Float,
                  @SerializedName("toll") val toll: Float): Serializable