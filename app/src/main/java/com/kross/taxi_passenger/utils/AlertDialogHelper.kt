package com.kross.taxi_passenger.utils

import android.content.Context
import android.provider.Settings.Global.getString
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import com.kross.taxi_passenger.R
import io.reactivex.functions.Action


object AlertDialogHelper {

    fun createDialog(context: Context,
                      title: Int?,
                      description: Int,
                     negativeButtonText: String? = null,
                     positiveButtonText: String? = null,
                     onPositiveClick: Action? = null,
                     cancellableFlag: Boolean = true) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogStyle)
                .setCancelable(cancellableFlag)
                .setMessage(context.resources.getString(description))


        title?.let { builder.setTitle(context.resources.getString(it)) }
        positiveButtonText?.let { builder.setPositiveButton(positiveButtonText) { dialog, which -> onPositiveButtonPressed(onPositiveClick)} }

        negativeButtonText?.let {  builder.setNegativeButton(negativeButtonText, null) }

        val alert = builder.create()
        alert.show()
    }

    private fun onPositiveButtonPressed(onPositiveClick: Action?) {
        onPositiveClick?.let { try {
            onPositiveClick.run()
        }catch (e: Exception){
            e.printStackTrace()
        } }
    }

    fun createDialog(context: Context, title: Int, description: Int, negativeButtonText: String, positiveButtonText: String) {
        createDialog(context, title, description,  negativeButtonText, positiveButtonText, null)
    }
}