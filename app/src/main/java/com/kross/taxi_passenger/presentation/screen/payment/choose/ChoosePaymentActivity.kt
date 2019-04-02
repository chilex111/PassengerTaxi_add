package com.kross.taxi_passenger.presentation.screen.payment.choose

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.MenuItem
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.Card
import com.kross.taxi_passenger.domain.entity.PaymentMenthod
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import kotlinx.android.synthetic.main.activity_container_toolbar.*

class ChoosePaymentActivity: BaseActivity() {

    companion object {
        const val CARD_RESULT_EXTRA = "CARD_RESULT_EXTRA"
        const val PAYMENT_TYPE_EXTRA = "PAYMENT_TYPE_EXTRA"

        fun start(fragment: Fragment, requestCode: Int) {
            val intent = Intent(fragment.context, ChoosePaymentActivity::class.java)
            fragment.startActivityForResult(intent, requestCode)
            fragment.activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
        }

        fun start(activity: Activity, requestCode: Int) {
            val intent = Intent(activity, ChoosePaymentActivity::class.java)
            activity.startActivityForResult(intent, requestCode)
            activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_toolbar)
        setSupportActionBar(toolbar)

        val upArrow = resources.getDrawable(R.drawable.ic_close, null)
        supportActionBar?.setHomeAsUpIndicator(upArrow)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, ChoosePaymentFragment.newInstance())
                    .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun deliverResult(paymentMethod: PaymentMenthod, card: Card? = null) {
        Intent().apply {
            if(card != null) {
                putExtra(CARD_RESULT_EXTRA, card.cardNumber)
            }

            putExtra(PAYMENT_TYPE_EXTRA, paymentMethod)
            setResult(Activity.RESULT_OK, this)
        }
        finish()
    }
}