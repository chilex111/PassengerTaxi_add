package com.kross.taxi_passenger.domain

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.view.View
import com.google.gson.Gson
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.utils.ErrorHelper
import com.kross.taxi_passenger.utils.errorLog
import com.test.client_taxi_kross_driver_android.data.server.pojo.response.Make
import com.test.client_taxi_kross_driver_android.data.server.pojo.response.MakeList
import com.test.client_taxi_kross_driver_android.data.server.pojo.response.ModelCar
import com.test.client_taxi_kross_driver_android.data.server.pojo.response.ModelCarList
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChooseCarMakeViewModel(application: Application, private val repository: Repository) : BaseViewModel(application) {

    private val liveDataListCarMake = MutableLiveData<List<Make>>()

    private val liveDateListCarModel = MutableLiveData<List<ModelCar>>()

    private val liveDataError = MutableLiveData<Int>()

    private val liveDataLoading = MutableLiveData<Int>()

    fun getListCarMake(){
        repository.getListCarMake().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                val resultString = response?.body()?.string()?.split("(")?.get(1)?.split(")")?.get(0)
                val gson = Gson()
                val makeList = gson.fromJson<MakeList>(resultString, MakeList::class.java)
                liveDataListCarMake.value = makeList.listMake
                liveDataLoading.value = View.GONE
            }

            override fun onFailure(call: Call<ResponseBody>?, throwable: Throwable?) {
                liveDataLoading.value = View.GONE
                liveDataError.value = throwable?.let { ErrorHelper.getErrorCode(it) }
                throwable?.let { errorLog(this, it)  }
            }
        })

    }

    fun getListCarModel(makeCar: String){
        repository.getListCarModel(makeCar).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                val resultString = response?.body()?.string()?.split("(")?.get(1)?.split(")")?.get(0)
                val gson = Gson()
                val modelList = gson.fromJson<ModelCarList>(resultString, ModelCarList::class.java)
                liveDateListCarModel.value = modelList.listModel
                liveDataLoading.value = View.GONE
            }

            override fun onFailure(call: Call<ResponseBody>?, throwable: Throwable?) {
                liveDataLoading.value = View.GONE
                liveDataError.value = throwable?.let { ErrorHelper.getErrorCode(it) }
                throwable?.let { errorLog(this, it) }
            }
        })

    }

    fun getLiveDataCarMake(): LiveData<List<Make>> = liveDataListCarMake

    fun getLiveDataCarModel(): LiveData<List<ModelCar>> = liveDateListCarModel

    fun getLiveDataError(): LiveData<Int> = liveDataError

    fun getLiveDataLoading(): LiveData<Int> = liveDataLoading
}