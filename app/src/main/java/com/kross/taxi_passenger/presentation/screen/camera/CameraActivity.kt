package com.kross.taxi_passenger.presentation.screen.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.fondesa.kpermissions.extension.onAccepted
import com.fondesa.kpermissions.extension.onDenied
import com.fondesa.kpermissions.extension.onPermanentlyDenied
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.domain.CameraViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.car_owner_registration.CarOwnerRegistrationActivity
import io.fotoapparat.Fotoapparat
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.layout_overlay_circle_photo_take.*
import kotlinx.android.synthetic.main.layout_overlay_circle_photo_take.view.*
import kotlinx.android.synthetic.main.layout_overlay_photo_ready.*
import kotlinx.android.synthetic.main.layout_overlay_photo_take.*
import kotlinx.android.synthetic.main.layout_photo_permission_denied.*
import org.jetbrains.anko.backgroundDrawable
import org.koin.android.viewmodel.ext.android.getViewModel
import java.io.File
import java.io.FileOutputStream

class CameraActivity : BaseActivity(), View.OnClickListener {

    private var fotoapparat: Fotoapparat? = null

    private var imageProcessDisposable: Disposable? = null

    private var frameHeightRaw = 0
    private var frameWidthRaw = 0

    private var photoPath: String? = null

    private lateinit var viewModel: CameraViewModel

    private var requestCodePhoto: Int? = null

    private var flagCircleShape = false

    private var degreesPhoto = 90f
    private var isCircle: Boolean = false

    private lateinit var messageRationalePermission: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        viewModel = getViewModel()
        val intent = intent;
        requestCodePhoto = intent.getIntExtra(getString(R.string.KEY_INTENT_REQUEST_CODE),0)
        setupLayoutDependsOnCode(requestCodePhoto)

