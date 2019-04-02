package com.kross.taxi_passenger.domain

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.server.pojo.response.PhoneConfirm
import com.kross.taxi_passenger.domain.BaseViewModel
import com.kross.taxi_passenger.utils.debugLog

class LoginViewModel(application: Application, private val repository: Repository) : BaseViewModel(application) {

    private val liveDataPhoneConfirm = MutableLiveData<PhoneConfirm>()

    private val liveDataError = MutableLiveData<String>()

    fun phoneConfirm(contentType: String, apiKey: String, cacheControl: String, jsonObject: JsonObject){
        repository.phoneConfirm(contentType, apiKey, cacheControl, jsonObject).subscribe(
                {phoneConfirm: PhoneConfirm? ->
                    liveDataPhoneConfirm.value = phoneConfirm },
                {throwable: Throwable? ->
                    liveDataError.value = throwable?.message
                    debugLog(this, "Error - " + throwable?.message) })
    }

    fun getLiveDataError(): LiveData<String> = liveDataError

    fun getLiveDataPhoneConfirm(): LiveData<PhoneConfirm> = liveDataPhoneConfirm

    fun createJsonObject(userType: Int, codeFacebook: String): JsonObject {
        val param = JsonObject()
        param.addProperty("user_type", userType)
        param.addProperty("code", codeFacebook)
        return param
    }
}