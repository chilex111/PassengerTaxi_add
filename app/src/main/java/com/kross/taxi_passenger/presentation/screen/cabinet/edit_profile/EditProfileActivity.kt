package com.kross.taxi_passenger.presentation.screen.cabinet.edit_profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.PassengerEntity
import com.kross.taxi_passenger.data.repository.server.pojo.request.Profile
import com.kross.taxi_passenger.domain.EditProfileViewModel
import com.kross.taxi_passenger.presentation.interfaces.BaseTextWacher
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.camera.CameraActivity
import com.kross.taxi_passenger.utils.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class EditProfileActivity : BaseActivity(), View.OnClickListener, TextView.OnEditorActionListener {

    private val viewModel: EditProfileViewModel by viewModel()
    private val profile = Profile()
    private var type: Int = -1

    val emailValidation = Validation(this)
    val nameValidation = Validation(this)
    private var validKey = false
    private var profilePhotoKey = false
    private lateinit var menuGlobal: Menu

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            when (v?.id) {
                R.id.editName -> {
                    inputLayoutName.isErrorEnabled = !inputLayoutName.isFieldValidWithoutError(nameValidation) && editName.text.toString().isNotEmpty()
                    editName.clearFocus()
                }
                R.id.editLastName -> {
                    inputLayoutLastName.isErrorEnabled = !inputLayoutName.isFieldValidWithoutError(nameValidation) && editName.text.toString().isNotEmpty()
                    editName.clearFocus()
                }
                R.id.editEmail -> {
                    inputLayoutEmail.isErrorEnabled = !inputLayoutEmail.isFieldValidWithoutError(emailValidation) && editEmail.text.toString().isNotEmpty()
                    editEmail.clearFocus()
                }
            }
        }
        hideKeyboard()
        return false
    }

    private fun getPassengerInfo() {
        viewModel.getPassenger(getString(R.string.CONTENT_TYPE), getStringPreference(R.string.token), getIntPreference(R.string.user_id)).observe(this, android.arch.lifecycle.Observer {
            fillWiews(it)
        })
    }

    private fun editProfile() {
        profile.email = editEmail.text.toString()
        profile.firstName = editName.text.toString()
        profile.lastName = editLastName.text.toString()

        viewModel.aditProfile(getString(R.string.CONTENT_TYPE), getStringPreference(R.string.token), profile).observe(this, android.arch.lifecycle.Observer {
            if (it!!) {
                Toast.makeText(this, "Your data has been changed", Toast.LENGTH_SHORT).show()
                finish()
                overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)

            }
        })
    }

    private fun fillWiews(passengerEntity: PassengerEntity?) {
        editName.text = Editable.Factory.getInstance().newEditable(passengerEntity?.firstName.toString())
        profile.firstName = passengerEntity?.firstName.toString()
        editLastName.text = Editable.Factory.getInstance().newEditable(passengerEntity?.lastName.toString())
        profile.lastName = passengerEntity?.lastName.toString()
        editEmail.text = Editable.Factory.getInstance().newEditable(passengerEntity?.email.toString())
        editPhoneNumber1.text = Editable.Factory.getInstance().newEditable(passengerEntity?.phoneNumber.toString())
        profile.email = passengerEntity?.email.toString()
        type = passengerEntity?.type!!


        Glide.with(this)
                .asBitmap()
                .load(passengerEntity.photo)
                .apply(RequestOptions.circleCropTransform())
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        profile_photo_edit_e.setImageBitmap(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        Log.d("FAAAIL", "fail")
                    }
                })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.profile_photo_edit_e -> {
                val intent = Intent(this, CameraActivity::class.java)
                intent.putExtra(getString(R.string.KEY_INTENT_REQUEST_CODE), resources.getInteger(R.integer.REQUEST_CODE_PROFILE_PHOTO))
                startActivityForResult(intent, resources.getInteger(R.integer.REQUEST_CODE_PROFILE_PHOTO))
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_card_screen, menu)
        menuGlobal = menu!!
        return true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            actionForResultActivityOk(requestCode, data)
        }
    }

    private fun actionForResultActivityOk(requestCode: Int, data: Intent) {
        when (requestCode) {
            resources.getInteger(R.integer.REQUEST_CODE_PROFILE_PHOTO) -> {
                setProfilePhoto(data)
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.getItem(0).isEnabled = validKey
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
            }
            R.id.menuActionSaveAddCard -> {
                editProfile()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setProfilePhoto(data: Intent) {
        val photoPath = data.extras.getString(getString(R.string.KEY_INTENT_PHOTO_PATH))
        //    file_name.text = photoPath
        //  profile.photo = photoPath
        profile.photo = photoPath
        Glide.with(this)
                .load(BitmapHelper.getDownSampledBitmap(this, resources.getDimension(R.dimen.indentation_78).toInt(), resources.getDimension(R.dimen.indentation_78).toInt(), photoPath = photoPath))
                .apply(RequestOptions.circleCropTransform())
                .into(profile_photo_edit_e)
    }


    private fun setChangeListenerFirstName() {
        editName.addTextChangedListener(object : BaseTextWacher {
            override var timer: Timer? = Timer()
            override var handler: Handler = Handler(Looper.getMainLooper())
            override fun afterTextChangedDelay(text: Editable?) {
                buttonEnableCheck()
                text?.toString()?.let { inputLayoutName.isErrorEnabled = !inputLayoutName.isFieldValid(nameValidation) && editName.text.toString().isNotEmpty() }
            }
        })
    }

    private fun setChangeListenerLastName() {
        editLastName.addTextChangedListener(object : BaseTextWacher {
            override var timer: Timer? = Timer()
            override var handler: Handler = Handler(Looper.getMainLooper())
            override fun afterTextChangedDelay(text: Editable?) {
                text?.toString()?.let { inputLayoutLastName.isErrorEnabled = !inputLayoutLastName.isFieldValid(nameValidation) && editLastName.text.toString().isNotEmpty() }
                buttonEnableCheck()
            }
        })

    }

    private fun setChangeListenerEmail() {
        editEmail.addTextChangedListener(object : BaseTextWacher {
            override var timer: Timer? = Timer()
            override var handler: Handler = Handler(Looper.getMainLooper())
            override fun afterTextChangedDelay(text: Editable?) {
                profile.email = text.toString()
                text?.toString()?.let { inputLayoutEmail.isErrorEnabled = !inputLayoutEmail.isFieldValid(emailValidation) && editEmail.text.toString().isNotEmpty() }
                profilePhotoKey = inputLayoutEmail.isFieldValid(emailValidation)
                buttonEnableCheck()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        emailValidation.setMinLength(6).notEmpty().useEmailRegex().hasAtSymbol().hasDot().onlyLatin().canContainSpaces(false)
        nameValidation.setMinLengthField(2)
        setActionBar()
        setUpListeners()
        getPassengerInfo()
    }


    private fun setUpListeners() {
        profile_photo_edit_e.setOnClickListener(this)
        editName.setOnEditorActionListener(this)
        editLastName.setOnEditorActionListener(this)
        editEmail.setOnEditorActionListener(this)

        setChangeListenerFirstName()
        setChangeListenerLastName()
        setChangeListenerEmail()
    }

    fun buttonEnableCheck() {
        menuGlobal.findItem(R.id.menuActionSaveAddCard).isEnabled = ((inputLayoutName.isFieldValidWithoutError(nameValidation))
                && (inputLayoutLastName.isFieldValidWithoutError(nameValidation)
                && (inputLayoutEmail.isFieldValidWithoutError(emailValidation))))

    }

    private fun setActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.personal_data)
    }
}
