package com.kross.taxi_passenger.data.repository.server.pojo.request

import com.google.gson.annotations.SerializedName

class HelpRequest(@SerializedName("help_id") var helpId: Int,
                  @SerializedName("title") var title: String,
                  @SerializedName("text") var text: String)
