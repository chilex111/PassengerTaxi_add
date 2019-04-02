package com.kross.taxi_passenger.presentation.screen.cabinet.wallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.cabinet.wallet.adapter.ListSuperClass
import com.kross.taxi_passenger.presentation.screen.cabinet.wallet.adapter.WXListAdapter
import com.kross.taxi_passenger.presentation.screen.payment.choose.ChoosePaymentActivity
import kotlinx.android.synthetic.main.activity_wallet.*

enum class BankEnum: ListSuperClass {

    AccessBank,
    DiamondBank,
    EcobankPlc,
    FidelityBankPlc,
    FirstBankofNigeriaPlc,
    FirstCityMonumentBank,
    GuarantyTrustBank,
    HeritageBankCompanyLimited,
    KeystoneBankLimited,
    ProvidusBank,
    SkyeBankPlc,
    StanbicIBTCBankPlc,
    StandardCharteredBank,
    SterlingBank,
    UBAPLC,
    UnionBankPlc,
    UnityBank,
    WemaBank,
    ZenithBankPlc;

    override var title: Int = 0
    override var details: Int = 0
    override var index: Int = ordinal

    override var titleString: String = "Access Bank"
    get() {
        when(this) {
            AccessBank -> return "Access Bank"
            DiamondBank -> return "Diamond Bank"
            EcobankPlc -> return "Ecobank Plc"
            FidelityBankPlc -> return "Fidelity Bank Plc"
            FirstBankofNigeriaPlc -> return "First Bank of Nigeria Plc"
            FirstCityMonumentBank -> return "First City Monument Bank"
            GuarantyTrustBank -> return "Guaranty Trust Bank"
            HeritageBankCompanyLimited -> return "Heritage Bank Company Limited"
            KeystoneBankLimited -> return "Keystone Bank Limited"
            ProvidusBank -> return "Providus Bank"
            SkyeBankPlc -> return "Skye Bank Plc"
            StanbicIBTCBankPlc -> return "Stanbic IBTC Bank Plc"
            StandardCharteredBank -> return "Standard Chartered Bank"
            SterlingBank -> return "Sterling Bank"
            UBAPLC -> return "UBA PLC"
            UnionBankPlc -> return "Union Bank Plc"
            UnityBank -> return "Unity Bank"
            WemaBank -> return "Wema Bank"
            ZenithBankPlc -> return "Zenith Bank Plc"
        }
    }

}

class BankListActivity: BaseActivity() {

    companion object {

        const val BANK_REQUEST_CODE: Int = 777
        const val BANK_NAME: String = ""

        fun start(activity: Activity) {
            val intent = Intent(activity, BankListActivity::class.java)
            activity.startActivityForResult(intent, BANK_REQUEST_CODE)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        initAppBar()
        initViews()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    // MARK: - Actions

    // MARK: - Helper methods

    private fun initAppBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
    }

    private fun initViews() {
        tableView.layoutManager = LinearLayoutManager(this)
        tableView.adapter = WXListAdapter(BankEnum.values() as Array<ListSuperClass>, this:: onTapListener)
    }

    fun onTapListener(index: Int) {
        Intent().apply {
            putExtra(BANK_NAME, BankEnum.values()[index].titleString)
            setResult(Activity.RESULT_OK, this)
        }
        finish()
    }

}