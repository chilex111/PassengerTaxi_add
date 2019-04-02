package com.kross.taxi_passenger.presentation.screen.car_owner_registration

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.PassengerEntity
import com.kross.taxi_passenger.data.repository.server.pojo.request.Car
import com.kross.taxi_passenger.data.repository.server.pojo.request.OwnerBody
import com.kross.taxi_passenger.data.repository.server.pojo.request.Profile
import com.kross.taxi_passenger.domain.CarOwnerRegistrationViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.camera.CameraActivity
import com.kross.taxi_passenger.presentation.screen.choose_car_make_model.ChooseCarModelMakeActivity
import com.kross.taxi_passenger.utils.*
import kotlinx.android.synthetic.main.activity_car_owner_registration.*
import org.koin.android.viewmodel.ext.android.viewModel


class CarOwnerRegistrationActivity : BaseActivity(), View.OnClickListener {


    private val car = Car()
    private val profile = Profile()

    private var registration = OwnerBody()

    private val emailValidation = Validation(this)
    private val nameValidation = Validation(this)
    private val plateNumberValidation = Validation(this)
    private var isSuccess: Boolean = false
    private var profilePhotoKey: Boolean = false

    private val viewModel: CarOwnerRegistrationViewModel by viewModel()

    companion object {
        private const val REGIST_OWNER: String = "reg"

        fun start(activity: Activity, balance: String) {
            val intent = Intent(activity, CarOwnerRegistrationActivity::class.java)
            intent.putExtra(REGIST_OWNER, balance.split("\n").last())
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_owner_registration)
        initAppBar()
        getPassengerInfo()
        setUpClickListeners()



        emailValidation.setMinLength(6).notEmpty().useEmailRegex().hasAtSymbol().hasDot().onlyLatin().canContainSpaces(false)
        nameValidation.setMinLengthField(2)
        plateNumberValidation.setMinLengthWithCustomHint(3)

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addCarModel -> {
                val intent = Intent(this, ChooseCarModelMakeActivity::class.java)
                intent.putExtra(resources.getString(R.string.KEY_INTENT_CARE_MAKE), addCarMake.text.toString())
                startActivityForResult(intent, resources.getInteger(R.integer.REQUEST_CODE_CHOOSE_CAR_MODEL))
                buttonEnableCheck()
            }
            R.id.addCarMake -> {
                val intent = Intent(this, ChooseCarModelMakeActivity::class.java)
                startActivityForResult(intent, resources.getInteger(R.integer.REQUEST_CODE_CHOOSE_CAR_MAKE))
                buttonEnableCheck()
            }
            R.id.layoutAddCarPhoto -> {
                val intent = Intent(this, CameraActivity::class.java)
                intent.putExtra(getString(R.string.KEY_INTENT_REQUEST_CODE), resources.getInteger(R.integer.REQUEST_CODE_CAR_PHOTO))
                startActivityForResult(intent, resources.getInteger(R.integer.REQUEST_CODE_CAR_PHOTO))
                buttonEnableCheck()
            }
            R.id.layoutProofOwnership -> {
                val intent = Intent(this, CameraActivity::class.java)
                intent.putExtra(getString(R.string.KEY_INTENT_REQUEST_CODE), resources.getInteger(R.integer.REQUEST_CODE_OWNERSHIP))
                startActivityForResult(intent, resources.getInteger(R.integer.REQUEST_CODE_OWNERSHIP))
                buttonEnableCheck()
            }
            R.id.layoutInsurance -> {
                val intent = Intent(this, CameraActivity::class.java)
                intent.putExtra(getString(R.string.KEY_INTENT_REQUEST_CODE), resources.getInteger(R.integer.REQUEST_CODE_CAR_INSURANCE))
                startActivityForResult(intent, resources.getInteger(R.integer.REQUEST_CODE_CAR_INSURANCE))
                buttonEnableCheck()
            }
            R.id.deleteCarPhoto -> {
                carPhotoLayout.visibility = View.GONE
                layoutAddCarPhoto.visibility = View.VISIBLE
                buttonEnableCheck()
            }
            R.id.deleteProofPhoto -> {
                proofOwnershipLayout.visibility = View.GONE
                layoutProofOwnership.visibility = View.VISIBLE
                buttonEnableCheck()
            }
            R.id.deleteInsurancePhoto -> {
                insuranceLayout.visibility = View.GONE
                layoutInsurance.visibility = View.VISIBLE
                buttonEnableCheck()
            }
            R.id.btnApplyVerification -> {
                registration.profile = profile
                registration.car = car
                viewModel.becomeCarOwner(getString(R.string.CONTENT_TYPE), getStringPreference(R.string.token), registration).observe(this, Observer { isSuccess = it!! })
                if (isSuccess)
                    saveToSharedPreference(R.string.SATUS, 1)//  1 car owner
                finish()
            }
            R.id.profile_photo -> {
                val intent = Intent(this, CameraActivity::class.java)
                intent.putExtra(getString(R.string.KEY_INTENT_REQUEST_CODE), resources.getInteger(R.integer.REQUEST_CODE_PROFILE_PHOTO))
                startActivityForResult(intent, resources.getInteger(R.integer.REQUEST_CODE_PROFILE_PHOTO))
                buttonEnableCheck()
            }
        }
    }

    private fun setUpClickListeners() {
        addCarModel.setOnClickListener(this)
        addCarMake.setOnClickListener(this)
        addCarPhotoBtn.setOnClickListener(this)
        layoutAddCarPhoto.setOnClickListener(this)
        layoutProofOwnership.setOnClickListener(this)
        layoutInsurance.setOnClickListener(this)
        addProofOwnership.setOnClickListener(this)
        addInsurance.setOnClickListener(this)
        deleteCarPhoto.setOnClickListener(this)
        deleteProofPhoto.setOnClickListener(this)
        deleteInsurancePhoto.setOnClickListener(this)
        setUpPlateNumberTextWather()
        btnApplyVerification.setOnClickListener(this)
        profile_photo.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun buttonEnableCheck() {
        btnApplyVerification.isEnabled = ((inputLayoutOwnerName.isFieldValidWithoutError(nameValidation))
                && (inputLayoutOwnerLastName.isFieldValidWithoutError(nameValidation)
                && (inputOwnerLayoutEmail.isFieldValidWithoutError(emailValidation))
                && (inputLayoutPlateNumber.isFieldValidWithoutError(plateNumberValidation))
                && (carPhotoLayout.isVisible()) && (proofOwnershipLayout.isVisible()) && (insuranceLayout.isVisible()) && profilePhotoKey))
    }


    private fun setUpPlateNumberTextWather() {
        editPlateNumber.addTextChangedListener(object : TextWatcher {
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
        addCarMake.text = carMake
    }

    private fun setCarModel(data: Intent) {
        val carMake = data.extras.getString(resources.getString(R.string.KEY_INTENT_CARE_MAKE_MODEL))
        addCarModel.text = carMake
        car.model = carMake
    }

    private fun setCarPhoto(data: Intent) {
        val photoPath = data.extras.getString(getString(R.string.KEY_INTENT_PHOTO_PATH))
        layoutAddCarPhoto.visibility = View.GONE
     //   file_name.text = photoPath
        Log.d("PHOTO_PATH", photoPath)
        car.carPhoto = photoPath
//        Glide.with(this)
//                .load(BitmapHelper.getDownSampledBitmap(this, resources.getDimension(R.dimen.indentation_78).toInt(), resources.getDimension(R.dimen.indentation_78).toInt(), photoPath = photoPath))
//                .apply(RequestOptions.circleCropTransform())
//                .into(carPhoto1)
        carPhotoLayout.visibility = View.VISIBLE
    }

    private fun setProfilePhoto(data: Intent) {
        val photoPath = data.extras.getString(getString(R.string.KEY_INTENT_PHOTO_PATH))
    //    file_name.text = photoPath
        profile.photo = photoPath
        Glide.with(this)
                .load(BitmapHelper.getDownSampledBitmap(this, resources.getDimension(R.dimen.indentation_78).toInt(), resources.getDimension(R.dimen.indentation_78).toInt(), photoPath = photoPath))
                .apply(RequestOptions.circleCropTransform())
                .into(profile_photo)
        profilePhotoKey = true
    }

    private fun setOwnershipProofPhoto(data: Intent) {
        val photoPath = data.extras.getString(getString(R.string.KEY_INTENT_PHOTO_PATH))
        layoutProofOwnership.visibility = View.GONE
     //   proof_file_name.text = photoPath
        Glide.with(this)
                .load(BitmapHelper.getDownSampledBitmap(this, resources.getDimension(R.dimen.indentation_78).toInt(), resources.getDimension(R.dimen.indentation_78).toInt(), photoPath = photoPath))
                .into(proofImage)
        proofOwnershipLayout.visibility = View.VISIBLE
        car.proofOfOwnership = photoPath
    }

    private fun setIncurancePhoto(data: Intent) {
        val photoPath = data.extras.getString(getString(R.string.KEY_INTENT_PHOTO_PATH))
        layoutInsurance.visibility = View.GONE
     //   insurance_file_name.text = photoPath
        Glide.with(this)
                .load(BitmapHelper.getDownSampledBitmap(this, resources.getDimension(R.dimen.indentation_78).toInt(), resources.getDimension(R.dimen.indentation_78).toInt(), photoPath = photoPath))
                .into(insuranceImage)
        insuranceLayout.visibility = View.VISIBLE
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
            resources.getInteger(R.integer.REQUEST_CODE_PROFILE_PHOTO) -> {
                setProfilePhoto(data)
            }
        }
        buttonEnableCheck()
    }


    private fun getPassengerInfo() {
        viewModel.getPassenger(getString(R.string.CONTENT_TYPE), getStringPreference(R.string.token), getIntPreference(R.string.user_id)).observe(this, Observer {
            fillWiews(it)
        })
    }

    private fun initAppBar() {
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.become)
    }

    @SuppressLint("ResourceType")
    private fun fillWiews(passengerEntity: PassengerEntity?) {
        editOwnerRegistrationName.text = Editable.Factory.getInstance().newEditable(passengerEntity?.firstName.toString())
        profile.firstName = passengerEntity?.firstName.toString()
        editOwnerRegistrationLastName.text = Editable.Factory.getInstance().newEditable(passengerEntity?.lastName.toString())
        profile.lastName = passengerEntity?.lastName.toString()
        editOwnerRegistrationEmail.text = Editable.Factory.getInstance().newEditable(passengerEntity?.email.toString())
        profile.email = passengerEntity?.email.toString()
        editOwnerNumber.text = Editable.Factory.getInstance().newEditable(passengerEntity?.phoneNumber.toString())
        if (!passengerEntity?.emailVerified!!) {
            mailConfirmBtn.visibility = View.GONE
        }

        Glide.with(this)
                .asBitmap()
                .load(passengerEntity.photo)
                .apply(RequestOptions.circleCropTransform())
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        profile_photo.setImageBitmap(resource)
                        profile.photo = passengerEntity.photo!!
                        profilePhotoKey = true
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        Log.d("FAAAIL", "fail")
                    }
                })

//        val inputStream = resources.openRawResource(R.drawable)
//        val tempFile = File.createTempFile("pre", "suf")
    }

}
