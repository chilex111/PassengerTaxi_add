package com.kross.taxi_passenger.presentation.screen.payment.mycards

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.MenuItem
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.Card
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import kotlinx.android.synthetic.main.activity_container_toolbar.*

class MyCardsActivity: BaseActivity() {

    companion object {
        const val CHOOSE_MY_CARD_RESULT_EXTRA = "CHOOSE_MY_CARD_RESULT_EXTRA"

        fun startForResult(fragment: Fragment, requestCode: Int) {
            val intent = Intent(fragment.activity, MyCardsActivity::class.java)
            fragment.startActivityForResult(intent, requestCode)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_toolbar)
        if(savedInstanceState == null) openMyCardsFragment()
        toolbarSettings()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> back()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openMyCardsFragment() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, MyCardsFragment.newIntstance())
                .commit()
    }

    private fun toolbarSettings() {
        setSupportActionBar(toolbar)
        val upArrow = resources.getDrawable(R.drawable.ic_back_white, null)
        supportActionBar?.setHomeAsUpIndicator(upArrow)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun deliverResult(it: Card) {
        Intent().apply {
            putExtra(CHOOSE_MY_CARD_RESULT_EXTRA, it)
            setResult(Activity.RESULT_OK, this)
        }
        back()
    }

    private fun back() {
        finish()
        overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
    }

}