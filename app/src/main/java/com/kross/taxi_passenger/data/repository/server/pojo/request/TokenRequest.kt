package com.kross.taxi_passenger.data.repository.server.pojo.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TokenRequest(@SerializedName("token") var token: String)
