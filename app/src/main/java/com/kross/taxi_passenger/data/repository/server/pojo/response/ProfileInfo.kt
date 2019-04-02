package com.kross.taxi_passenger.data.repository.server.pojo.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProfileInfo(@Expose @SerializedName("first_name") val firstName: String,
                  @Expose @SerializedName("last_name") val lastName: String,
                  @Expose @SerializedName("email") val email: String,
                  @Expose @SerializedName("phone_number") val phoneNumber: String,
                  @Expose @SerializedName("email_verified") val emailVerified: Boolean,
                  @Expose @SerializedName("referral_code") val referralCode: String,
                  @Expose @SerializedName("photo") val photoUrl: String?,
                  @Expose @SerializedName("type") val type: Int,
                  @Expose @SerializedName("status") val status: Int)