package com.kross.taxi_passenger.presentation.screen.add_card

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.domain.AddCardViewModel
import com.kross.taxi_passenger.presentation.interfaces.BaseTextWacher
import com.kross.taxi_passenger.presentation.screen.MASK_EDIT_DATE
import com.kross.taxi_passenger.presentation.screen.MASK_EDIT_NUMBER
import com.kross.taxi_passenger.presentation.screen.MaskWatcher
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.main.MainActivity
import com.kross.taxi_passenger.utils.getIntPreference
import com.kross.taxi_passenger.utils.gone
import com.kross.taxi_passenger.utils.visibleOrGone
import kotlinx.android.synthetic.main.activity_add_card.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class AddCardActivity : BaseActivity(), View.OnClickListener, BaseTextWacher, TextView.OnEditorActionListener {

    override var timer: Timer? = Timer()
    override var handler: Handler = Handler(Looper.getMainLooper())

    private val viewModel: AddCardViewModel by viewModel()
    private var menuItemSave: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        setListenersView()
        txtSkipAddCardScreen.visibleOrGone(!isFromMyCard())
    }

    private fun setListenersView() {
        txtSkipAddCardScreen.setOnClickListener(this)

        editAddCardName.setOnEditorActionListener(this)
        editAddCarNumber.setOnEditorActionListener(this)
        editAddCardDate.setOnEditorActionListener(this)
        editAddCardCvv.setOnEditorActionListener(this)

        editAddCardName.addTextChangedListener(this)
        editAddCardCvv.addTextChangedListener(this)
        editAddCarNumber.addTextChangedListener(MaskWatcher(MASK_EDIT_NUMBER))
        editAddCardDate.addTextChangedListener(MaskWatcher(MASK_EDIT_DATE))
        editAddCarNumber.addTextChangedListener(this)
        editAddCardDate.addTextChangedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_card_screen, menu)
        menuItemSave = menu?.findItem(R.id.menuActionSaveAddCard)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.menuActionSaveAddCard)?.isEnabled = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                invokeFinish()
            }
            R.id.menuActionSaveAddCard -> {
                addCard()
                invokeFinish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        invokeFinish()
    }

    private fun invokeFinish() {
        if (!isFromMyCard()) {
            MainActivity.start(this@AddCardActivity)
        }
        finish()
    }

    private fun isFromMyCard() = intent.getBooleanExtra(IS_FROM_MY_CARDS_EXTRA, false);

    private fun addCard() {
        viewModel.addCard(editAddCardName.text.toString(), editAddCarNumber.text.toString().filter { Character.isDigit(it) }.toLong(),
                editAddCardDate.text.toString(), editAddCardCvv.text.toString().toInt(), getIntPreference(R.string.user_id))
    }

    override fun afterTextChangedDelay(text: Editable?) {
        menuItemSave?.isEnabled =
                editAddCarNumber.text.toString().length == resources.getInteger(R.integer.card_number_max_length) &&
                editAddCardDate.text.toString().length == resources.getInteger(R.integer.card_date_max_length) &&
                editAddCardCvv.text.toString().length == resources.getInteger(R.integer.card_cvv_max_length)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        editAddCardName.clearFocus()
        editAddCarNumber.clearFocus()
        editAddCardDate.clearFocus()
        editAddCardCvv.clearFocus()
        hideKeyboard()
        return false
    }

    companion object {
        private const val IS_FROM_MY_CARDS_EXTRA = "IS_FROM_MY_CARDS_EXTRA"

        fun start(activity: Activity) {
            val intent = Intent(activity, AddCardActivity::class.java)
            activity.startActivity(intent)
        }

        fun startForMyCards(activity: Activity) {
            val intent = Intent(activity, AddCardActivity::class.java)
            intent.putExtra(IS_FROM_MY_CARDS_EXTRA, true)
            activity.startActivity(intent)
        }
    }
}