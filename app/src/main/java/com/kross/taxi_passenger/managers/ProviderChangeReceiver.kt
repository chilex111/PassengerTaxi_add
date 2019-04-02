package com.kross.taxi_passenger.managers

import android.arch.lifecycle.MutableLiveData
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager

const val PROVIDER_CHANGED_ACTION = "android.location.PROVIDERS_CHANGED"

class ProviderChangeReceiver : BroadcastReceiver() {

    class ProviderChangeAction

    val providerChangeLiveData: MutableLiveData<ProviderChangeAction> = MutableLiveData()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.matches(Regex.fromLiteral(LocationManager.PROVIDERS_CHANGED_ACTION))) {
            providerChangeLiveData.value = ProviderChangeAction()
        }
    }
}