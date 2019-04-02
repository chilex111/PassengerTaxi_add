package com.kross.taxi_passenger.presentation.screen.cancel_trip

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.MapFragment
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.OrderEntity
import com.kross.taxi_passenger.domain.CancelTripViewModel
import com.kross.taxi_passenger.presentation.interfaces.BaseTextWacher
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.cancel_trip.adapter.CancelTripAdapter
import com.kross.taxi_passenger.presentation.screen.cancel_trip.adapter.CancelTripEnum
import com.kross.taxi_passenger.presentation.screen.main.map.MapFragment.Companion.TRIP_CANCELED_REQUEST_CODE
import com.kross.taxi_passenger.presentation.screen.payment.choose.ChoosePaymentActivity
import com.kross.taxi_passenger.utils.hideKeyboardEx
import kotlinx.android.synthetic.main.activity_authorization.*
import kotlinx.android.synthetic.main.activity_trip_cancel.*
import org.jetbrains.anko.enabled
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class CancelTripActivity: BaseActivity(), BaseTextWacher, TextView.OnEditorActionListener {

    companion object {

        const val ORDER_ID: String = "orderId"

        fun start(fragment: Fragment, orderId: Int, requestCode: Int) {
            val intent = Intent(fragment.context, CancelTripActivity::class.java)
            intent.putExtra(ORDER_ID, orderId)
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    override var timer: Timer? = Timer()
    override var handler: Handler = Handler(Looper.getMainLooper())

    private var checkMap: HashMap<Int, Boolean> = HashMap()
    private val viewModel: CancelTripViewModel by viewModel()

    var order: OrderEntity? = null

    // MARK: - Life cycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_cancel)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        order = viewModel.getOrderBy(intent.getIntExtra(ORDER_ID, 0))
        initRecycler()
        initValues()
        addListeners()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    // MARK: - Actions

    fun cancelAnswer(v: View) {
        if (order == null) return
        viewModel.cancelOrder(order!!, Pair(checkMap
                .filter {
                    it.value
                }.keys.toList(), textInput.text.toString())) {
            if (it) {
                Intent().apply {
                    setResult(Activity.RESULT_OK, this)
                }
                finish()
            }
        }
    }

    private fun handleCheck(index: Int, isChecked: Boolean) {
        checkMap[index] = isChecked
        checkButtonState()
    }

    // MARK: - Helper methods

    private fun initValues() {
        CancelTripEnum.values().forEach {
            checkMap[it.ordinal] = false
        }
    }

    private fun initRecycler() {
        tableView.layoutManager = LinearLayoutManager(this)
        tableView.adapter = CancelTripAdapter(this::handleCheck)
    }

    private fun checkButtonState() {
        val isEnabled = (checkMap.values
                .firstOrNull() {
                    it == true
                } != null || !textInput.text.isNullOrEmpty()) && textInput.text.count() < 250
        answerButton.enabled = isEnabled
    }

    // MARK: - TextInput methods

    private fun addListeners() {
        textInput.setOnEditorActionListener(this)
        textInput.addTextChangedListener(this)
    }

    override fun afterTextChangedDelay(text: Editable?) {
        checkButtonState()
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        editAuthPassword.clearFocus()
        hideKeyboardEx()
        return false
    }

}