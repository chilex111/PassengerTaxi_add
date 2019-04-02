package com.kross.taxi_passenger.data.repository.server.pojo.response

import com.google.gson.annotations.SerializedName


class About(@SerializedName("text") val text: String)

class FAQ(@SerializedName("id") val id: Int,
          @SerializedName("title") val title: String,
          @SerializedName("text") val text: String)

class News(@SerializedName("total") val total: Int,
           @SerializedName("news") val news: List<NewsItem>)

class NewsItem(@SerializedName("id") val id: Long?,
           @SerializedName("title") val title: String,
           @SerializedName("text") val text: String,
           @SerializedName("image") val image: String?,
           @SerializedName("published_date") val published_date: String)
