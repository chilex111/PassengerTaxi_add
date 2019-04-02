package com.kross.taxi_passenger.presentation.screen.cabinet.news

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kross.taxi_passenger.R
import kotlinx.android.synthetic.main.activity_read_more.*
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity


class ReadMoreActivity : BaseActivity() {
    private var title: String ?= null
    private var image: String ?= null
    private var detail: String ?= null
    private var pubDate: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.kross.taxi_passenger.R.layout.activity_read_more)
        //intent.getStringExtra(
        val extras = intent.extras
        if (extras != null) {

            image = extras.getString(IMAGE)
            title = extras.getString(TITLE)
            pubDate = extras.getString(DATE)
            detail = extras.getString(BODY)
            displayNews()
        }
        toolbarSettings()
    }

    private fun displayNews() {
        Glide.with(this)
                .asBitmap()
                .load(image)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView)

        pubDateTv.text = pubDate
        descriptionTextView.movementMethod = ScrollingMovementMethod()
        descriptionTextView.text = detail
        titleTextView.text = title
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.share_menu, menu)
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        when(item?.itemId){
            R.id.action_share-> {
                val uri = Uri.parse(image)

                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "*/*"
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title)
                sharingIntent.putExtra(Intent.EXTRA_TEXT, detail)
                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri.toString())

                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(Intent.createChooser(sharingIntent, "Share Kross Taxi News"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toolbarSettings() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        //supportActionBar?.setIcon(R.drawable.ic_share_cabinet_grey)
        supportActionBar?.title = title
    }

    companion object {

        const val TITLE: String = "bonus"
        const val BODY:String = "details"
        const val IMAGE: String = "image"
        const val DATE: String = "posted"

        fun start(activity: Activity, title: String, details: String, postDate:String, image:String) {
            val intent = Intent(activity, ReadMoreActivity::class.java)
            intent.putExtra(TITLE, title)
            intent.putExtra(BODY, details)
            intent.putExtra(DATE, postDate)
            intent.putExtra(IMAGE, image)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
        }

    }

}
