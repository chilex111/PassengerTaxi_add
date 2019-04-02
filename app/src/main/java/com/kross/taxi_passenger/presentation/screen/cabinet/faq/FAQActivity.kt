package com.kross.taxi_passenger.presentation.screen.cabinet.faq

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.response.FAQ
import com.kross.taxi_passenger.domain.FAQViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.utils.getStringPreference
import kotlinx.android.synthetic.main.activity_list.*
import org.koin.android.viewmodel.ext.android.viewModel

class FAQActivity : BaseActivity() {

    private val viewModel: FAQViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        toolbarSettings()
        initAdapter()

        viewModel.getFAQ(getStringPreference(R.string.token)).observe(this, Observer {
            if (it != null){
                loadStartData(it)
            }
        })
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun toolbarSettings() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        supportActionBar?.title = getString(R.string.txt_cabinet_faq)
    }

    private fun initAdapter() {
        tableView.layoutManager = LinearLayoutManager(this)
        tableView.adapter = FAQAdapter(this)
        // loadStartData()
    }

    private fun loadStartData(faqs: List<FAQ>) {
        //     tableView.adapter
        (tableView.adapter as FAQAdapter).addObjects(faqs)
        // TODO: - use (tableView.adapter as NewsAdapter).addObjects(), to update recyclerview
        // TODO: - add onScrollListener to handle preload
    }

}
