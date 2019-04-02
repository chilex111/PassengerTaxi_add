package com.kross.taxi_passenger.presentation.screen.login

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.facebook.accountkit.AccountKitLoginResult
import com.facebook.accountkit.ui.AccountKitActivity
import com.facebook.accountkit.ui.AccountKitConfiguration
import com.facebook.accountkit.ui.LoginType
import com.facebook.accountkit.ui.SkinManager
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.response.PhoneConfirm
import com.kross.taxi_passenger.domain.LoginViewModel
import com.kross.taxi_passenger.presentation.screen.authorization.AuthorizationActivity
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.registration.RegistrationActivity
import com.kross.taxi_passenger.utils.debugLog
import com.kross.taxi_passenger.utils.getStringPreference
import com.kross.taxi_passenger.utils.saveToSharedPreference
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity(){


    private val REQUEST_FACEBOOK_CODE = 101

    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        onSMSLoginFlow()
    }

    fun onSMSLoginFlow() {
        val intent = Intent(this, AccountKitActivity::class.java)
        val uiManager = SkinManager(SkinManager.Skin.CONTEMPORARY,
                ContextCompat.getColor(this, R.color.colorAccent),
                R.drawable.icon_login_screen,
                SkinManager.Tint.WHITE,
                0.6)
        val configurationBuilder = AccountKitConfiguration.AccountKitConfigurationBuilder(
                LoginType.PHONE,
                AccountKitActivity.ResponseType.CODE)
        configurationBuilder.setUIManager(uiManager)
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build())
        startActivityForResult(intent, REQUEST_FACEBOOK_CODE)
    }

    fun login(loginResult: AccountKitLoginResult){
        val jsonObject  = loginViewModel.createJsonObject(resources.getInteger(R.integer.user_type), loginResult.authorizationCode.toString())
        loginViewModel.phoneConfirm(resources.getString(R.string.CONTENT_TYPE), getStringPreference(R.string.API_KEY), resources.getString(R.string.CACHE_CONNTROL), jsonObject)
        loginViewModel.getLiveDataPhoneConfirm().observe(this, Observer<PhoneConfirm> {
            val phone = it?.phoneNumber?.let {return@let it}

            it?.token?.let {
                RegistrationActivity.start(this, phone, getString(R.string.KEY_INTENT_REGISTRATION_PHONE), it, getString(R.string.KEY_INTENT_REGISTRATION_TOKEN))
            }
            it?.userId?.let {
                saveToSharedPreference(R.string.user_id, it)
                AuthorizationActivity.start(this, phone, getString(R.string.KEY_INTENT_AUTHORIZATION))
            }
        })
        loginViewModel.getLiveDataError().observe(this, Observer<String> {
            it?.let {
                txtAuthError.visibility  = View.VISIBLE
                progressBarLineAuth.visibility = View.GONE }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQUEST_FACEBOOK_CODE ->{
                    val loginResult = data.getParcelableExtra<AccountKitLoginResult>(AccountKitLoginResult.RESULT_KEY)
                    loginResult.error?.let {   debugLog(this, loginResult.error?.errorType?.message ?: " ") }
                    login(loginResult)
                }
            }
        }else if (resultCode == Activity.RESULT_CANCELED){
            debugLog(this,  "Was result canceled")
            finish()
        }
    }

    companion object {
        fun start(activity: Activity){
            val intent = Intent(activity, LoginActivity::class.java)
            activity.startActivity(intent)
        }
    }
}