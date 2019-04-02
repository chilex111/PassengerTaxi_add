package com.kross.taxi_passenger.presentation.screen.cabinet.news

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.response.NewsItem
import com.kross.taxi_passenger.domain.NewsViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.cabinet.news.adapter.NewsAdapter
import com.kross.taxi_passenger.utils.getStringPreference
import kotlinx.android.synthetic.main.activity_news.*
import org.koin.android.viewmodel.ext.android.viewModel


class NewsActivity: BaseActivity() {

    private val viewModel: NewsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        toolbarSettings()
        initAdapter()

        viewModel.getNews(10,0,getStringPreference(R.string.token)).observe(this, Observer {
            if (it != null)
           /* it?.news?.let {
                it1 -> {*/
                    loadStartData(it.news)
          /*  }
            }*/
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    // MARK: - Actions

    // TODO: - pass object if needed

    private fun share(content: NewsItem) {
        val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, content.title)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, content.text)
        sharingIntent.putExtra(Intent.EXTRA_STREAM,content.image)
        startActivity(Intent.createChooser(sharingIntent, "Share Kross Taxi News"))


    }

    private fun readMore(content: NewsItem) {
        ReadMoreActivity.start(this, content.title,content.text,content.published_date,content.image!!)
    }

    // MARK: - Helper methods

    private fun toolbarSettings() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        supportActionBar?.title = getString(R.string.news)
    }

    private fun initAdapter() {
        tableView.layoutManager = LinearLayoutManager(this)
        tableView.adapter = NewsAdapter(this::share, this::readMore, this)
        // loadStartData()
    }

    private fun loadStartData(news: List<NewsItem>) {
        //     tableView.adapter
        (tableView.adapter as NewsAdapter).addObjects(news)
        // TODO: - use (tableView.adapter as NewsAdapter).addObjects(), to update recyclerview
        // TODO: - add onScrollListener to handle preload
    }

}