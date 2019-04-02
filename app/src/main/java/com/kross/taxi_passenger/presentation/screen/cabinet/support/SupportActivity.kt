package com.kross.taxi_passenger.presentation.screen.cabinet.support

import android.arch.lifecycle.Observer
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.response.HelpList
import com.kross.taxi_passenger.domain.SupportViewModel
import com.kross.taxi_passenger.utils.getStringPreference
import kotlinx.android.synthetic.main.activity_list.view.*
import kotlinx.android.synthetic.main.activity_support.*
import org.koin.android.viewmodel.ext.android.viewModel

class SupportActivity : AppCompatActivity() {
    private val viewModel: SupportViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)

        toolbarSettings()
        initAdapter()

        txtAppeal.setOnClickListener {
            txtAppeal.setTypeface(null, Typeface.BOLD)
            txtRefund.setTypeface(null, Typeface.NORMAL)
            txtComplaint.setTypeface(null, Typeface.NORMAL)
            getHelpList(1)

        }

        txtComplaint.setOnClickListener {
            txtComplaint.setTypeface(null, Typeface.BOLD)
            txtRefund.setTypeface(null, Typeface.NORMAL)
            txtAppeal.setTypeface(null, Typeface.NORMAL)
           getHelpList(0)
        }
        txtRefund.setOnClickListener {
            txtRefund.setTypeface(null, Typeface.BOLD)
            txtAppeal.setTypeface(null, Typeface.NORMAL)
            txtComplaint.setTypeface(null, Typeface.NORMAL)
          getHelpList(2)
        }
    }

    private fun getHelpList(i: Int) {
       // ( recycler?.tableView?.adapter as SupportAdapter).clearAdapter()
        viewModel.getHelpList(getStringPreference(R.string.token),i ).observe(this, Observer {
            ( recycler?.tableView?.adapter as SupportAdapter).clearAdapter()
            loadStartData(it!!)
        }) }

    private fun makeComplaint(help: HelpList){
        ComplaintsActivity.start(this, help.id, help.title)
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun toolbarSettings() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        supportActionBar?.title = getString(R.string.txt_cabinet_support)
    }

    private fun initAdapter() {
        recycler?.tableView?.layoutManager = LinearLayoutManager(this)
        recycler?.tableView?.adapter = SupportAdapter(this::makeComplaint,this)

        // loadStartData()
    }

    private fun loadStartData(faqs: List<HelpList>) {
        //     tableView.adapter
        ( recycler?.tableView?.adapter as SupportAdapter).addObjects(faqs)
        // TODO: - use (tableView.adapter as NewsAdapter).addObjects(), to update recyclerview
        // TODO: - add onScrollListener to handle preload
    }

}
