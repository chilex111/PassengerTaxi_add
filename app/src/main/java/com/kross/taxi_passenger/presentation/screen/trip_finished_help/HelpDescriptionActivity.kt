package com.kross.taxi_passenger.presentation.screen.trip_finished_help

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.view.Menu
import android.view.MenuItem
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.request.HelpRequest
import com.kross.taxi_passenger.domain.MapViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.utils.textObserver
import kotlinx.android.synthetic.main.activity_help_description.*
import org.koin.android.viewmodel.ext.android.viewModel

const val SCREEN_TITLE: String = "screen_title"
const val HELP_ID: String = "help_id"
const val HELP_TITLE: String = "help_title"

class HelpDescriptionActivity : BaseActivity() {
//    companion object {
//        fun  start(context: Context ,helpId: Int, helpTitle: String){
//            val intent = Intent(context, HelpDescriptionActivity::class.java)
//            intent.putExtra(HELP_ID, helpId)
//            intent.putExtra(HELP_TITLE, helpTitle)
//            context.startActivity(intent)
//        }
//    }

    private val viewModel: MapViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_description)
        initAppBar()
    }

    @SuppressLint("CheckResult")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_card_screen, menu)
        val saveItem = menu?.findItem(R.id.menuActionSaveAddCard)
        saveItem?.isEnabled = false
        textInput.filters = arrayOf(InputFilter.LengthFilter(140))

        HelpRequest(intent.getIntExtra(HELP_ID, 0), intent.getStringExtra(SCREEN_TITLE), textInput.text.toString())
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
               viewModel.addHelp(HelpRequest(1, intent.getStringExtra(SCREEN_TITLE), textInput.text.toString())){
                   finish()
               }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // MARK: - Actions

    // MARK: - Helper methods

    private fun initAppBar() {
        supportActionBar?.title = intent.getStringExtra(SCREEN_TITLE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)
    }

}