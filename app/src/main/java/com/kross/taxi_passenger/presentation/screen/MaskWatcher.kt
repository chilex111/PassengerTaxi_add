package com.kross.taxi_passenger.presentation.screen

import android.text.Editable
import android.text.TextWatcher


const val MASK_EDIT_NUMBER = 0
const val MASK_EDIT_DATE = 1

class MaskWatcher(val mask: Int) : TextWatcher {
    private var isRunning = false
    private var isDeleting = false

    override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {
        isDeleting = count > after
    }

    override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(editable: Editable) {
        if (isRunning || isDeleting) {
            return
        }
        isRunning = true

       when(mask){
           MASK_EDIT_NUMBER -> maskNumber(editable)
           MASK_EDIT_DATE -> maskDate(editable)
       }
        isRunning = false
    }

    private fun maskNumber(editable: Editable){
        if (editable.length == 4 || editable.length == 9 || editable.length == 14) {
            editable.append(" ")
        }
    }

    private fun maskDate(editable: Editable){
        if(editable.length == 2){
            editable.append("/")
        }
    }
}