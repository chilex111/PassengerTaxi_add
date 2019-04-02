package com.kross.taxi_passenger.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import com.kross.taxi_passenger.R


fun Context.showAlert(paramString: String) {
    val localBuilder = AlertDialog.Builder(this)
    localBuilder.setMessage(paramString)
    localBuilder.setNeutralButton(R.string.ok) { _, _ -> }
    localBuilder.create().show()
}
fun Context.showAlertFinish(paramString: String,context: Activity) {
    val localBuilder = AlertDialog.Builder(this)
    localBuilder.setMessage(paramString)
    localBuilder.setNeutralButton(R.string.ok) { _, _ ->
        context.finish()
    }
    localBuilder.create().show()
}

fun Context.getIntPreference(@StringRes key: Int): Int {
    val prefs = getPrefs(this)
    return prefs.getInt(getString(key), -1)
}

fun Context.getStringPreference(@StringRes key: Int): String {
    val value = getString(key)
    val prefs = getPrefs(this)
    return prefs.getString(value, "")
}

fun Context.getStringPreferenceSocketToken(@StringRes key: Int): String{
    val token = getStringPreference(key)
    return token.replace("Bearer ", "")
}

fun Context.getBooleanPreference(@StringRes key: Int, defaultValue: Boolean = false): Boolean {
    val prefs = getPrefs(this)
    return prefs.getBoolean(getString(key), defaultValue)
}

fun Context.getLongPreference(@StringRes key: Int): Long {
    val prefs = getPrefs(this)
    return prefs.getLong(getString(key), 0)
}

fun Context.clearPrefsProperty(key: String) {
    getPrefs(this).edit().remove(key).apply()
}

private fun getPrefs(context: Context): SharedPreferences {
    val prefsName = context.getString(R.string.preference_file)
    return context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
}

fun Context.containsPref(key: String): Boolean = getPrefs(this).contains(key)

fun Context.saveToSharedPreference(@StringRes res: Int, value: Any) {
    val editor = getPrefs(this).edit()
    val key = getString(res)
    when (value) {
        is String -> editor.putString(key, value).apply()
        is Int -> editor.putInt(key, value).apply()
        is Boolean -> editor.putBoolean(key, value).apply()
        is Long -> editor.putLong(key, value).apply()
        else -> throw IllegalArgumentException("Not supported value set into preferences")
    }
}