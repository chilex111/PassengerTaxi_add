package com.kross.taxi_passenger.data.repository.server.pojo.response

import com.google.gson.annotations.SerializedName
/*
class PhoneConfirmModel {

    @SerializedName("Data")
    var data: Data? = null
    @SerializedName("Message")
    var message: String? = null
    @SerializedName("StatusCode")
    var statusCode: Long? = null

}*/

class PhoneConfirmModel(@SerializedName("Message") var message: String,
                        @SerializedName("Data") var data: Data,
                        @SerializedName("StatusCode") var code: Long)

class Data(  @SerializedName("PhoneNumber") var phoneNumber: String,
             @SerializedName("Token") var token: String)