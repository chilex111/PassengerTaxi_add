package com.kross.taxi_passenger.data.repository.server.socket

import io.socket.client.Socket

enum class Parameter(val rawValue: String)  {
    USER_ID("userId"),
    CODE_ERROR("error"),
    MESSAGE("message"),
    ERROR_MESSAGE("errorMessage"),
    LATITUDE("lat"),
    LONGITUDE("lng"),
    ORDER_ID("order_id"),
    DRIVER_ID("driver_id"),
    STATUS("status")
}

enum class Request(val event: String) {
    ORDER_DECLINE("passenger:orderDecline"),
    ORDER_CHECK("passenger:currentOrder"),
    ORDER_CANCEL("passenger:cancelTrip")

}

enum class Response(val event: String) {

    CONNECTION("connection"),
    ERROR(Socket.EVENT_ERROR),
    ORDER_APPROVE("passenger:orderApprove"),
    ORDER_FINISH("passenger:finish"),
    ORDER_DECLINE("passenger:orderDecline"),
    ORDER_ON_TRIP("passenger:driverOnTrip"),
    ORDER_CHECK("passenger:currentOrder"),
    DRIVER_NOT_FOUND("passenger:driverNotFound"),
    DRIVER_ARRIVED("passenger:driverArrived"),
    DRIVER_LOCATION("position"),
    DRIVER_FIND("passenger:driverFind")


}