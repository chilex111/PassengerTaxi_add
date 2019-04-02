package com.kross.taxi_passenger.presentation.screen.base

import android.app.ActionBar
import android.content.Intent
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.utils.hideKeyboardEx
import com.kross.taxi_passenger.utils.illegalState
import com.kross.taxi_passenger.utils.showSnack
import com.kross.taxi_passenger.utils.showToast

abstract class BaseFragment : Fragment() {

    protected val appBar: ActionBar? = activity?.actionBar

    protected fun disableHomeAsUp() = appBar?.setDisplayHomeAsUpEnabled(false)

    protected fun initializeNavigationBar(title: String, showBackButton: Boolean, @DrawableRes resId: Int? = null) {
        appBar?.apply {
            setTitle(title)
            this.setDisplayHomeAsUpEnabled(showBackButton)
            resId?.let { setHomeAsUpIndicator(it) }
            this.elevation = 4f
        }
    }

    fun showAlertDialog(dialogBuilder: AlertDialog.Builder.() -> Unit) {
        val builder = activity?.let { AlertDialog.Builder(it)}
        builder?.dialogBuilder()
        val dialog = builder?.create()

        dialog?.show()
    }

    fun AlertDialog.Builder.positiveButton(text: String = getString(R.string.ok_alert_btn), handleClick: (which: Int) -> Unit = {}) {
        this.setPositiveButton(text, { dialogInterface, which-> handleClick(which) })
    }

    fun AlertDialog.Builder.negativeButton(text: String = getString(R.string.cancel_alert_btn), handleClick: (which: Int) -> Unit = {}) {
        this.setNegativeButton(text, { dialogInterface, which-> handleClick(which) })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> fragmentManager?.popBackStackImmediate()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(getLayout(), container, false)
        return view
    }

    fun getColor(@ColorRes colorRes: Int) = context?.let { ContextCompat.getColor(it, colorRes) }

    abstract fun getLayout(): Int

    protected fun showToast(text: String) = activity?.showToast(text)

    protected fun showSnack(text: String) = activity?.showSnack(text)

    protected fun hideKeyboard() = activity?.hideKeyboardEx()

    inline fun <reified T> argOrThrow(arg: String): T =
        argOrNull<T>(arg) ?: illegalState("Arg $arg is missing")

    inline fun <reified T> argOrNull(arg: String): T? = arguments?.get(arg) as? T

    fun getSupportActionBar() = (activity as? AppCompatActivity)?.supportActionBar
}
