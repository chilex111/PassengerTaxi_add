package com.kross.taxi_passenger.presentation.screen.cabinet.wallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.gson.JsonObject
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.BankAccount
import com.kross.taxi_passenger.domain.CabinetViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.cabinet.wallet.BankListActivity.Companion.BANK_NAME
import com.kross.taxi_passenger.presentation.screen.cabinet.wallet.BankListActivity.Companion.BANK_REQUEST_CODE
import com.kross.taxi_passenger.utils.getStringPreference
import com.kross.taxi_passenger.utils.textObserver
import kotlinx.android.synthetic.main.activity_add_bank_account.*
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.koin.android.viewmodel.ext.android.viewModel

class AddBankAccountActivity: BaseActivity() {

    companion object {

        fun start(activity: Activity) {
            val intent = Intent(activity, AddBankAccountActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val viewModel: CabinetViewModel by viewModel()

    // MARK: - Life cycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bank_account)
        initAppBar()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_card_screen, menu)
        val saveItem = menu?.findItem(R.id.menuActionSaveAddCard)
        saveItem?.isEnabled = false
        bindButton(saveItem)
        addBankListener()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> { onBackPressed() }
            else -> {
                saveBankAccount()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                BANK_REQUEST_CODE -> firstEditText.setText(data.extras.get(BANK_NAME) as String)
            }
        }
    }

    // MARK: - Actions

    // MARK: - Helper methods

    private fun initAppBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)
    }

    private fun bindButton(item: MenuItem?) {
        val first = firstEditText.textObserver()
        val second = secondEditText.textObserver()
        val third = thirdEditText.textObserver()

        Observable.combineLatest<String, String, Boolean>(
                first,
                second,
                BiFunction {f, s -> f.count() > 5 && s.count() > 9 && s.count() < 20}
        ).apply {
            Observable.combineLatest<Boolean, String, Boolean>(
                    this,
                    third,
                    BiFunction {i, t -> i && "([A-Za-z]{2,})\\w+".toRegex().matches(t) }
            ).subscribe({
                item?.isEnabled = it
            }, {
                print("Not all fields filled")
            })
        }
    }

    private fun addBankListener() {
        fakeEditButton.setOnClickListener {
            BankListActivity.start(this@AddBankAccountActivity)
        }
        secondEditText.requestFocus()
    }

    private fun saveBankAccount() {
        val account = BankAccount(1,
                firstEditText.text.toString(),
                secondEditText.text.toString(),
                thirdEditText.text.toString())
        val token = getStringPreference(R.string.token)
        viewModel.addBankAccount(token, account) { success, message ->
            if (success) {
                finish()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

}