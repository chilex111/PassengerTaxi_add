package com.kross.taxi_passenger.presentation.screen.drivers_list

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.response.Driver
import com.kross.taxi_passenger.domain.DriversListViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.driver_details.DriverDetailsActivity
import com.kross.taxi_passenger.presentation.widget.BaseAdapter
import com.kross.taxi_passenger.utils.getStringPreference
import kotlinx.android.synthetic.main.activity_drivers_list.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class DriversListActivity : BaseActivity(), BaseAdapter.OnItemClickListener<Driver>, DriversListAdapter.OnItemClickListener, SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
        return false

    }

    private val REQUEST_CODE: Int = 1111
    private var changeKey = false

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let {
            list?.let {
                filterListMake(newText)
            }
        }
        return false
    }

    override fun onItemClick(view: View, item: Driver) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val adapterDriversList: DriversListAdapter = DriversListAdapter(this, this)

    override fun onItemClick(view: View, id: Int) {
        DriverDetailsActivity.start(this@DriversListActivity, id, intent.getIntExtra(DRIVERS_LISR, 0), false, changeKey, REQUEST_CODE)
        overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
    }

    private val viewModel: DriversListViewModel by viewModel()

    companion object {

        const val DRIVERS_LISR: String = "drivers_list"
        const val CHANGE_KEY: String = "key"

        fun start(activity: Activity, carId: Int, changeKey: Boolean) {
            val intent = Intent(activity, DriversListActivity::class.java)
            intent.putExtra(DRIVERS_LISR, carId)
            intent.putExtra(CHANGE_KEY, changeKey)
            activity.startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        // Check which request we're responding to
        if (requestCode == REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                changeKey = false
            }
        }
    }

    private val list = ArrayList<Driver>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drivers_list)
        initAppBar()
        recyclerDriversList.layoutManager = LinearLayoutManager(this)
        recyclerDriversList.adapter = adapterDriversList
        adapterDriversList.setOnClickListener(this)
        changeKey = intent.getBooleanExtra(CHANGE_KEY, false)
        viewModel.getLiveDataLoading().observe(this, Observer {
            //            it?.let { progressBarDrivers.visibility = it }
        })  //.getDriversFromServer(apiKey, amount)

        viewModel.getDrivers(getStringPreference(R.string.token), 25).observe(this, Observer {
            it?.drivers?.let { it1 -> setUpLists(it1) }
        })
    }

    fun setUpLists(it1: List<Driver>) {
        adapterDriversList.addAll(it1)
        list.addAll(it1)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = getString(R.string.txt_choose_car_make_hint_search)
        searchView.setOnQueryTextListener(this)
        searchView.isIconified = false
        return true
    }

    private fun initAppBar() {
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.my_cars_title)
    }


    fun filterListMake(text: String) {
        if (adapterDriversList.filter(text, list)) {
            recyclerDriversList.visibility = View.VISIBLE
        } else {
            recyclerDriversList.visibility = View.GONE
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
