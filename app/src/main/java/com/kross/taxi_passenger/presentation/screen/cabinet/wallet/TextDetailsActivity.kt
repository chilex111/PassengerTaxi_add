package com.kross.taxi_passenger.presentation.screen.cabinet.wallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.cabinet.wallet.adapter.WalletEnum
import com.kross.taxi_passenger.utils.visibleOrGone
import kotlinx.android.synthetic.main.activity_text_details.*

class TextDetailsActivity: BaseActivity() {

    companion object {

        const val DETAILS_TYPE: String = "index"

        fun start(activity: Activity, type: Int) {
            val intent = Intent(activity, TextDetailsActivity::class.java)
            intent.putExtra(DETAILS_TYPE, type)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
        }
    }

    private lateinit var type: WalletEnum

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_details)
        type = WalletEnum.values()[intent.getIntExtra(DETAILS_TYPE, 0)]
        initAppBar()
        initContent()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
        return super.onOptionsItemSelected(item)
    }

    // MARK: - Actions

    // MARK: - Helper methods

    private fun initAppBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        supportActionBar?.title = getString(type.title)
    }

    private fun initContent() {
        if (type != WalletEnum.trips) imageView.visibleOrGone(false)
        textView.text = getString(type.details)
    }

}