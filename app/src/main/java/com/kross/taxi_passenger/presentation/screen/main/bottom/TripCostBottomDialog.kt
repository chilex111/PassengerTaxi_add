package com.kross.taxi_passenger.presentation.screen.main.bottom


import android.view.ViewGroup
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.OrderEntity
import com.kross.taxi_passenger.domain.entity.CarType
import com.kross.taxi_passenger.domain.entity.PaymentMenthod
import com.kross.taxi_passenger.domain.entity.StubTripRates
import com.kross.taxi_passenger.utils.calculateCost
import kotlinx.android.synthetic.main.bottom_dialog_trip_cost.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.image
import java.util.*

class TripCostBottomDialog(view: ViewGroup, order: OrderEntity) : BottomDialog(view, R.layout.bottom_dialog_trip_cost) {


    var closeListener: (() -> Unit)? = null
    var nextListener: (() -> Unit)? = null
    var driverNoteListener: (() -> Unit)? = null
    var carTypeListener: (() -> Unit)? = null
    var timeListener: (() -> Unit)? = null
    var cardListener: (() -> Unit)? = null

    init {
        btnNext.enabled = true

        updateView(order)
        updateCommentView(order.comment)
        updateCarType(CarType.STANDARD)

        iv_close.setOnClickListener { closeListener?.invoke() }

        btnNext.setOnClickListener { nextListener?.invoke() }

        tvLeaveNote.setOnClickListener { driverNoteListener?.invoke() }
        ivLeaveNote.setOnClickListener { driverNoteListener?.invoke() }

        ivCarType.setOnClickListener { carTypeListener?.invoke() }
        tvCarType.setOnClickListener { carTypeListener?.invoke() }

        ivTime.setOnClickListener { timeListener?.invoke() }
        tvTime.setOnClickListener { timeListener?.invoke() }

        ivCard.setOnClickListener { cardListener?.invoke() }
        tvCard.setOnClickListener { cardListener?.invoke() }
    }

    fun updateView(order: OrderEntity) {
        val resources = containerView.resources
        val tripCost = calculateCost(order.distance, StubTripRates())
        tvProxPrice.text = resources.getString(R.string.prox_price,
                String.format(Locale.US, "%.2f", tripCost.min),
                String.format(Locale.US, "%.2f", tripCost.max))
        updatePayment(PaymentMenthod.values()[order.paymentType], order.cardNumber)
    }

    fun updatePayment(method: PaymentMenthod, cardNumber: String?) {
        ivCard.setImageResource(method.image)
        tvCard.text = if(method == PaymentMenthod.CARD)
            if(cardNumber == null) "**0000"
            else "**${cardNumber.substring(cardNumber.length - 4)}"
        else containerView.resources.getString(method.shortDescription)
    }

    fun updateCommentView(message: String) {
        val resources = containerView.resources
        tvLeaveNote.text = if(message.isEmpty()) resources.getString(R.string.leave_not_for_driver) else resources.getString(R.string.note_for_driver_was_added)
    }

    fun updateDate(time: String) {
        tvTime.text = time
    }

    fun updateCarType(carType: CarType){
        tvCarType.text = containerView.resources.getString(carType.title)
        ivCarType.setImageResource(carType.image)
    }

}