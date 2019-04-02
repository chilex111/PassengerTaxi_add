package com.kross.taxi_passenger.presentation.screen.cars_list

// TODO add map fragment states
enum class CarStatus(val status: Int) {
    NOT_ACTIVE(0),
    ACTIVE(1),
    IN_CHECK(2),
    DELETED(3),
    FROM_TRIP(4)
}