package com.kross.taxi_passenger.presentation.screen.cabinet.trips

import android.os.Bundle
import android.view.MenuItem
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import kotlinx.android.synthetic.main.activity_container_toolbar.*

class MyTripsActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_toolbar)
        toolbarSettings()
        openMyTripsPagerFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
        return super.onOptionsItemSelected(item)
    }

    // MARK: - Actions

    // MARK: - Helper methods

    private fun toolbarSettings() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        appBarLayout.post { appBarLayout.elevation = .0f}
    }

    private fun openMyTripsPagerFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, TripsPagerFragment.newInstance())
                .addToBackStack(null)
                .commit()
    }

    // MARK: - Navigation

}