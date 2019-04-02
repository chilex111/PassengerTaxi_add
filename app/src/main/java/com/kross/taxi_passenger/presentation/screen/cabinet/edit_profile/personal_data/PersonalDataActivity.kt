package com.kross.taxi_passenger.presentation.screen.cabinet.edit_profile.personal_data

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.PassengerEntity
import com.kross.taxi_passenger.data.repository.server.pojo.request.EmailVerifyBody
import com.kross.taxi_passenger.domain.CabinetViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.cabinet.edit_profile.EditProfileActivity
import com.kross.taxi_passenger.utils.AlertDialogHelper
import com.kross.taxi_passenger.utils.getIntPreference
import com.kross.taxi_passenger.utils.getStringPreference
import kotlinx.android.synthetic.main.activity_choose_car_model_make.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_personal_data.*
import org.jetbrains.anko.startActivity
import org.koin.android.viewmodel.ext.android.viewModel

class PersonalDataActivity : BaseActivity(), View.OnClickListener {

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mailConfirmEditBtn -> {
                viewModel.verifyEmail(getString(R.string.CONTENT_TYPE), getStringPreference(R.string.API_KEY), EmailVerifyBody(1,  user_mail_personal.text.toString())).observe(this, android.arch.lifecycle.Observer {
                    if (it!!) {
                        showDialogEmail()
                        mailConfirmEditBtn.isEnabled = false
                    }
                })
            }
        }
    }

    private fun showDialogEmail() {
        AlertDialogHelper.createDialog(this,
                R.string.emsil,
                R.string.dfd,
                null,
                "Ok")
    }

    private val viewModel: CabinetViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_data)
        setActionBar()
        getPassengerInfo()
        mailConfirmEditBtn.setOnClickListener(this)

        viewModel.getLiveDataLoading().observe(this, Observer {
            it?.let { progressBarMail.visibility = it }
        })
        //    vi
    }



    private fun setActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.personal_data)
    }

    private fun getPassengerInfo() {
        viewModel.getPassenger(getString(R.string.CONTENT_TYPE), getStringPreference(R.string.token), getIntPreference(R.string.user_id)).observe(this, Observer {
            fillProfileInfo(it)
        })
    }

    override fun onResume() {
        super.onResume()
        getPassengerInfo()
    }

    private fun fillProfileInfo(passengerEntity: PassengerEntity?) {

        user_name_personal.text = "${passengerEntity?.firstName} ${passengerEntity?.lastName}"
        user_phone_personal.text = passengerEntity?.phoneNumber
        user_mail_personal.text = passengerEntity?.email

        if (passengerEntity?.emailVerified!!) {
            mailConfirmEditBtn.visibility = View.GONE
        }

        Glide.with(this).load(passengerEntity?.photo)
                .apply(RequestOptions.circleCropTransform().placeholder(R.drawable.photo_placeholder_personal_data).error(R.drawable.photo_placeholder_personal_data))
                .into(profile_photo_edit)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.personal_data_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
            }
            R.id.action_edit -> {
                startActivity<EditProfileActivity>()
                overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_exit)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
