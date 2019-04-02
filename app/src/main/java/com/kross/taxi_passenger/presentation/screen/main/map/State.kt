package com.kross.taxi_passenger.presentation.screen.main.map

// TODO add map fragment states
enum class State(val status: Int) {

    DEFAULT(0),

    PAYMENT_PARAMS(1),

    TAXI_FOR(2),

    TAXI_SEARCH(3),

    TAXI_NOT_FOUND(4),

    TAXI_FOUND(5),

    TRIP_ON(6),

    TRIP_FINISHED(7),

    TRIP_END(8),

    DRIVER_ARRIVED(9)

}