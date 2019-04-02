package com.kross.taxi_passenger.data.repository.server.pojo.response

import com.google.gson.annotations.SerializedName

class FavoriteAdressResponce(@SerializedName("address_id") val addressId: Int)


class FavoriteRouteResponce(@SerializedName("route_id") val routeId: Int)