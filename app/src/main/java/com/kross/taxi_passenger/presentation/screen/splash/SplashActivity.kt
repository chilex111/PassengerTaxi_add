package com.kross.taxi_passenger.presentation.screen.splash

import android.os.Bundle
import android.util.Log
import com.kross.taxi_passenger.ApiKey
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.login.LoginActivity
import com.kross.taxi_passenger.presentation.screen.main.MainActivity
import com.kross.taxi_passenger.presentation.screen.onboarding.OnboardingActivity
import com.kross.taxi_passenger.presentation.screen.one_more_trip.OneMoreTripActivity
import com.kross.taxi_passenger.utils.*
import org.jetbrains.anko.startActivity

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        saveToSharedPreference(R.string.API_KEY, "Key ${ApiKey(this).getPassagerApiKey()}")

        if(getBooleanPreference(R.string.onboarding_passed_key)){
            openAuthOrUnauthScreen()
        } else {
            startActivity<OnboardingActivity>()
        }
        finish()
    }

    private fun openAuthOrUnauthScreen(){
        if(getStringPreference(R.string.token).isNotEmpty()){
            startActivity<MainActivity>()
        } else {
            startActivity<LoginActivity>()
        }
    }

}