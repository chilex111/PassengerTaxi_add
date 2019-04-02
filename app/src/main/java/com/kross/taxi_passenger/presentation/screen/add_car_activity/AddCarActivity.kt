package com.kross.taxi_passenger.presentation.screen.add_car_activity

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.request.Car
import com.kross.taxi_passenger.domain.AddCarViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.camera.CameraActivity
import com.kross.taxi_passenger.presentation.screen.choose_car_make_model.ChooseCarModelMakeActivity
import com.kross.taxi_passenger.utils.*
import kotlinx.android.synthetic.main.activity_add_car.*
import org.koin.android.viewmodel.ext.android.getViewModel


class AddCarActivity : BaseActivity(), View.OnClickListener {

    private val car = Car()
    private val plateNumberValidation = Validation(this)
    private lateinit var viewModel: AddCarViewModel


    companion object {

        private const val ADD_CAR: String = "add_car"

        fun start(activity: Activity, balance: String) {
            val intent = Intent(activity, AddCarActivity::class.java)
            intent.putExtra(ADD_CAR, balance.split("\n").last())
            activity.startActivity(intent)

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_car)
        initAppBar()
        viewModel = getViewModel()
        setUpClickListeners()

        plateNumberValidation.setMinLengthWithCustomHint(3)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addModelCar -> {
                val intent = Intent(this, ChooseCarModelMakeActivity::class.java)
                intent.putExtra(resources.getString(R.string.KEY_INTENT_CARE_MAKE), addMakeCar.text.toString())
                startActivityForResult(intent, resources.getInteger(R.integer.REQUEST_CODE_CHOOSE_CAR_MODEL))
                buttonEnableCheck()
            }
            R.id.addMakeCar -> {
                val intent = Intent(this, ChooseCarModelMakeActivity::class.java)
                startActivityForResult(intent, resources.getInteger(R.integer.REQUEST_CODE_CHOOSE_CAR_MAKE))
                buttonEnableCheck()
            }
            R.id.layoutCarPhotoAdd -> {
                val intent = Intent(this, CameraActivity::class.java)
                intent.putExtra(getString(R.string.KEY_INTENT_REQUEST_CODE), resources.getInteger(R.integer.REQUEST_CODE_CAR_PHOTO))
                startActivityForResult(intent, resources.getInteger(R.integer.REQUEST_CODE_CAR_PHOTO))
                buttonEnableCheck()
            }
            R.id.layoutOwnershipProof -> {
                val intent = Intent(this, CameraActivity::class.java)
                intent.putExtra(getString(R.string.KEY_INTENT_REQUEST_CODE), resources.getInteger(R.integer.REQUEST_CODE_OWNERSHIP))
                startActivityForResult(intent, resources.getInteger(R.integer.REQUEST_CODE_OWNERSHIP))
                buttonEnableCheck()
            }
            R.id.layoutInsuranceCar -> {
                val intent = Intent(this, CameraActivity::class.java)
                intent.putExtra(getString(R.string.KEY_INTENT_REQUEST_CODE), resources.getInteger(R.integer.REQUEST_CODE_CAR_INSURANCE))
                startActivityForResult(intent, resources.getInteger(R.integer.REQUEST_CODE_CAR_INSURANCE))
                buttonEnableCheck()
            }
            R.id.deletePhotoCar -> {
                carLayoutPhoto.visibility = View.GONE
                layoutCarPhotoAdd.visibility = View.VISIBLE
                buttonEnableCheck()
            }
            R.id.deletePhotoProof -> {
                proofLayoutOwnership.visibility = View.GONE
                layoutOwnershipProof.visibility = View.VISIBLE
                buttonEnableCheck()
            }
            R.id.deletePhotoInsurance -> {
                insuranceLayoutCar.visibility = View.GONE
                layoutInsuranceCar.visibility = View.VISIBLE
                buttonEnableCheck()
            }
            R.id.btnAddCar -> {

                viewModel.addCar(getString(R.string.CONTENT_TYPE), getStringPreference(R.string.token), car).observe(this, Observer {
                    if (it != null)
                        Toast.makeText(this, "Your car will be added soon", Toast.LENGTH_SHORT).show()
                    finish()
                    overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
                })
            }
        }
    }

    private fun initAppBar() {
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_new_car_title)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpClickListeners() {
        addModelCar.setOnClickListener(this)
        addMakeCar.setOnClickListener(this)
        addPhotoCarBtn.setOnClickListener(this)
        layoutCarPhotoAdd.setOnClickListener(this)
        layoutOwnershipProof.setOnClickListener(this)
        layoutInsuranceCar.setOnClickListener(this)
        addOwnershipProof.setOnClickListener(this)
        addInsuranceCarr.setOnClickListener(this)
        deletePhotoCar.setOnClickListener(this)
        deletePhotoProof.setOnClickListener(this)
        deletePhotoInsurance.setOnClickListener(this)
        setUpPlateNumberTextWather()
        btnAddCar.setOnClickListener(this)
    }

    private fun buttonEnableCheck() {
        btnAddCar.isEnabled = (((inputLayoutNumberPlate.isFieldValidWithoutError(plateNumberValidation))
                && (carLayoutPhoto.isVisible()) && (proofLayoutOwnership.isVisible()) && (insuranceLayoutCar.isVisible())))
    }


    private fun setUpPlateNumberTextWather() {
        editNumberPlate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                car.licensePlateNumber = p0.toString()
                buttonEnableCheck()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            actionForResultActivityOk(requestCode, data)
        }
        buttonEnableCheck()
    }

    private fun setCarMake(data: Intent) {
        val carMake = data.extras.getString(resources.getString(R.string.KEY_INTENT_CARE_MAKE_MODEL))
        car.make = carMake
        addMakeCar.text = carMake
    }

    private fun setCarModel(data: Intent) {
        val carMake = data.extras.getString(resources.getString(R.string.KEY_INTENT_CARE_MAKE_MODEL))
        addModelCar.text = carMake
        car.model = carMake
    }

    private fun setCarPhoto(data: Intent) {
        val photoPath = data.extras.getString(getString(R.string.KEY_INTENT_PHOTO_PATH))
        layoutCarPhotoAdd.visibility = View.GONE
        car.carPhoto = photoPath
        Glide.with(this)
                .load(photoPath)
                .apply(RequestOptions.circleCropTransform())
                .into(carImage1)
        carLayoutPhoto.visibility = View.VISIBLE
    }

    private fun setOwnershipProofPhoto(data: Intent) {
        val photoPath = data.extras.getString(getString(R.string.KEY_INTENT_PHOTO_PATH))
        layoutOwnershipProof.visibility = View.GONE
        Glide.with(this)
                .load( photoPath)
                .into(proofImage1)
        proofLayoutOwnership.visibility = View.VISIBLE
        car.proofOfOwnership = photoPath
    }

    private fun setIncurancePhoto(data: Intent) {
        val photoPath = data.extras.getString(getString(R.string.KEY_INTENT_PHOTO_PATH))
        layoutInsuranceCar.visibility = View.GONE
        Glide.with(this)
                .load( photoPath)
                .into(insuranceImage1)
        insuranceLayoutCar.visibility = View.VISIBLE
        car.insurance = photoPath
    }


    private fun actionForResultActivityOk(requestCode: Int, data: Intent) {
        when (requestCode) {
            resources.getInteger(R.integer.REQUEST_CODE_CHOOSE_CAR_MAKE) -> {
                setCarMake(data)
            }
            resources.getInteger(R.integer.REQUEST_CODE_CHOOSE_CAR_MODEL) -> {
                setCarModel(data)
            }
            resources.getInteger(R.integer.REQUEST_CODE_CAR_PHOTO) -> {
                setCarPhoto(data)
            }
            resources.getInteger(R.integer.REQUEST_CODE_OWNERSHIP) -> {
                setOwnershipProofPhoto(data)
            }
            resources.getInteger(R.integer.REQUEST_CODE_CAR_INSURANCE) -> {
                setIncurancePhoto(data)
            }
        }
        buttonEnableCheck()
    }


}
