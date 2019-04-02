package com.kross.taxi_passenger.domain

import android.app.Application
import android.arch.lifecycle.LiveData
import android.content.Context
import android.view.View
import com.google.gson.JsonObject
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.server.pojo.request.Authorization
import com.kross.taxi_passenger.data.repository.server.pojo.response.Token
import com.kross.taxi_passenger.presentation.widget.SingleLiveEvent
import com.kross.taxi_passenger.utils.ErrorHelper
import com.kross.taxi_passenger.utils.errorLog
import okhttp3.ResponseBody
import retrofit2.Response

class AuthorizationViewModel(application: Application, private val repository: Repository) : BaseViewModel(application){

    private val liveDateToken = SingleLiveEvent<Token>()

    private val liveDatePasswordRecovery = SingleLiveEvent<Boolean>()

    private val liveDataError = SingleLiveEvent<Int>()

    private val liveDataLoading = SingleLiveEvent<Int>()

    fun authorization(contentType: String, apiKey: String, authorization: Authorization){
        repository.authorization(contentType, apiKey, authorization)
                .doOnSubscribe { liveDataLoading.value = View.VISIBLE }
                .doAfterTerminate { liveDataLoading.value = View.GONE }
                .subscribe(
                        {token: Token? -> liveDateToken.value = token },
                        {throwable: Throwable? -> throwable?.let { liveDataError.value = ErrorHelper.getErrorCode(it) }})
    }

    fun passwordRecovery(context: Context, contentType: String, apiKey: String, jsonObject: JsonObject) : LiveData<Boolean> {
        repository.passwordRecovery(contentType, apiKey, jsonObject)
                .doOnSubscribe { liveDataLoading.value = View.VISIBLE }
                .doAfterTerminate { liveDataLoading.value = View.GONE }
                .subscribe(
                        { response: Response<ResponseBody>? ->  response?.let { liveDatePasswordRecovery.value = it.isSuccessful }},
                        { throwable: Throwable? ->  throwable?.let { errorLog(this, it.stackTrace) }})
        return liveDatePasswordRecovery
    }

    fun createJsonObject(userType: Int, phoneNumber: String): JsonObject {
        val param = JsonObject()
        param.addProperty("user_type", userType)
        param.addProperty("phone_number", phoneNumber)
        return param
    }

    fun getLiveDataToken(): LiveData<Token> = liveDateToken

    fun getLiveDataError(): LiveData<Int> = liveDataError

    fun getLiveDataLoading(): LiveData<Int> = liveDataLoading

}