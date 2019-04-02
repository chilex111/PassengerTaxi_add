package com.kross.taxi_passenger.data.repository.server.pojo.request

import com.google.gson.annotations.SerializedName

class RateDriver( @SerializedName("rating") var rating: Int,
                 @SerializedName("review") var review: String,
                 @SerializedName("detailed") var detailed: DetailedRate)


class DetailedRate(@SerializedName("air_condition") var airCondition: Int,
                 @SerializedName("car_neatness") var carNeatness: Int,
                 @SerializedName("driver_attitude") var driverAttitude: Int)
