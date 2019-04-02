package com.kross.taxi_passenger.presentation.screen.leave_note_and_passenger_number

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.interfaces.BaseTextWacher
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.main.map.MapFragment
import com.kross.taxi_passenger.presentation.screen.time_trip.SetTimeTripActivity
import com.kross.taxi_passenger.utils.showToast
import com.kross.taxi_passenger.utils.visibleOrGone
import kotlinx.android.synthetic.main.activity_note_and_number.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import java.util.*

class NoteAndNumberActivity: BaseActivity(), TextView.OnEditorActionListener, BaseTextWacher{

    override var timer: Timer? = Timer()
    override var handler: Handler = Handler(Looper.getMainLooper())

    private var menuSave: MenuItem? = null
    private var requestCode: Int? = null

    companion object {
        const val NOTE_RESULT_KEY= "NOTE_RESULT_KEY"
        const val PHONE_CODE_KEY = "PHONE_CODE_KEY"

        fun start(fragment: Fragment, requestCode: Int, comment: String?) {
            val intent = Intent(fragment.context, NoteAndNumberActivity::class.java)
            intent.putExtra(NOTE_RESULT_KEY, comment)
            fragment.startActivityForResult(intent, requestCode)
            fragment.activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
        }

        fun start(activity: Activity, requestCode: Int, comment: String?) {
            val intent = Intent(activity, NoteAndNumberActivity::class.java)
            intent.putExtra(NOTE_RESULT_KEY, comment)
            intent.putExtra(activity.getString(R.string.txt_request_code), requestCode)
            activity.startActivityForResult(intent, requestCode)
            activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_and_number)

        setSupportActionBar(toolbarNoteAndNumberScreen.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        requestCode = intent.extras.getInt(getString(R.string.txt_request_code))
        val comment = intent.extras.getString(NOTE_RESULT_KEY)
        setupScreen(requestCode, comment)
        setListenersView()
    }

    private fun setupScreen(requestCode: Int?, comment: String){
        when(requestCode){
            MapFragment.LEAVE_NOTE_REQUEST_CODE -> {
                setupEditText(comment)
                inputLayoutLeaveNote.visibleOrGone(true)
            }
            MapFragment.CHOOSE_NUMBER_REQUEST_CODE -> {
                supportActionBar?.title = getString(R.string.txt_label_phone_number)
                inputLayoutPhoneCode.visibleOrGone(true)
                inputLayoutPhoneNum.visibleOrGone(true)
            }
        }
    }

    private fun setListenersView(){
        editLeaveNote.setOnEditorActionListener(this)
        editPhoneNumber.setOnEditorActionListener(this)

        editLeaveNote.addTextChangedListener(this)
        editPhoneNumber.addTextChangedListener(this)
    }

    private fun setupEditText(comment: String){
        editLeaveNote.imeOptions = EditorInfo.IME_ACTION_DONE
        editLeaveNote.setRawInputType(InputType.TYPE_CLASS_TEXT)
        editLeaveNote.setText(comment)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.menuActionSaveAddCard)?.isEnabled = false
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_card_screen, menu)
        menuSave = menu?.findItem(R.id.menuActionSaveAddCard)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> finish()
            R.id.menuActionSaveAddCard -> deliveryResult()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        editLeaveNote.clearFocus()
        editPhoneNumber.clearFocus()
        hideKeyboard()
        return false
    }

    override fun afterTextChangedDelay(text: Editable?) {
        menuSave?.isEnabled = editLeaveNote.text.isNotEmpty() || (editPhoneNumber.text.isNotEmpty() && editPhoneNumber.text.length == 9)
    }

    private fun deliveryResult(){
        when(requestCode){
            MapFragment.LEAVE_NOTE_REQUEST_CODE -> { deliverResult(editLeaveNote.text.toString(), NOTE_RESULT_KEY)}
            MapFragment.CHOOSE_NUMBER_REQUEST_CODE -> {deliverResult("+234${editPhoneNumber.text.toString()}", PHONE_CODE_KEY)}
        }
    }

    fun deliverResult(data: String, keyResult: String) {
        Intent().apply {
            putExtra(keyResult, data)
            setResult(Activity.RESULT_OK, this)
        }
        hideKeyboard()
        finish()
    }

}