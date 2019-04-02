package com.kross.taxi_passenger.data.repository.server.pojo.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Registration(@Expose @SerializedName("first_name") val firstName: String,
                   @Expose @SerializedName("last_name") val lastName: String,
                   @Expose @SerializedName("phone_number") val phoneNumber: String,
                   @Expose @SerializedName("email") val email: String,
                   @Expose @SerializedName("password") val password: String,
                   @Expose @SerializedName("invite_code") val inviteCode: String,
                   @Expose @SerializedName("token") val token: String)