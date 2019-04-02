package com.kross.taxi_passenger.domain

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.LiveData
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.database.entity.CarEntity
import com.kross.taxi_passenger.data.repository.server.pojo.request.Car
import com.kross.taxi_passenger.data.repository.server.pojo.request.OwnerBody
import com.kross.taxi_passenger.data.repository.server.pojo.response.AddCarResponce
import com.kross.taxi_passenger.presentation.screen.add_car_activity.AddCarMultipartBodyBuilder
import com.kross.taxi_passenger.presentation.screen.car_owner_registration.MultipartBodyBuilder
import com.kross.taxi_passenger.presentation.widget.SingleLiveEvent
import com.kross.taxi_passenger.utils.getStringPreference
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

class AddCarViewModel(application: Application, private val repository: Repository) : BaseViewModel(application) {
    private val liveDataAddCar= SingleLiveEvent<AddCarResponce>()

    private val loadingViewModel = SingleLiveEvent<Int>()

    @SuppressLint("CheckResult")
    fun addCar(contentType: String, apiKey: String, car: Car): LiveData<AddCarResponce> {

        val multipartBody = createRequestBody(car)

        repository.addCar(contentType, apiKey, multipartBody)
                .doOnSubscribe { loadingViewModel.value = View.VISIBLE }
                .doAfterTerminate { loadingViewModel.value = View.GONE }
                .subscribe(
                        {liveDataAddCar.value = it}, //getCars(apiKey) },
                        { throwable: Throwable? -> throwable?.printStackTrace() })
        return liveDataAddCar
    }

    private fun createRequestBody(registration: Car): List<MultipartBody.Part> {
        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        val multipartBuilder =  AddCarMultipartBodyBuilder.newBuilder(requestBodyBuilder)
        addToRequestCar(registration, multipartBuilder)
        return requestBodyBuilder.build().parts()
    }


    private fun addToRequestCar(registration: Car, multipartBuilder:  AddCarMultipartBodyBuilder) {
        registration.let {
            multipartBuilder.setMake(it.make)
            multipartBuilder.setModel(it.model)
            multipartBuilder.setLicensePlateNumber(it.licensePlateNumber)
            multipartBuilder.setCarPhoto(it.carPhoto)
            multipartBuilder.setProofOfOwnership(it.proofOfOwnership)
            multipartBuilder.setInsurance(it.insurance)
        }
    }

    @SuppressLint("CheckResult")
    fun getCars(apiKey: String) {
        repository.getCarsFromServer(apiKey).doOnSubscribe { loadingViewModel.value = View.VISIBLE }
                .doAfterTerminate { loadingViewModel.value = View.GONE }
                .subscribe({ triple ->
                },
                        { throwable: Throwable? -> throwable?.printStackTrace() })
    }

}