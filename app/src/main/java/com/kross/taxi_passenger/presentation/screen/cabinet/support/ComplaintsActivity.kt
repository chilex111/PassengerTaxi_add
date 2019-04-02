package com.kross.taxi_passenger.presentation.screen.cabinet.support

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.request.HelpRequest
import com.kross.taxi_passenger.domain.MapViewModel
import com.kross.taxi_passenger.utils.showAlert
import com.kross.taxi_passenger.utils.showAlertFinish
import com.kross.taxi_passenger.utils.textObserver
import kotlinx.android.synthetic.main.activity_help_description.*
import org.koin.android.viewmodel.ext.android.viewModel


class ComplaintsActivity : AppCompatActivity() {
    private val viewModel: MapViewModel by viewModel()
    private var helpId: Long?= null
    private var helpTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_description)


        val extras = intent.extras
        if (extras != null) {
            helpId = extras.getLong(HELP_ID)
            helpTitle = extras.getString(HELP_TITLE)
        }
        initAppBar()

    }

    @SuppressLint("CheckResult")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_card_screen, menu)
        val saveItem = menu?.findItem(R.id.menuActionSaveAddCard)
        saveItem?.isEnabled = false
        textInput.filters = arrayOf(InputFilter.LengthFilter(140))

        HelpRequest(helpId!!.toInt(), helpTitle!!, textInput.text.toString())
        textInput.textObserver()
                .subscribe({
                    saveItem?.isEnabled = it.count() in 6..140
                }, {
                    print(it.stackTrace)
                })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            else -> {
                viewModel.addHelp(HelpRequest(helpId!!.toInt(),helpTitle!!, textInput.text.toString())){
                   showAlertFinish("Your complaint has been submit. \n Do not hesitate to share your thoughts and complaint to us.",this)

                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initAppBar() {
        supportActionBar?.title = helpTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)
    }

    companion object {
        const val HELP_TITLE: String = "help"
        const val HELP_ID: String = "Id"
        fun start(activity: Activity, id: Long?, title: String?){
            val intent = Intent(activity, ComplaintsActivity::class.java)
            intent.putExtra(HELP_TITLE, title)
            intent.putExtra(HELP_ID, id)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
        }
    }
}
