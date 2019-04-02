package com.kross.taxi_passenger.presentation.screen.payment.choose

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.Card
import com.kross.taxi_passenger.domain.entity.PaymentMenthod
import com.kross.taxi_passenger.presentation.screen.base.BaseFragment
import com.kross.taxi_passenger.presentation.screen.payment.mycards.MyCardsActivity
import com.kross.taxi_passenger.utils.argOrThrow
import kotlinx.android.synthetic.main.fragment_payment_methods.*

class ChoosePaymentFragment: BaseFragment() {

    companion object {
        const val MY_CARD_REQUEST_CODE = 190

        fun newInstance(): ChoosePaymentFragment {
            return ChoosePaymentFragment()
        }
    }

    override fun getLayout() = R.layout.fragment_payment_methods

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardClickArea.setOnClickListener {
            MyCardsActivity.startForResult(this, MY_CARD_REQUEST_CODE)
        }

        cashClickArea.setOnClickListener {
            (activity as? ChoosePaymentActivity)?.deliverResult(PaymentMenthod.CASH)
        }

        bonusesClickArea.setOnClickListener {
            (activity as? ChoosePaymentActivity)?.deliverResult(PaymentMenthod.BONUSES)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            MY_CARD_REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK && data != null) {
                    val card: Card = data.argOrThrow(MyCardsActivity.CHOOSE_MY_CARD_RESULT_EXTRA)

                    (activity as? ChoosePaymentActivity)?.deliverResult(PaymentMenthod.CARD, card)
                }
            }
        }
    }
}