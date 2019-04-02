package com.kross.taxi_passenger.domain

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.LiveData
import android.view.View
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.database.entity.PassengerEntity
import com.kross.taxi_passenger.data.repository.server.pojo.request.OwnerBody
import com.kross.taxi_passenger.presentation.screen.car_owner_registration.MultipartBodyBuilder
import com.kross.taxi_passenger.presentation.widget.SingleLiveEvent
import com.kross.taxi_passenger.utils.debugLog
import com.kross.taxi_passenger.utils.errorLog
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.jetbrains.anko.getStackTraceString
import retrofit2.Response


class CarOwnerRegistrationViewModel(application: Application, private val repository: Repository) : BaseViewModel(application) {

    @SuppressLint("CheckResult")
    fun getPassenger(contentType: String, token: String, passengerId: Int): LiveData<PassengerEntity> {
        repository.getProfileInfo(contentType, token, passengerId).subscribe(
                { debugLog(this, "Obtain passenger entity") },
                { throwable: Throwable? -> errorLog(this, "Error - ${throwable?.getStackTraceString()}") })
        return repository.getPassenger(passengerId)
    }

    private val liveDateBecomeOwner = SingleLiveEvent<Boolean>()

    private val loadingViewModel = SingleLiveEvent<Int>()

    @SuppressLint("CheckResult")
    fun becomeCarOwner(contentType: String, apiKey: String, body: OwnerBody): LiveData<Boolean> {

        val multipartBody = createRequestBody(body)

        repository.becomeCarOwner(contentType, apiKey, multipartBody)
                .doOnSubscribe { loadingViewModel.value = View.VISIBLE }
                .doAfterTerminate { loadingViewModel.value = View.GONE }
                .subscribe(
                        { response: Response<ResponseBody>? -> response?.let { liveDateBecomeOwner.value = it.isSuccessful } },
                        { throwable: Throwable? -> throwable?.printStackTrace() })
        return liveDateBecomeOwner
    }

    private fun createRequestBody(registration: OwnerBody): List<MultipartBody.Part> {
        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        val multipartBuilder = MultipartBodyBuilder.newBuilder(requestBodyBuilder)


        addToRequestProfile(registration, multipartBuilder)
        addToRequestCar(registration, multipartBuilder)

        return requestBodyBuilder.build().parts()
    }


    private fun addToRequestCar(registration: OwnerBody, multipartBuilder: MultipartBodyBuilder) {
        registration.car?.let {
            multipartBuilder.setMake(it.make)
            multipartBuilder.setModel(it.model)
            multipartBuilder.setLicensePlateNumber(it.licensePlateNumber)
            multipartBuilder.setCarPhoto(it.carPhoto)
            multipartBuilder.setProofOfOwnership(it.proofOfOwnership)
            multipartBuilder.setInsurance(it.insurance)
        }
    }


    private fun addToRequestProfile(registration: OwnerBody, multipartBuilder: MultipartBodyBuilder) {
        registration.profile?.let {
            multipartBuilder.setFirstName(it.firstName)
            multipartBuilder.setLastName(it.lastName)
            multipartBuilder.setEmail(it.email)
            multipartBuilder.setProfilePhoto(it.photo)
        }
    }


}