package com.kross.taxi_passenger.presentation.screen.cabinet

import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.R.id.txtCabinetBecameCarowner
import com.kross.taxi_passenger.data.repository.database.entity.PassengerEntity
import com.kross.taxi_passenger.domain.CabinetViewModel
import com.kross.taxi_passenger.presentation.dialog.BottomDialog
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.become_car_owner.BecomeOwnerActivity
import com.kross.taxi_passenger.presentation.screen.cabinet.about.AboutAppActivity
import com.kross.taxi_passenger.presentation.screen.cabinet.edit_profile.personal_data.PersonalDataActivity
import com.kross.taxi_passenger.presentation.screen.cabinet.faq.FAQActivity
import com.kross.taxi_passenger.presentation.screen.cabinet.news.NewsActivity
import com.kross.taxi_passenger.presentation.screen.cabinet.support.SupportActivity
import com.kross.taxi_passenger.presentation.screen.cabinet.trips.MyTripsActivity
import com.kross.taxi_passenger.presentation.screen.cabinet.wallet.WalletActivity
import com.kross.taxi_passenger.presentation.screen.cars_list.CarsListActivity
import com.kross.taxi_passenger.presentation.screen.favorite.FavoriteActivity
import com.kross.taxi_passenger.presentation.screen.login.LoginActivity
import com.kross.taxi_passenger.presentation.screen.payment.choose.ChoosePaymentActivity
import com.kross.taxi_passenger.presentation.screen.trip_finished_help.HelpActivity
import com.kross.taxi_passenger.presentation.screen.verification_waiting.VerificationWaitingActivity
import com.kross.taxi_passenger.utils.clearPrefsProperty
import com.kross.taxi_passenger.utils.getIntPreference
import com.kross.taxi_passenger.utils.getStringPreference
import kotlinx.android.synthetic.main.activity_cabinet.*
import org.jetbrains.anko.startActivity
import org.koin.android.viewmodel.ext.android.viewModel


class CabinetActivity : BaseActivity(), View.OnClickListener, BottomDialog.OnClick {

