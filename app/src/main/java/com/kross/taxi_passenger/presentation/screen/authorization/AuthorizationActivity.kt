package com.kross.taxi_passenger.presentation.screen.authorization

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.kross.taxi_passenger.ApiKey
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.request.Authorization
import com.kross.taxi_passenger.domain.AuthorizationViewModel
import com.kross.taxi_passenger.presentation.interfaces.BaseTextWacher
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.main.MainActivity
import com.kross.taxi_passenger.utils.*
import kotlinx.android.synthetic.main.activity_authorization.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class AuthorizationActivity : BaseActivity(), BaseTextWacher, TextView.OnEditorActionListener, View.OnClickListener{

    override var timer: Timer? = Timer()
    override var handler: Handler = Handler(Looper.getMainLooper())

    private val authorization = Authorization()

    private lateinit var apiKey: ApiKey

    private val validation = Validation(this).notEmpty()

    private val viewModel: AuthorizationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        apiKey = ApiKey(this)

        authorization.phoneNumber = intent.getStringExtra(getString(R.string.KEY_INTENT_AUTHORIZATION))
        viewModel.getLiveDataLoading().observe(this, android.arch.lifecycle.Observer { it?.let { progressBarAuth.visibility = it } })
        setListenersForView()
    }

    private fun setListenersForView(){
        editAuthPassword.setOnEditorActionListener(this)
        editAuthPassword.addTextChangedListener(this)
        btnAuthNext.setOnClickListener(this)
        txtForgotPassword.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun afterTextChangedDelay(text: Editable?) {
        inputLayoutAuthPassword.isErrorEnabled = !inputLayoutAuthPassword.isFieldValid(validation)
        btnAuthNext.isEnabled = inputLayoutAuthPassword.isFieldValid(validation)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        editAuthPassword.clearFocus()
        hideKeyboardEx()
        return false
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnAuthNext -> {
                authorization()
                hideKeyboardEx()
            }
            R.id.txtForgotPassword -> {
                recoveryPassword()
            }
        }
    }

    private fun recoveryPassword(){
        val jsonObject = viewModel.createJsonObject(resources.getInteger(R.integer.user_type), authorization.phoneNumber)
        viewModel.passwordRecovery(this, getString(R.string.CONTENT_TYPE), getStringPreference(R.string.API_KEY), jsonObject)
                .observe(this, android.arch.lifecycle.Observer { it?.let {handleRecovery(it)} })
    }

    private fun handleRecovery(result: Boolean){
        if(result){
            AlertDialogHelper.createDialog(this,
                    R.string.txt_auth_screen_dialog_title,
                    R.string.txt_auth_screen_dialog_message,
                    null,
                    resources.getString(R.string.txt_auth_screen_dialog_pos_btn))}
        else{
            showSnack(getString(R.string.error_message_user_not_found))
        }
    }

    private fun authorization() {
        authorization.password = apiKey.getStringInMd5(editAuthPassword.text.toString())
        viewModel.authorization(getString(R.string.CONTENT_TYPE), getStringPreference(R.string.API_KEY), authorization)
        viewModel.getLiveDataToken().observe(this, android.arch.lifecycle.Observer {
            it?.token?.let { saveToSharedPreference(R.string.token, "Bearer $it")
                MainActivity.start(this)
                finish()}
        })
        viewModel.getLiveDataError().observe(this, android.arch.lifecycle.Observer { it?.let { handleError(it) } })
    }

    private fun handleError(errorCode: Int){
        when(errorCode){
            resources.getInteger(R.integer.error_code_wrong_password) ->{
                inputLayoutAuthPassword.isEnabled = true
                inputLayoutAuthPassword.error = getString(R.string.txt_auth_screen_wrong_password)
            }
            resources.getInteger(R.integer.error_code_user_blocked) -> {
            }
            resources.getInteger(R.integer.error_code_user_not_found) -> {
            }
        }
    }

    companion object {
        fun start(activity: Activity, phoneNumber: String?, key: String){
            val intent = Intent(activity, AuthorizationActivity::class.java)
            intent.putExtra(key, phoneNumber)
            activity.startActivity(intent)
            activity.finish()
        }
    }
}