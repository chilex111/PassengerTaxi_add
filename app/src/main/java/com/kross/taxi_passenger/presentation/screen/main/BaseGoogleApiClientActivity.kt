package com.kross.taxi_passenger.presentation.screen.main

import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import com.fondesa.kpermissions.extension.onAccepted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import org.jetbrains.anko.toast

abstract class BaseGoogleApiClientActivity : BaseActivity(),
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    companion object {
        private val REQUEST_RESOLUTION = 61124
        private val STATE_IN_RESOLUTION = "inResolution"

        private val TAG_ERROR_DIALOG_FRAGMENT = "errorDialog"
    }

    private var playServices: GoogleApiClient? = null

    private var isResolvingPlayServicesError = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            isResolvingPlayServicesError = savedInstanceState.getBoolean(STATE_IN_RESOLUTION, false)
        }

        initPlayServices()
    }

    override fun onStart() {
        super.onStart()

        if (!isResolvingPlayServicesError) playServices?.connect()
    }

    override fun onStop() {
        playServices?.disconnect()

        super.onStop()
    }

    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int, data: Intent?) {
        isResolvingPlayServicesError = false

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun initPlayServices() {
        playServices = configureApiClientBuilder(GoogleApiClient.Builder(this))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        if (isResolvingPlayServicesError) return

        if (result.hasResolution()) {
            connectionFailedHasResolution(result)
        } else {
            connectionFailedNoResolution(result)
        }
    }

    private fun connectionFailedHasResolution(result: ConnectionResult) {
        try {
            isResolvingPlayServicesError = true
            result.startResolutionForResult(this, REQUEST_RESOLUTION)
        } catch (e: IntentSender.SendIntentException) {
            playServices?.connect()
        }
    }

    private fun connectionFailedNoResolution(result: ConnectionResult) {
        ErrorDialogFragment.newInstance(result.errorCode)
                .show(fragmentManager,
                        TAG_ERROR_DIALOG_FRAGMENT)
        isResolvingPlayServicesError = true
    }

    fun getPlayServices() = playServices

    abstract fun configureApiClientBuilder(b: GoogleApiClient.Builder): GoogleApiClient.Builder

    class ErrorDialogFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
            return GoogleApiAvailability
                    .getInstance()
                    .getErrorDialog(
                            activity,
                            arguments.getInt(ARG_ERROR_CODE),
                            REQUEST_RESOLUTION)
        }

        override fun onCancel(dlg: DialogInterface) {
            if (activity != null) {
                activity.finish()
            }

            super.onCancel(dlg)
        }

        override fun onDismiss(dlg: DialogInterface) {
            if (activity != null) {
                (activity as BaseGoogleApiClientActivity)
                        .isResolvingPlayServicesError = false
            }

            super.onDismiss(dlg)
        }

        companion object {
            internal val ARG_ERROR_CODE = "errorCode"

            internal fun newInstance(errorCode: Int): ErrorDialogFragment {
                val args = Bundle()
                val result = ErrorDialogFragment()

                args.putInt(ARG_ERROR_CODE, errorCode)
                result.arguments = args

                return result
            }
        }
    }

    override fun onConnectionSuspended(p0: Int) {}
}