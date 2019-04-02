package com.kross.taxi_passenger.presentation.screen.verification_waiting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import kotlinx.android.synthetic.main.activity_verification_waiting.*

class VerificationWaitingActivity : BaseActivity() {

    companion object {

        const val VERIFICATION_WAITING: String = "waiting"

        fun start(activity: Activity, status: Int) {
            val intent = Intent(activity, VerificationWaitingActivity::class.java)
            intent.putExtra(VERIFICATION_WAITING, status)
            activity.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification_waiting)
        val status = intent.getIntExtra(VERIFICATION_WAITING, -1)
        initAppBar(status)
        setUpViews(status)
    }

    private fun initAppBar(status: Int) {
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (status == 0) {
            supportActionBar?.title = getString(R.string.title_car_owner)
        } else {
            supportActionBar?.title = getString(R.string.title_verification)
        }
    }

    private fun setUpViews(status: Int) {
        if (status == 1) {
            appliedVerificationLayout.visibility = View.GONE
            officeInviteLayout.visibility = View.VISIBLE
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
}
