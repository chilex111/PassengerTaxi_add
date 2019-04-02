package com.kross.taxi_passenger.presentation.screen.car_owner_registration

import android.util.Log
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class MultipartBodyBuilder(val multipartBuilder: MultipartBody.Builder) {

    fun setProfilePhoto(profilePhoto: String) {
        if (!profilePhoto.toLowerCase().contains("api-dev.kross.taxi")) {
            val filePhotoCar = File(profilePhoto)
            multipartBuilder.addFormDataPart("profile[photo]", filePhotoCar.name, RequestBody.create(MediaType.parse("multipart/form-data"), filePhotoCar))
            Log.d("FFFFFF", " www")
        } else {
            multipartBuilder.addFormDataPart("profile[photo]", profilePhoto, RequestBody.create(MediaType.parse("multipart/form-data"), profilePhoto))
        }
    }

    fun setFirstName(firstName: String) {
        multipartBuilder.addFormDataPart("profile[first_name]", null, RequestBody.create(MediaType.parse("text/plain"), firstName))
    }

    fun setLastName(lastName: String) {
        multipartBuilder.addFormDataPart("profile[last_name]", null, RequestBody.create(MediaType.parse("text/plain"), lastName))
    }

    fun setEmail(email: String) {
        multipartBuilder.addFormDataPart("profile[email]", null, RequestBody.create(MediaType.parse("text/plain"), email))
    }

    fun setMake(make: String) {
        multipartBuilder.addFormDataPart("car[make]", null, RequestBody.create(MediaType.parse("text/plain"), make))
    }

    fun setModel(model: String) {
        multipartBuilder.addFormDataPart("car[model]", null, RequestBody.create(MediaType.parse("text/plain"), model))
    }

    fun setLicensePlateNumber(licensePlateNumber: String) {
        multipartBuilder.addFormDataPart("car[license_plate_number]", null, RequestBody.create(MediaType.parse("text/plain"), licensePlateNumber))
    }

    fun setCarPhoto(carPhoto: String) {
        val filePhotoCar = File(carPhoto)
        multipartBuilder.addFormDataPart("car[car_photo]", filePhotoCar.name, RequestBody.create(MediaType.parse("multipart/form-data"), filePhotoCar))
    }

    fun setProofOfOwnership(proofOfOwnership: String) {
        val filePhotoOwnershipProof = File(proofOfOwnership)
        multipartBuilder.addFormDataPart("car[proof_of_ownership]", filePhotoOwnershipProof.name, RequestBody.create(MediaType.parse("multipart/form-data"), filePhotoOwnershipProof))
    }

    fun setInsurance(insurance: String) {
        val filePhotoInsurance = File(insurance)
        multipartBuilder.addFormDataPart("car[insurance]", filePhotoInsurance.name, RequestBody.create(MediaType.parse("multipart/form-data"), filePhotoInsurance))
    }


    companion object {
        fun newBuilder(multipartBuilder: MultipartBody.Builder): MultipartBodyBuilder = MultipartBodyBuilder(multipartBuilder)
    }

}
