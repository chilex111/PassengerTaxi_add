package com.kross.taxi_passenger.presentation.screen.become_car_owner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.car_owner_registration.CarOwnerRegistrationActivity
import kotlinx.android.synthetic.main.activity_become_owner.*

class BecomeOwnerActivity : BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.becomeCarOwnerBtn -> {
                CarOwnerRegistrationActivity.start(this@BecomeOwnerActivity, "ff")
                overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_become_owner)
        initAppBar()
        setListenersView()
    }

    companion object {

        fun start(activity: Activity) {
            val intent = Intent(activity, BecomeOwnerActivity::class.java)
            activity.startActivity(intent)
        }

    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setListenersView() {
        becomeCarOwnerBtn.setOnClickListener(this)
    }

    private fun initAppBar() {
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_car_owner)
    }

}
