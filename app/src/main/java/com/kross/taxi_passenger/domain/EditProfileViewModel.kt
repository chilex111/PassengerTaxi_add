package com.kross.taxi_passenger.domain

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.LiveData
import android.view.View
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.database.entity.PassengerEntity
import com.kross.taxi_passenger.data.repository.server.pojo.request.Authorization
import com.kross.taxi_passenger.data.repository.server.pojo.request.EmailVerifyBody
import com.kross.taxi_passenger.data.repository.server.pojo.request.OwnerBody
import com.kross.taxi_passenger.data.repository.server.pojo.request.Profile
import com.kross.taxi_passenger.data.repository.server.pojo.response.Token
import com.kross.taxi_passenger.presentation.screen.cabinet.edit_profile.MultipartBodyBuilderEdit
import com.kross.taxi_passenger.presentation.widget.SingleLiveEvent
import com.kross.taxi_passenger.utils.ErrorHelper
import com.kross.taxi_passenger.utils.debugLog
import com.kross.taxi_passenger.utils.errorLog
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.jetbrains.anko.getStackTraceString
import retrofit2.Response

class EditProfileViewModel (application: Application, private val repository: Repository) : BaseViewModel(application) {

    private val liveDateBecomeOwner = SingleLiveEvent<Boolean>()
    private val liveDateVerify = SingleLiveEvent<Boolean>()

    @SuppressLint("CheckResult")
    fun getPassenger(contentType: String, token: String, passengerId: Int): LiveData<PassengerEntity> {
        repository.getProfileInfo(contentType, token, passengerId).subscribe(
                { debugLog(this, "Obtain passenger entity") },
                { throwable: Throwable? -> errorLog(this, "Error - ${throwable?.getStackTraceString()}") })
        return repository.getPassenger(passengerId)
    }

    private val loadingViewModel = SingleLiveEvent<Int>()

    @SuppressLint("CheckResult")
    fun aditProfile(contentType: String, apiKey: String, body: Profile): LiveData<Boolean> {

        val multipartBody = createRequestBody(body)

        repository.profileEdit(contentType, apiKey, multipartBody)
                .doOnSubscribe { loadingViewModel.value = View.VISIBLE }
                .doAfterTerminate { loadingViewModel.value = View.GONE }
                .subscribe(
                        { response: Response<ResponseBody>? -> response?.let { liveDateBecomeOwner.value = it.isSuccessful } },
                        { throwable: Throwable? -> throwable?.printStackTrace() })
        return liveDateBecomeOwner
    }

    private fun createRequestBody(registration: Profile): List<MultipartBody.Part> {
        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        val multipartBuilder = MultipartBodyBuilderEdit.newBuilder(requestBodyBuilder)

        addToRequestProfile(registration, multipartBuilder)

        return requestBodyBuilder.build().parts()
    }

    private fun addToRequestProfile(registration: Profile, multipartBuilder: MultipartBodyBuilderEdit) {
        registration.let {
            multipartBuilder.setFirstName(it.firstName)
            multipartBuilder.setLastName(it.lastName)
            multipartBuilder.setEmail(it.email)
           multipartBuilder.setProfilePhoto(it.photo)
        }
    }

}