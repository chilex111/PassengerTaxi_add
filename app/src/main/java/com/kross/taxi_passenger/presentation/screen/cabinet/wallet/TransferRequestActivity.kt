package com.kross.taxi_passenger.presentation.screen.cabinet.wallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.gson.JsonObject
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.domain.CabinetViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.cabinet.wallet.WalletActivity.Companion.BONUS_BALANCE
import com.kross.taxi_passenger.utils.getStringPreference
import com.kross.taxi_passenger.utils.textObserver
import kotlinx.android.synthetic.main.activity_transfer_request.*
import org.jetbrains.anko.enabled
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.DecimalFormat

class TransferRequestActivity: BaseActivity() {

    companion object {

        fun start(activity: Activity, balance: String) {
            val intent = Intent(activity, TransferRequestActivity::class.java)
            intent.putExtra(BONUS_BALANCE, balance)
            activity.startActivity(intent)
        }
    }

    private val viewModel: CabinetViewModel by viewModel()
    private lateinit var balance: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer_request)
        balance = intent.getStringExtra(BONUS_BALANCE)
        initAppBar()
        fillInTextViews(0f)
        bindButton()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    // MARK: - Actions

    fun requestBonuses(v: View) {
        val json = JsonObject()
        json.addProperty("amount", inputText.text.toString())
        val token = getStringPreference(R.string.token)
        viewModel.paymentWithDraw(token, json) { success, message ->
            if (success) {
                finish()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    // MARK: - Helper methods

    private fun initAppBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
    }

    private fun fillInTextViews(number: Float) {
        val formatter = DecimalFormat("#.##")
        if (number == 0f) {
            courseTextView.text = String.format(getString(R.string.txt_transfer_converted), "1", "1")
            amountTextView.text = String.format(getString(R.string.txt_transfer_left), formatter.format(balance.toFloat()))
        } else {
            val left = balance.toFloat() - number
            courseTextView.text = String.format(getString(R.string.txt_transfer_converted), formatter.format(number) , formatter.format(number))
            amountTextView.text = String.format(getString(R.string.txt_transfer_left), formatter.format(left))
            errorTextView.alpha = if (left < 0) 1f else 0f
        }
    }

    private fun bindButton() {
        val textObserver = inputText.textObserver()
        textObserver.subscribe ({
            fillInTextViews(if(it.count() > 0) it.toFloat() else 0f)
            requestButton.enabled = (it.count() > 0 && errorTextView.alpha == 0f)
        }, {
            print("error")
        })
    }

}