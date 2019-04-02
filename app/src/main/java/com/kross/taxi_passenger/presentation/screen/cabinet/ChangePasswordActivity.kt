package com.kross.taxi_passenger.presentation.screen.cabinet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.gson.JsonObject
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.domain.CabinetViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.utils.getStringPreference
import com.kross.taxi_passenger.utils.md5
import com.kross.taxi_passenger.utils.textObserver
import kotlinx.android.synthetic.main.activity_change_password.*
import org.koin.android.viewmodel.ext.android.viewModel
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class ChangePasswordActivity: BaseActivity() {

    companion object {

        fun start(activity: Activity) {
            val intent = Intent(activity, ChangePasswordActivity::class.java)
            activity.startActivity(intent)
        }

    }

    private val viewModel: CabinetViewModel by viewModel()

    // MARK: - Life cycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_card_screen, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val menuItem = menu?.findItem(R.id.menuActionSaveAddCard)
        val first = passwordInput.textObserver()
        val second = secondPasswordInput.textObserver()
        menuItem?.isEnabled = false
        Observable
                .combineLatest<String, String, Boolean>(
                first,
                second,
                BiFunction {
                    u, p -> u.count() >= 6 && p.count() >= 6
                })
                .subscribe({
                    menuItem?.isEnabled = it
                }, {

                })
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> { onBackPressed()
                overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)}
            else -> {
                savePassword()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // MARK: - Actions

    // MARK: - Helper methods

    private fun savePassword() {
        hideKeyboard()
        var jsonObject = JsonObject()
        jsonObject.addProperty("password", secondPasswordInput.text.toString().md5())
        jsonObject.addProperty("password_old", passwordInput.text.toString().md5())
        viewModel.changePassword("application/json", getStringPreference(R.string.token), jsonObject) { first, second ->
            if (first) { finish() }
            else Toast.makeText(this, second ?: getString(R.string.txt_error_change_password) ,Toast.LENGTH_LONG).show()
        }
    }

}