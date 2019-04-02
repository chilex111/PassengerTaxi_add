package com.kross.taxi_passenger.presentation.screen.main.bottom

import android.view.ViewGroup
import com.kross.taxi_passenger.R
import kotlinx.android.synthetic.main.bottom_dialog_search_driver.*

class SearchDriverBottomDialog(viewGroup: ViewGroup) : BottomDialog(viewGroup, R.layout.bottom_dialog_search_driver){

    var cancelListener: (() -> Unit)? = null

    init {
        btnCancelSearchDriver.setOnClickListener { cancelListener?.invoke() }
    }
}