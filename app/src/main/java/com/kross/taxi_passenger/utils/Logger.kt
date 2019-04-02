package com.kross.taxi_passenger.utils

import android.util.Log
import com.kross.taxi_passenger.BuildConfig

fun infoLog(anyClass: Any, any: Any) = whenDebug { Log.i(anyClass::class.java.simpleName, checkNotNull(any.toString())) }

fun errorLog(anyClass: Any, any: Any) = whenDebug { Log.e(anyClass::class.java.simpleName, checkNotNull(any.toString())) }

fun errorLog(anyClass: Any, exception: Exception) = whenDebug { Log.e(anyClass::class.java.simpleName, checkNotNull(exception.message)) }

fun errorLog(anyClass: Any, any: Any, tr: Throwable) = whenDebug { Log.e(anyClass::class.java.simpleName, checkNotNull(any.toString()), tr) }

fun debugLog(anyClass: Any, any: Any) = whenDebug { Log.d(anyClass::class.java.simpleName, checkNotNull(any.toString())) }

fun debugLog(anyClass: Any, any: Any, tr: Throwable) = whenDebug { Log.d(anyClass::class.java.simpleName, checkNotNull(any.toString()), tr) }

fun verboseLog(anyClass: Any, any: Any) = whenDebug { Log.v(anyClass::class.java.simpleName, checkNotNull(any.toString())) }

fun warnLog(anyClass: Any, any: Any) = whenDebug { Log.w(anyClass::class.java.simpleName, checkNotNull(any.toString())) }

private inline fun whenDebug(f: () -> Unit) {
    if (BuildConfig.DEBUG) f.invoke()
}

private fun checkNotNull(string: String): String = if (string.isEmpty()) "string for log is null" else string