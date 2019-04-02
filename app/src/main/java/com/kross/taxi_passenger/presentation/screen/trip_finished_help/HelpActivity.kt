package com.kross.taxi_passenger.presentation.screen.trip_finished_help

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.cabinet.wallet.adapter.ListSuperClass
import com.kross.taxi_passenger.presentation.screen.cabinet.wallet.adapter.WXListAdapter
import kotlinx.android.synthetic.main.activity_wallet.*
import org.jetbrains.anko.startActivity

enum class HelpEnum : ListSuperClass {

    accident, refund, lost, driver, vehicle, different;

    override var details: Int = 0
    override var index: Int = ordinal
    override var titleString: String = ""

    override var title: Int = 0
        get() {
            return when (this) {
                accident -> R.string.text_help_accident
                refund -> R.string.text_help_refund
                lost -> R.string.text_help_lost
                driver -> R.string.text_help_driver
                vehicle -> R.string.text_help_vehicle
                different -> R.string.text_help_different
            }
        }

}

class HelpActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        initAppBar()
        initViews()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    // MARK: - Actions

    // MARK: - Helper methods

    private fun initAppBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)
    }

    private fun initViews() {
        tableView.layoutManager = LinearLayoutManager(this)
        tableView.adapter = WXListAdapter(HelpEnum.values() as Array<ListSuperClass>, this::onTapListener)
    }

    private fun onTapListener(index: Int) {
        startActivity<HelpDescriptionActivity>(SCREEN_TITLE to getString(HelpEnum.values()[index].title),
                HELP_ID to index)
    }

}