package com.kross.taxi_passenger.data.repository.server.pojo.response

import com.google.gson.annotations.SerializedName
import com.kross.taxi_passenger.data.repository.server.pojo.request.Coords

class CarsList(@SerializedName("count") val coutn: Int,
                @SerializedName("cars") val cars: List<Cars>)

class DriversList(@SerializedName("users") val drivers: List<Driver>)

class Cars(@SerializedName("id") val id: Int,
           @SerializedName("make") val make: String,
           @SerializedName("model") val model: String,
           @SerializedName("year") val year: Int?,
           @SerializedName("photo") val photo: String?,
           @SerializedName("plate_number") val plateNumber: String?,
            @SerializedName("status") val status: Int,
            @SerializedName("money_earned") val money_earned: Float,
          @SerializedName("trips_count") val trips_count: Int,
               @SerializedName("location") val location: DriverLocation?,
              @SerializedName("driver") val driver: Driver?)

class Driver(@SerializedName("id") val id: Int,
             @SerializedName("first_name") val firstName: String,
             @SerializedName("last_name") val lastName: String,
             @SerializedName("photo") val photo: String?,
             @SerializedName("city") val city: String?,
             @SerializedName("country") val country: String?,
             @SerializedName("phone_number") val phoneNumber: String,
             @SerializedName("driver_since") val driver_since: String?,
             @SerializedName("driving_from") val driving_from: String?,
             @SerializedName("total_rides") val totalRides: String?,
             @SerializedName("registration_date") val registrationDate: String?,
             @SerializedName("rating") val rating: Float?,
             @SerializedName("reviews") val reviews: Reviews?

)

class Reviews(@SerializedName("total") val total: Int,
              @SerializedName("items") val items: List<ReviewItem>)

class ReviewItem(@SerializedName("photo") val photo: String?,
                 @SerializedName("first_name") val firstName: String?,
                 @SerializedName("last_name") val lastName: String?,
                 @SerializedName("rating") val rating: Float?,
                 @SerializedName("text") val text: String?,
                 @SerializedName("date") val date: String?)


class DriverLocation( @SerializedName("current") val current: Coords,
                      @SerializedName("last") val last: Coords)
