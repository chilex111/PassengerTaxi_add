package com.kross.taxi_passenger.presentation.screen.cabinet.wallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.BankAccount
import com.kross.taxi_passenger.domain.CabinetViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.cabinet.wallet.adapter.ListSuperClass
import com.kross.taxi_passenger.presentation.screen.cabinet.wallet.adapter.WXListAdapter
import com.kross.taxi_passenger.presentation.screen.cabinet.wallet.adapter.WalletEnum
import kotlinx.android.synthetic.main.activity_wallet.*
import org.jetbrains.anko.enabled
import org.jetbrains.anko.wrapContent
import org.koin.android.viewmodel.ext.android.viewModel

class WalletActivity: BaseActivity() {

    companion object {

        const val BONUS_BALANCE: String = "bonus"

        fun start(activity: Activity, balance: String) {
            val intent = Intent(activity, WalletActivity::class.java)
            intent.putExtra(BONUS_BALANCE, balance.split("\n").last())
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
        }

    }

    private val viewModel: CabinetViewModel by viewModel()
    private var account: BankAccount? = null
    private var canDraw: Boolean = false


    // MARK: - Life cycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        treatAccount()
        initAppBar()
        initViews()
    }

    override fun onResume() {
        super.onResume()
        treatAccount()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // MARK: - actions

    fun addBankAccount(v: View) {
        if (canDraw) TransferRequestActivity.start(this@WalletActivity, intent.getStringExtra(BONUS_BALANCE)) else AddBankAccountActivity.start(this@WalletActivity)
    }

    // MARK: - Helper methods

    private fun initAppBar() {
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initViews() {
        tableView.layoutManager = LinearLayoutManager(this)
        tableView.adapter = WXListAdapter(WalletEnum.values() as Array<ListSuperClass>, this::onTapListener)
    }

    fun onTapListener(index: Int) {
        TextDetailsActivity.start(this@WalletActivity, index)
    }

    private fun treatAccount() {
        account = viewModel.getBankAccount()
        val balance = intent.getStringExtra(BONUS_BALANCE)
        bonusesTextView.text = balance.replace(".", ",")
        if (account != null) {
            canDraw = true
            cardTextView.text = getString(R.string.txt_withdraw_bonuses)
            cardTextView.setTextColor(if (balance.toFloat().toInt() < 10000) resources.getColor(R.color.light_grey_blue) else resources.getColor(R.color.fab_bg))
            cardTextView.enabled = !(balance.toFloat().toInt() < 10000)
            accountTextView.text = account?.getShortNumber()
            if (balance.toFloat() < 0) {
                errorTextView.text = getString(R.string.txt_negative_balance)
            }
        }
        val params = middleFrame.layoutParams as ConstraintLayout.LayoutParams
        params.height = if (account == null) 1 else wrapContent
        middleFrame.requestLayout()
    }

}