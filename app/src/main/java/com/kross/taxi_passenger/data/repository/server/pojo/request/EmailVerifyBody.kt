package com.kross.taxi_passenger.data.repository.server.pojo.request

import com.google.gson.annotations.SerializedName

class EmailVerifyBody( @SerializedName("user_type") var type: Int = 1,
                       @SerializedName("email") var email: String = "")
