package com.kross.taxi_passenger.presentation.interfaces

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import java.util.*

interface BaseTextWacher : TextWatcher{

    var timer: Timer?

    var handler: Handler

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        timer?.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Runnable {
                    afterTextChangedDelay(s)
                })
            }
        }, 700)
    }

    fun afterTextChangedDelay(text: Editable?)
}