    private val viewModel: CabinetViewModel by viewModel()
    private var status: Int = 2
    private var type: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cabinet)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setListenersView()
        fillBalance()
        getPassengerInfo()
        getWalletBalabce()
    }

    private fun setUpViews(passengerEntity: PassengerEntity?) {
        passengerEntity?.let {
            status = it.status
            type = it.type
            if (it.type == 2 && it.status == 2) {
                txtCabinetBecameCarowner.text = getString(R.string.txt_cabinet_my_cars)
            }
        }
    }

    private fun getPassengerInfo() {
        viewModel.getPassenger(getString(R.string.CONTENT_TYPE), getStringPreference(R.string.token), getIntPreference(R.string.user_id)).observe(this, Observer {
            fillProfileInfo(it)
            setUpViews(it)
        })
    }

    private fun fillProfileInfo(passengerEntity: PassengerEntity?) {
        txtCabinetName.text = "${passengerEntity?.firstName} ${passengerEntity?.lastName}"
        txtCabinetNumber.text = passengerEntity?.phoneNumber
        txtCabinetReferralLink.text = passengerEntity?.phoneNumber

        Glide.with(this).load(passengerEntity?.photo)
                .apply(RequestOptions.circleCropTransform().placeholder(R.drawable.photo_placeholder_personal_data).error(R.drawable.photo_placeholder_personal_data))
                .into(icCabinetPhoto)
    }

    private fun getWalletBalabce() {
        viewModel.getWallet(getStringPreference(R.string.token), getIntPreference(R.string.user_id)).observe(this, Observer {
            fillBalance(it?.total.toString())
        })
    }

    override fun onResume() {
        super.onResume()
        getPassengerInfo()
    }

    private fun setListenersView() {
        icGoToPersonalDataCabinet.setOnClickListener(this)
        txtCabinetPayment.setOnClickListener(this)
        txtCabinetPassword.setOnClickListener(this)
        txtCabinetMyTrip.setOnClickListener(this)
        txtCabinetMyFavorite.setOnClickListener(this)
        txtCabinetNews.setOnClickListener(this)
        icCabinetShare.setOnClickListener(this)
        txtCabinetBecameCarowner.setOnClickListener(this)
        txtCabinetAboutApp.setOnClickListener(this)
        txtCabinetSupport.setOnClickListener(this)
        txtCabinetFaQ.setOnClickListener(this)
        txtTabCabinetSignout.setOnClickListener(this)
        cardTabWallet.setOnClickListener(this)
    }

    fun fillBalance(totalBalance: String = "0") {
        txtTabWalletCardTitle.text = increaseFontSizeForPath(getString(R.string.txt_wallet_card_title), totalBalance, 1.7f)
    }

    fun increaseFontSizeForPath(text: String, path: String, increaseTime: Float): SpannableString {
        val spannable = SpannableString(text + path)
        val startIndexOfPath = spannable.toString().indexOf(path)
        spannable.setSpan(RelativeSizeSpan(increaseTime), startIndexOfPath,
                startIndexOfPath + path.length, 0)
        return spannable
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cabinet, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
                android.R.id.home -> {
                    onBackPressed()
                    overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
                }
        }
        return super.onOptionsItemSelected(item)
    }

    // TODO: - override navigation animation

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cardTabWallet -> {
                WalletActivity.start(this@CabinetActivity, txtTabWalletCardTitle.text.toString())
            }
            R.id.icGoToPersonalDataCabinet -> {
                startActivity<PersonalDataActivity>()
                overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
            }
            R.id.txtCabinetPayment -> {
                startActivity<ChoosePaymentActivity>()
                overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
            }
            R.id.txtCabinetPassword -> {
                startActivity<ChangePasswordActivity>()
                overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
            }
            R.id.txtCabinetMyTrip -> {
                startActivity<MyTripsActivity>()
                overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
            }
            R.id.txtCabinetMyFavorite -> {
                startActivity<FavoriteActivity>()
                overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
            }
            R.id.txtCabinetNews -> {//TODO worked on it 28-29/03/2019
                startActivity<NewsActivity>()
                overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
            }
            R.id.icCabinetShare -> {
                startActivity(viewModel.shareReferralLink(this, txtCabinetReferralLink.text.toString()))
            }
            R.id.txtCabinetBecameCarowner -> {
                if (type == 1) {
                    BecomeOwnerActivity.start(this@CabinetActivity)
                } else {
                    if (status == 2) {
                        CarsListActivity.start(this@CabinetActivity, "Cars List")
                    } else {
                        VerificationWaitingActivity.start(this@CabinetActivity, status)
                    }
                }
            }
           R.id.txtCabinetAboutApp -> {
               startActivity<AboutAppActivity>()//TODO worked on
               overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
           }
           R.id.txtCabinetSupport -> { startActivity<SupportActivity>()//TODO currently working on it
               overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
           }
           R.id.txtCabinetFaQ -> {startActivity<FAQActivity>() //TODO worked on
               overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
           }
           R.id.txtTabCabinetSignout -> { showBottomDialog() }
       }
    }

    private fun showBottomDialog() {
        val bottomSheetFragment = getString(R.string.txt_cabinet_sign_out_upper_case)?.let { BottomDialog.newInstance(it) }
        bottomSheetFragment?.let {
            it.setOnClickListener(this)
            it.show(supportFragmentManager, it.tag)
        }
    }

    override fun onClickItem(view: View, idRequest: Int?) {
        AlertDialog.Builder(this)
                .setMessage(getString(R.string.confirm_sign_out))
                .setNegativeButton(R.string.label_cancel) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.label_ok) { dialog, _ ->
                    dialog.dismiss()
                    progressBarTabCabinet.visibility = View.VISIBLE
                    clearPrefsProperty(getString(R.string.token))
                    LoginActivity.start(this)
                    finish()
                }
                .create()
                .show()
    }

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, CabinetActivity::class.java)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
        }
    }
}