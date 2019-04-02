package com.kross.taxi_passenger.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.ActivityManager
import android.app.Fragment
import android.app.FragmentManager
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.model.LatLng
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.screen.base.BaseFragment
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.connectivityManager
import org.jetbrains.anko.contentView
import java.math.BigInteger
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

inline fun SharedPreferences.edit(action: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    action(editor)
    editor.apply()
}

inline fun doOnRxAsync(millis: Long = 0, crossinline backgroundFun: () -> Unit) {
    Completable.create { backgroundFun.invoke() }
            .delay(millis, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .doOnError { t -> t.printStackTrace() }
            .subscribe()
}

fun Activity.hideKeyboardEx() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    currentFocus?.apply { imm.hideSoftInputFromWindow(windowToken, 0) }
}

fun Context.isNetworkAvailable(): Boolean {
    val info = connectivityManager.activeNetworkInfo
    return info != null && info.isConnected && !info.isRoaming
}

fun FragmentManager.replaceFragment(containerViewId: Int, fragment: Fragment, addToBackStack: Boolean, needAnimate: Boolean) {
    var ft = this.beginTransaction()
    val fragmentName = fragment.javaClass.simpleName
    if (addToBackStack) ft = ft.addToBackStack(fragmentName)
    if (needAnimate) ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.pop_out_right, R.animator.pop_in_left)
    ft.replace(containerViewId, fragment, fragmentName).commit()
}

fun Context.showToast(text: Any) = Toast.makeText(this, text.toString(), Toast.LENGTH_SHORT).show()
fun Activity.showToast(text: Any) = Toast.makeText(this, text.toString(), Toast.LENGTH_SHORT).show()
fun Activity.showSnack(text: String) = Snackbar.make(this.findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG).show()
fun View.snack(message: String, length: Int = Snackbar.LENGTH_SHORT) = Snackbar.make(this, message, length)
inline fun View.snack(message: String, length: Int = Snackbar.LENGTH_SHORT, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}

fun Snackbar.action(action: String, color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    color?.let { setActionTextColor(color) }
}

fun android.support.v4.app.Fragment.showSnackBarAction(context: Context, parent: View, message: String, action: String, listener: (View) -> Unit, length: Int = Snackbar.LENGTH_LONG): Snackbar {
    val color = ContextCompat.getColor(context, android.R.color.white)
    val snackbar = Snackbar.make(parent, message, length)
    val text = snackbar.view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
    text.setTextColor(color)
    snackbar.setAction(action, listener)
    val textAction = snackbar.view.findViewById<TextView>(android.support.design.R.id.snackbar_action)
    textAction.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
    snackbar.show()
    return snackbar
}

fun Activity.showSnackBarAction(message: String, action: String, listener: (View) -> Unit, length: Int = Snackbar.LENGTH_LONG): Snackbar {
    val color = ContextCompat.getColor(this, android.R.color.white)
    val snackbar = Snackbar.make(contentView!!, message, length)
    val text = snackbar.view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
    text.setTextColor(color)
    snackbar.setAction(action, listener)
    val textAction = snackbar.view.findViewById<TextView>(android.support.design.R.id.snackbar_action)
    textAction.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
    snackbar.show()
    return snackbar
}

inline fun <reified T> Intent.argOrThrow(arg: String): T {
    if (extras == null || !extras!!.containsKey(arg)) {
        throw IllegalStateException("Arg $arg is missing")
    }

    return extras!!.get(arg) as T
}

inline fun <reified T> Intent.getArgOrNull(arg: String): T? {
    if (extras == null || !extras!!.containsKey(arg)) {
        return null
    }

    return extras!!.get(arg) as T
}

fun BaseFragment.subscribeToConnectivityChanges(connectivityLiveData: LiveData<Boolean>?, listener: (Boolean) -> Unit) {
    connectivityLiveData?.observe(this, Observer {
        it?.let {
            listener.invoke(it)
        }
    })
}

fun GoogleApiClient.doOnConnect(action: () -> Unit) {
    if (isConnected) {
        action.invoke()
        return
    }

    registerConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
        override fun onConnectionSuspended(p0: Int) {
        }

        override fun onConnected(p0: Bundle?) {
            unregisterConnectionCallbacks(this)
            action.invoke()
        }
    })
}

fun EditText.onTextChanged(listener: ((String) -> Unit)) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            listener.invoke(p0.toString())
        }
    })
}


fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

fun TextInputEditText.textObserver(seconds: Long = 0): Observable<String> {

    val textChangeObservable = Observable.create<String> { emitter ->
        val textWatcher = object : TextWatcher {

            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                s?.toString()?.let { emitter.onNext(it) }
            }

        }

        addTextChangedListener(textWatcher)

        emitter.setCancellable {
            removeTextChangedListener(textWatcher)
        }
    }

    return if (seconds.toInt() == 0) textChangeObservable else textChangeObservable.debounce(seconds, TimeUnit.SECONDS)
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visible(animated: Boolean = false) {
    visibilityState(View.VISIBLE, animated)
}

fun View.visibilityState(state: Int, animate: Boolean = true) {
    if (animate) {
        animate()
                .alpha(if (state == View.GONE) 0f else 1f)
                .setDuration(500)
                .setListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        alpha = 1f
                        visibility = state
                    }

                })
    } else {
        alpha = if (state == View.GONE) 0f else 1f
        visibility = state
    }
}

fun View.isVisible() = visibility == View.VISIBLE

fun View.visibleOrGone(isVisible: Boolean) {
    if (isVisible) visible() else gone()
}

fun TextView.setTypeface(style: Int) {
    setTypeface(typeface, style)
}

fun TextView.updateTypeface(style: Int) {
    setTypeface(style)
}

fun Context.isServiceRunning(serviceClass: Class<*>): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}

fun Context.stopService(serviceClass: Class<*>) {
    stopService(Intent(this, serviceClass))
}

fun LatLng.angle(target: LatLng): Float {
    var bearingDegrees: Double = Math.toDegrees(Math.atan2(target.latitude - latitude, target.longitude - longitude))
    var roundDegrees = 360.0
    if (bearingDegrees < 0) {
        if (bearingDegrees > -90) {
            roundDegrees = 350.0
        }
        if (bearingDegrees < -90 && bearingDegrees > -180) {
            roundDegrees = 370.0
        }
        bearingDegrees += roundDegrees
        return if (360 - bearingDegrees > 360) (0 - bearingDegrees).toFloat() else (360 - bearingDegrees).toFloat()
    }
    roundDegrees = if (bearingDegrees < 90) 0.0 else 360.0
    if (bearingDegrees > 90 && bearingDegrees < 180) {
        roundDegrees = 370.0
    }
    return (roundDegrees - bearingDegrees).toFloat()
}