        containerCameraActivity.post {
            val request = permissionsBuilder(Manifest.permission.CAMERA).build()
            request.onAccepted { initCamera(containerCameraActivity) }
                    .onDenied { showLayoutPermissionDenied() }
                    .onPermanentlyDenied { showLayoutPermissionDenied() }
            request.send()
        }
        setListenersView()
    }

    private fun showLayoutPermissionDenied() {
        ivFrameLayoutPermissionDenied.layoutParams.height = frameHeightRaw
        ivFrameLayoutPermissionDenied.layoutParams.width = frameWidthRaw
        if (flagCircleShape) ivFrameLayoutPermissionDenied.backgroundDrawable = resources.getDrawable(R.drawable.frame_circle_stroke)
        layoutCameraPermissionDenied.visibility = View.VISIBLE
        messageRationalePermission = getString(R.string.txt_camera_screen_setting_explanation)
    }

    private fun setListenersView() {
        icCamera.setOnClickListener(this)
        icCloseOverlayTakePhoto.setOnClickListener(this)
        icCloseOverlayCircleTakePhoto.setOnClickListener(this)
        iClosePhotoPermissionDenied.setOnClickListener(this)
        icBackOverlayPhotoReady.setOnClickListener(this)
        txtSaveCameraScreen.setOnClickListener(this)
        btbAllowPermissionCamera.setOnClickListener(this)
    }

    private fun setupLayoutDependsOnCode(requestCode: Int?) {
        when (requestCode) {
            resources.getInteger(R.integer.REQUEST_CODE_LICENSE_PHOTO) -> {
                frameHeightRaw = resources.getDimensionPixelOffset(R.dimen.photo_frame_height)
                frameWidthRaw = resources.getDimensionPixelOffset(R.dimen.photo_frame_width)
                overlayTake.visibility = View.GONE
                overlayTake.visibility = View.VISIBLE
            }
            resources.getInteger(R.integer.REQUEST_CODE_PASSPORT_PHOTO) -> {
                frameHeightRaw = resources.getDimensionPixelOffset(R.dimen.photo_passport_frame_height)
                frameWidthRaw = resources.getDimensionPixelOffset(R.dimen.photo_frame_width)
                overlayTake.visibility = View.GONE
                overlayTake.visibility = View.VISIBLE
            }
            resources.getInteger(R.integer.REQUEST_CODE_CAR_INSURANCE) -> {
                frameHeightRaw = resources.getDimensionPixelOffset(R.dimen.photo_car_insurance_height)
                frameWidthRaw = resources.getDimensionPixelOffset(R.dimen.photo_car_insurance_width)
                setupInsuranceLayout()
                overlayInshTake.txtCameraScreenTitle.text = getString(R.string.txt_camera_screen_profile_photo)
                textView.text = getString(R.string.txt_camera_screen_profile_photo)
            }
            resources.getInteger(R.integer.REQUEST_CODE_PROFILE_PHOTO) -> {
                degreesPhoto = 270f
                setupCircleLayout()
                overlayCircleTake.txtCameraScreenTitle.text = getString(R.string.txt_camera_screen_profile_photo)
                textView.text = getString(R.string.txt_camera_screen_profile_photo)
            } resources.getInteger(R.integer.REQUEST_CODE_CAR_PHOTO) -> {
                degreesPhoto = 270f
                setupCircleLayout()
                overlayCircleTake.txtCameraScreenTitle.text = getString(R.string.txt_camera_screen_profile_photo)
                textView.text = getString(R.string.txt_camera_screen_profile_photo)
            isCircle = true
            }
            resources.getInteger(R.integer.REQUEST_CODE_OWNERSHIP) -> {
                frameHeightRaw = resources.getDimensionPixelOffset(R.dimen.photo_frame_height)
                frameWidthRaw = resources.getDimensionPixelOffset(R.dimen.photo_frame_width)
                overlayTake.visibility = View.GONE
                overlayTake.visibility = View.VISIBLE
            }
        }
    }


    private fun setupCircleLayout() {
        flagCircleShape = true
        frameHeightRaw = resources.getDimensionPixelOffset(R.dimen.photo_frame_circle_height)
        frameWidthRaw = resources.getDimensionPixelOffset(R.dimen.photo_frame_circle_width)
        overlayTake.visibility = View.GONE
        overlayCircleTake.visibility = View.VISIBLE
    }


    private fun setupInsuranceLayout() {
        flagCircleShape = false
        overlayTake.visibility = View.GONE
        overlayInshTake.visibility = View.VISIBLE
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        ivReadyPhotoScreen.layoutParams.height = frameHeightRaw
        ivReadyPhotoScreen.layoutParams.width = frameWidthRaw
        setSizeViewDependsOnShape()
    }

    private fun setSizeViewDependsOnShape() {
        if (flagCircleShape) {
            frameCircle.layoutParams.height = frameHeightRaw
            frameCircle.layoutParams.width = frameWidthRaw
        } else {
            frame.layoutParams.height = frameHeightRaw
            frame.layoutParams.width = frameWidthRaw
        }
    }

    override fun onStart() {
        super.onStart()
        fotoapparat?.start()
    }

    override fun onStop() {
        super.onStop()
        imageProcessDisposable?.dispose()
        fotoapparat?.stop()
    }

    private fun initCamera(view: View) {
        val viewAspect = view.width.toFloat() / view.height.toFloat()

        when (requestCodePhoto) {
            resources.getInteger(R.integer.REQUEST_CODE_PROFILE_PHOTO) -> {
                fotoapparat = viewModel.initFrontCamera(cameraView, this, viewAspect)
            }
            else -> {
                fotoapparat = viewModel.initBackCamera(cameraView, this, viewAspect)
            }
        }
        fotoapparat?.start()
    }

    private fun takePicture() {
        val requiredFotoapparat = fotoapparat ?: return
        val requiredViewWidth = containerCameraActivity?.width ?: return
        val requiredContext = this

        cameraView.getPreview()

        requiredFotoapparat
                .takePicture()
                .toBitmap()
                .whenAvailable {
                    if (it == null || requiredViewWidth == 0) return@whenAvailable
                    processImage(requiredContext, requiredViewWidth, it.bitmap, it.rotationDegrees)
                }
    }

    private fun processImage(context: Context, width: Int, bitmap: Bitmap, rotatin: Int) {
        val file = viewModel.prepareFile(context, requestCodePhoto)

        fotoapparat?.stop()
        imageProcessDisposable = Single
                .fromCallable {
                    var transformedBitmap = viewModel.transform(width, bitmap, rotatin.toFloat(), frameWidthRaw, frameHeightRaw, isCircle)
                    if (flagCircleShape) transformedBitmap = viewModel.createCircularBitmap(transformedBitmap)

                    FileOutputStream(file).use { transformedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
                    transformedBitmap
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { progressBarCameraActivity.visibility = View.VISIBLE }
                .doOnSuccess { progressBarCameraActivity.visibility = View.GONE }
                .subscribe { bitmap ->
                    onPhotoReady(bitmap, file)
                }
    }

    private fun onPhotoReady(bitmap: Bitmap?, file: File) {
        photoPath = file.absolutePath
        ivReadyPhotoScreen.setImageBitmap(bitmap)
        overlayTake.visibility = View.GONE
        overlayCircleTake.visibility = View.GONE
        overlayInshTake.visibility = View.GONE
        overlayReady.visibility = View.VISIBLE
        icCamera.isEnabled = false
        icCamera.visibility = View.INVISIBLE
        viewCameraIcon.visibility = View.INVISIBLE
    }

    private fun prepareTakePhoto() {
        fotoapparat?.start()
        icCamera.isEnabled = true
        showOverlayDependsShape()
        overlayReady.visibility = View.GONE
        icCamera.visibility = View.VISIBLE
        viewCameraIcon.visibility = View.VISIBLE
    }

    private fun showOverlayDependsShape() {
        if (flagCircleShape) {
            overlayCircleTake.visibility = View.VISIBLE
        } else {
            overlayTake.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.icCamera -> {
                takePicture()
            }
            R.id.icCloseOverlayTakePhoto -> {
                onBackPressed()
            }
            R.id.icCloseOverlayCircleTakePhoto -> {
                onBackPressed()
            }
            R.id.iClosePhotoPermissionDenied -> {
                onBackPressed()
            }
            R.id.icBackOverlayPhotoReady -> {
                prepareTakePhoto()
            }
            R.id.txtSaveCameraScreen -> {
                sentResultPhotoPath()
            }
            R.id.btbAllowPermissionCamera -> {
            }
        }
    }

    private fun sentResultPhotoPath() {
        val intent = Intent(this, CarOwnerRegistrationActivity::class.java)
        intent.putExtra(getString(R.string.KEY_INTENT_PHOTO_PATH), photoPath)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PERMISSION_REQUEST -> {
                checkCameraPermission()
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            layoutCameraPermissionDenied.visibility = View.GONE
            initCamera(containerCameraActivity)
        }
    }

    companion object {
        fun start(activity: Activity, requestCode: Int) {
            val intent = Intent(activity, CameraActivity::class.java)
            activity.startActivityForResult(intent, requestCode)
        }
    }
}