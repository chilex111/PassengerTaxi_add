package com.kross.taxi_passenger.presentation.screen.registration

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.kross.taxi_passenger.ApiKey
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.request.Registration
import com.kross.taxi_passenger.domain.RegistrationViewModel
import com.kross.taxi_passenger.presentation.interfaces.BaseTextWacher
import com.kross.taxi_passenger.presentation.screen.add_card.AddCardActivity
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.utils.*
import kotlinx.android.synthetic.main.activity_registartion.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class RegistrationActivity : BaseActivity(), View.OnClickListener, TextView.OnEditorActionListener{

    val emailValidation = Validation(this)
    val nameValidation = Validation(this)
    val passwordValidation = Validation(this)

    private lateinit var phoneNumber: String
    private lateinit var token: String

    private val viewModel: RegistrationViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registartion)

        phoneNumber = intent.getStringExtra(getString(R.string.KEY_INTENT_REGISTRATION_PHONE))
        token = intent.getStringExtra(getString(R.string.KEY_INTENT_REGISTRATION_TOKEN))

        emailValidation.setMinLength(6).notEmpty().useEmailRegex().hasAtSymbol().hasDot().onlyLatin().canContainSpaces(false)
        nameValidation.setMinLengthField(2)
        passwordValidation.setMinLengthWithCustomHint(6)
        setListenerViews()
        viewModel.getLoadingLiveData().observe(this, android.arch.lifecycle.Observer { it?.let{ progressBarRegistration.visibility = it }})
        handleError()
    }

    private fun setListenerViews(){
        btnNextRegistration.setOnClickListener(this)

        editRegistrationName.setOnEditorActionListener(this)
        editRegistrationLastName.setOnEditorActionListener(this)
        editRegistrationEmail.setOnEditorActionListener(this)
        editRegistrationPassword.setOnEditorActionListener(this)
        editRegistrationCode.setOnEditorActionListener(this)

        setChangeListenerFirstName()
        setChangeListenerLastName()
        setChangeListenerEmail()
        setChangeListenerPassword()
    }

    private fun setChangeListenerFirstName(){
        editRegistrationName.addTextChangedListener(object : BaseTextWacher {
            override var timer: Timer? = Timer()
            override var handler: Handler = Handler(Looper.getMainLooper())
            override fun afterTextChangedDelay(text: Editable?) {
                buttonEnableCheck()
                text?.toString()?.let { inputLayoutRegistrationName.isErrorEnabled = !inputLayoutRegistrationName.isFieldValid(nameValidation) && editRegistrationName.text.toString().isNotEmpty() } } })
    }

    private fun setChangeListenerLastName(){
        editRegistrationLastName.addTextChangedListener(object : BaseTextWacher {
            override var timer: Timer? = Timer()
            override var handler: Handler = Handler(Looper.getMainLooper())
            override fun afterTextChangedDelay(text: Editable?) {
                text?.toString()?.let { inputLayoutRegistrationLastName.isErrorEnabled = !inputLayoutRegistrationLastName.isFieldValid(nameValidation) && editRegistrationLastName.text.toString().isNotEmpty() }
                buttonEnableCheck()
            } })

    }

    private fun setChangeListenerEmail(){
        editRegistrationEmail.addTextChangedListener(object : BaseTextWacher {
            override var timer: Timer? = Timer()
            override var handler: Handler = Handler(Looper.getMainLooper())
            override fun afterTextChangedDelay(text: Editable?) {
                buttonEnableCheck()
                text?.toString()?.let { inputLayoutRegistrationEmail.isErrorEnabled = !inputLayoutRegistrationEmail.isFieldValid(emailValidation) && editRegistrationEmail.text.toString().isNotEmpty() } } })
    }

    private fun setChangeListenerPassword(){
        editRegistrationPassword.addTextChangedListener(object : BaseTextWacher {
            override var timer: Timer? = Timer()
            override var handler: Handler = Handler(Looper.getMainLooper())
            override fun afterTextChangedDelay(text: Editable?) {
                buttonEnableCheck()
                visibilityHintPassword()
                text?.toString()?.let { inputLayoutRegistrationPassword.isErrorEnabled = !inputLayoutRegistrationPassword.isFieldValid(passwordValidation) && editRegistrationPassword.text.toString().isNotEmpty() } } })
    }

    override fun onClick(v: View?) {
        if(isNetworkAvailable()){
            registration()
        }else{
            showSnack(getString(R.string.txt_error_no_connection))
        }
    }

    private fun registration(){
        val registration = Registration(editRegistrationName.text.toString(), editRegistrationLastName.text.toString(),
                phoneNumber, editRegistrationEmail.text.toString(),
                ApiKey(this).getStringInMd5(editRegistrationPassword.text.toString()), editRegistrationCode.text.toString(), token)
        viewModel.registration(this, getStringPreference(R.string.API_KEY), registration).observe(this, android.arch.lifecycle.Observer {
            it?.let {
                saveToSharedPreference(R.string.token, "Bearer ${it.token}")
                saveToSharedPreference(R.string.user_id, it.userId)
                AddCardActivity.start(this@RegistrationActivity)
                finish()
            }
        })
    }

    fun handleError(){
        viewModel.getErrorLiveData().observe(this, android.arch.lifecycle.Observer {
            it?.let {
                showSnack(it)
            }
        })
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            when(v?.id){
                R.id.editRegistrationName -> {
                    inputLayoutRegistrationName.isErrorEnabled = !inputLayoutRegistrationName.isFieldValidWithoutError(nameValidation) && editRegistrationName.text.toString().isNotEmpty()
                    editRegistrationName.clearFocus()
                }
                R.id.editRegistrationLastName -> {
                    inputLayoutRegistrationLastName.isErrorEnabled = !inputLayoutRegistrationLastName.isFieldValidWithoutError(nameValidation) && editRegistrationLastName.text.toString().isNotEmpty()
                    editRegistrationLastName.clearFocus()
                }
                R.id.editRegistrationEmail -> {
                    inputLayoutRegistrationEmail.isErrorEnabled = !inputLayoutRegistrationEmail.isFieldValidWithoutError(emailValidation) && editRegistrationEmail.text.toString().isNotEmpty()
                    editRegistrationEmail.clearFocus()
                }
                R.id.editRegistrationPassword -> {
                    inputLayoutRegistrationPassword.isErrorEnabled = !inputLayoutRegistrationPassword.isFieldValidWithoutError(passwordValidation) && editRegistrationPassword.text.toString().isNotEmpty()
                    editRegistrationPassword.clearFocus()
                }
                R.id.editRegistrationCode -> editRegistrationCode.clearFocus()
            }
        }
        hideKeyboard()
        return false
    }

    private fun visibilityHintPassword(){
        if(inputLayoutRegistrationPassword.isFieldValidWithoutError(passwordValidation)){
            txtRegistrFourthHint.visibility = View.VISIBLE
        }else{
            txtRegistrFourthHint.visibility = View.GONE
        }
    }

    fun buttonEnableCheck(){
        btnNextRegistration.isEnabled = ((inputLayoutRegistrationName.isFieldValidWithoutError(nameValidation))
                && (inputLayoutRegistrationLastName.isFieldValidWithoutError(nameValidation)
                && (inputLayoutRegistrationEmail.isFieldValidWithoutError(emailValidation))
                && (inputLayoutRegistrationPassword.isFieldValidWithoutError(passwordValidation))))
    }

    companion object {
        fun start(activity: Activity, phoneNumber: String?, keyNumber: String,
                  token: String, keyToken: String){
            val intent = Intent(activity, RegistrationActivity::class.java)
            intent.putExtra(keyNumber, phoneNumber)
            intent.putExtra(keyToken, token)
            activity.startActivity(intent)
            activity.finish()
        }
    }
}