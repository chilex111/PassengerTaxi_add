package com.kross.taxi_passenger.data.repository.server.pojo.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PhoneConfirm (@Expose @SerializedName("token") val token: String?,
                         @Expose @SerializedName("phone_number") val phoneNumber: String?,
                         @Expose @SerializedName("user_id") val userId: Int?)