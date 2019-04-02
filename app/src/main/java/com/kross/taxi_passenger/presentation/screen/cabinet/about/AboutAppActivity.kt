package com.kross.taxi_passenger.presentation.screen.cabinet.about

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.MenuItem
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.domain.AboutActivityViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.utils.getStringPreference
import kotlinx.android.synthetic.main.activity_about_app.*
import org.koin.android.viewmodel.ext.android.viewModel

// TODO: - implement screens described below

// FAQ - screen - inherit logic of Help, use WXListAdapter
// Support - screen - inherit logic of NoteAndNumberActivity, maybe adapt screen for any entry data

class AboutAppActivity: BaseActivity() {

    private val viewModel: AboutActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)
        toolbarSettings()
        getAboutText()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    fun getAboutText(){
        viewModel.getAboutText(getStringPreference(R.string.token)).observe(this, Observer {
            aboutTv.text = it
        })
    }
    // MARK: - Actions

    // MARK: - Helper methods

    private fun toolbarSettings() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
    }

}