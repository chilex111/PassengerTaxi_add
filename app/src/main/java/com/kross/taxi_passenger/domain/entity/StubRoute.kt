package com.kross.taxi_passenger.domain.entity

import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.Card
import com.kross.taxi_passenger.data.repository.server.pojo.response.Step
import java.util.*

enum class CarType(val carType: Int) {

    STANDARD(0),
    PREMIUM(1);

    companion object {
        val DEFAULT = STANDARD
    }

    var title: Int = 0
    get() {
        when(this) {
            STANDARD -> return R.string.txt_standard_car_selection
            PREMIUM -> return R.string.txt_premium_car_selection
        }
    }

    var image: Int = 0
    get() {
        when(this) {
            STANDARD -> return R.drawable.ic_car_type_standard
            PREMIUM -> return R.drawable.ic_cars
        }
    }

}

enum class PaymentMenthod(val paymentMethod: Int) {

    CARD(0),
    BONUSES(1),
    CASH(2);

    var image: Int = 0
    get() {
        when(this) {
            CARD -> return R.drawable.ic_card
            BONUSES -> return R.drawable.ic_bonuses
            CASH -> return R.drawable.ic_cash
        }
    }

    var shortDescription: Int = 0
    get() {
        when(this) {
            CARD -> return R.string.txt_card
            BONUSES -> return R.string.txt_bonuses
            CASH -> return R.string.txt_cash
        }
    }

}

data class StubRoute(val waypoints: List<StubWaypointAddress> = mutableListOf(),
                     var distance: Int?,
                     val carType: CarType,
                     var paymentMethod: PaymentMenthod,
                     var card: Card?,
                     val date: Date)