package com.kross.taxi_passenger.managers

import android.arch.lifecycle.MutableLiveData
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class NetworkChangeReceiver : BroadcastReceiver() {

    val connectivityLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun updateConnectivityState(context: Context) : Boolean {
        isNetworkAvailable(context)
                .let {
                    connectivityLiveData.value = isNetworkAvailable(context)
                    return it
                }
    }

    override fun onReceive(context: Context, intent: Intent) {
        connectivityLiveData.value = isNetworkAvailable(context)
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}