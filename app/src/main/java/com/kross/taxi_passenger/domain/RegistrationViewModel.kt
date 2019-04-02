package com.kross.taxi_passenger.domain

import android.app.Application
import android.arch.lifecycle.LiveData
import android.content.Context
import android.view.View
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.server.pojo.request.Registration
import com.kross.taxi_passenger.data.repository.server.pojo.response.UserId
import com.kross.taxi_passenger.presentation.widget.SingleLiveEvent
import com.kross.taxi_passenger.utils.ErrorHelper

class RegistrationViewModel(application: Application, private val repository: Repository) : BaseViewModel(application){

    private val loadingViewModel = SingleLiveEvent<Int>()

    private val errorLiveData = SingleLiveEvent<String>()

    private val tokenLiveDate = SingleLiveEvent<UserId>()

    fun registration(context: Context, apiKey: String, registration: Registration): LiveData<UserId>{
        repository.registration(apiKey, registration)
                .doOnSubscribe { loadingViewModel.value = View.VISIBLE }
                .doAfterTerminate { loadingViewModel.value = View.GONE }
                .subscribe(
                        { token: UserId? -> tokenLiveDate.value = token },
                        { throwable: Throwable? -> throwable?.let { errorLiveData.value = ErrorHelper.parseErrorAndGetString(context, it) } })
        return tokenLiveDate
    }

    fun getLoadingLiveData(): LiveData<Int> = loadingViewModel

    fun getErrorLiveData(): LiveData<String> = errorLiveData

}