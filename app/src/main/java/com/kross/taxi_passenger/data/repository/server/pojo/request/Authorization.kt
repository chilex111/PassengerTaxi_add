package com.kross.taxi_passenger.data.repository.server.pojo.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Authorization(@Expose @SerializedName("user_type") var userType: Int = 1,
                    @Expose @SerializedName("phone_number") var phoneNumber: String = "",
                    @Expose @SerializedName("password") var password: String = "")