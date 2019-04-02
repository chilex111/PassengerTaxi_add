package com.kross.taxi_passenger.presentation.screen.add_car_activity

import com.kross.taxi_passenger.presentation.screen.car_owner_registration.MultipartBodyBuilder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class AddCarMultipartBodyBuilder(val multipartBuilder: MultipartBody.Builder) {



    fun setMake(make: String) {
        multipartBuilder.addFormDataPart("make", null, RequestBody.create(MediaType.parse("text/plain"), make))
    }

    fun setModel(model: String) {
        multipartBuilder.addFormDataPart("model", null, RequestBody.create(MediaType.parse("text/plain"), model))
    }

    fun setLicensePlateNumber(licensePlateNumber: String) {
        multipartBuilder.addFormDataPart("license_plate_number", null, RequestBody.create(MediaType.parse("text/plain"), licensePlateNumber))
    }

    fun setCarPhoto(carPhoto: String) {
        val filePhotoCar = File(carPhoto)
        multipartBuilder.addFormDataPart("car_photo", filePhotoCar.name, RequestBody.create(MediaType.parse("multipart/form-data"), filePhotoCar))
    }

    fun setProofOfOwnership(proofOfOwnership: String) {
        val filePhotoOwnershipProof = File(proofOfOwnership)
        multipartBuilder.addFormDataPart("proof_of_ownership", filePhotoOwnershipProof.name, RequestBody.create(MediaType.parse("multipart/form-data"), filePhotoOwnershipProof))
    }

    fun setInsurance(insurance: String) {
        val filePhotoInsurance = File(insurance)
        multipartBuilder.addFormDataPart("insurance", filePhotoInsurance.name, RequestBody.create(MediaType.parse("multipart/form-data"), filePhotoInsurance))
    }


    companion object {
        fun newBuilder(multipartBuilder: MultipartBody.Builder):  AddCarMultipartBodyBuilder =  AddCarMultipartBodyBuilder(multipartBuilder)
    }

}
