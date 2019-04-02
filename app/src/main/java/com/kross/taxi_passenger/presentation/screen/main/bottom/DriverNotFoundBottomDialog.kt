package com.kross.taxi_passenger.presentation.screen.main.bottom

import android.view.ViewGroup
import com.kross.taxi_passenger.R
import kotlinx.android.synthetic.main.bottom_dialog_driver_not_found.*

class DriverNotFoundBottomDialog(view: ViewGroup): BottomDialog(view, R.layout.bottom_dialog_driver_not_found) {

    var cancelListener: (() -> Unit)? = null
    var tryOneMoreTimeListener: (() -> Unit)? = null

    init {
        txtCancelTripDriverNotFound.setOnClickListener { cancelListener?.invoke() }
        txtTryAgainDriverNotFound.setOnClickListener { tryOneMoreTimeListener?.invoke() }
    }
}