package com.kross.taxi_passenger.data.repository.server.pojo.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Token(@Expose @SerializedName("token") val token: String)