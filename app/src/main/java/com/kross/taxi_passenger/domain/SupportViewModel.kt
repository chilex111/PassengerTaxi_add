package com.kross.taxi_passenger.domain

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.server.pojo.response.HelpList
import com.kross.taxi_passenger.data.repository.server.pojo.response.PhoneConfirm
import com.kross.taxi_passenger.domain.BaseViewModel
import com.kross.taxi_passenger.presentation.widget.SingleLiveEvent
import com.kross.taxi_passenger.utils.debugLog

class SupportViewModel(application: Application, private val repository: Repository) : BaseViewModel(application) {

    private val liveDataPhoneConfirm = MutableLiveData<PhoneConfirm>()
    private val liveDataFAQ = MutableLiveData<List<HelpList>>()
    private val liveDataError = MutableLiveData<String>()
    val progressLiveData = SingleLiveEvent<Boolean>()
    fun postHelp(contentType: String, apiKey: String, cacheControl: String, jsonObject: JsonObject){
        repository.phoneConfirm(contentType, apiKey, cacheControl, jsonObject).subscribe(
                {phoneConfirm: PhoneConfirm? ->
                    liveDataPhoneConfirm.value = phoneConfirm },
                {throwable: Throwable? ->
                    liveDataError.value = throwable?.message
                    debugLog(this, "Error - " + throwable?.message) })
    }

    fun getLiveDataError(): LiveData<String> = liveDataError

    @SuppressLint("CheckResult")
    fun getHelpList(apiKey: String, value: Int): MutableLiveData<List<HelpList>> {
        progressLiveData.value = true
        repository.getHelpList(apiKey,value)
                .subscribe({
                    liveDataFAQ.value = it
                    progressLiveData.value = false
                },
                        {
                            throwable: Throwable? -> throwable?.printStackTrace()
                        })

        return liveDataFAQ
    }
    fun createJsonObject(userType: Int, codeFacebook: String): JsonObject {
        val param = JsonObject()
        param.addProperty("title", userType)
        param.addProperty("text", codeFacebook)
        return param
    }
}