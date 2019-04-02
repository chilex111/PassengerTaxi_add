package com.kross.taxi_passenger.data.repository.server.pojo.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserId(@Expose @SerializedName("user_id") val userId: Int,
             @Expose @SerializedName("token") val token: String) {
}