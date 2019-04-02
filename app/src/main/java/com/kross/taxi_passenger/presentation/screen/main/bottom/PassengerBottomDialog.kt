package com.kross.taxi_passenger.presentation.screen.main.bottom

import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.utils.updateTypeface
import kotlinx.android.synthetic.main.bottom_dialog_passenger.*
import org.jetbrains.anko.enabled


class PassengerBottomDialog(view: ViewGroup, chosenItem: String?):  BottomDialog(view, R.layout.bottom_dialog_passenger)  {

    var closeListener: (() -> Unit)? = null
    var nextListener: (() -> Unit)? = null
    var txtSomeoneElseListener: (() -> Unit)? = null
    var txtMeListener: (() -> Unit)? = null

    init {
        btnNextPassengerNumber.enabled = true

        icBackPassengerChose.setOnClickListener { closeListener?.invoke() }
        btnNextPassengerNumber.setOnClickListener { nextListener?.invoke() }

        initView(chosenItem)

        icFacePurple.setOnClickListener{ txtMeListener?.invoke() }
        txtMe.setOnClickListener{ txtMeListener?.invoke() }
        icFaceWhite.setOnClickListener { txtSomeoneElseListener?.invoke() }
        txtSomeoneElse.setOnClickListener { txtSomeoneElseListener?.invoke() }

    }

    fun initView(chosenItem: String?){
        if(chosenItem == null){
            icFacePurple.isActivated = true
            txtMe.isActivated = true
            txtMe.updateTypeface(Typeface.BOLD)
            icFaceWhite.isActivated = false
            txtSomeoneElse.isActivated = false
            txtSomeoneElse.text = txtSomeoneElse.context.getString(R.string.txt_someone_else)
            txtSomeoneElse.updateTypeface(Typeface.NORMAL)
        } else {
            icFacePurple.isActivated = false
            txtMe.isActivated = false
            txtMe.updateTypeface(Typeface.NORMAL)
            icFaceWhite.isActivated = true
            txtSomeoneElse.text = chosenItem
            txtSomeoneElse.isActivated = true
            txtSomeoneElse.updateTypeface(Typeface.BOLD)
        }
        icFacePurple.requestLayout()
        icFaceWhite.requestLayout()
    }
}