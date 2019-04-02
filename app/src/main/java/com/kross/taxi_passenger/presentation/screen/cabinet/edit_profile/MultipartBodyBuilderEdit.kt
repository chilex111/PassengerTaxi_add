package com.kross.taxi_passenger.presentation.screen.cabinet.edit_profile

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class MultipartBodyBuilderEdit(val multipartBuilder: MultipartBody.Builder) {

    fun setProfilePhoto(profilePhoto: String) {
    //    if (!profilePhoto.isEmpty()) {
            val filePhotoCar = File(profilePhoto)
            multipartBuilder.addFormDataPart("photo", filePhotoCar.name, RequestBody.create(MediaType.parse("multipart/form-data"), filePhotoCar))
    //    }
    }

    fun setFirstName(firstName: String) {
        multipartBuilder.addFormDataPart("first_name", null, RequestBody.create(MediaType.parse("text/plain"), firstName))
    }

    fun setLastName(lastName: String) {
        multipartBuilder.addFormDataPart("last_name", null, RequestBody.create(MediaType.parse("text/plain"), lastName))
    }

    fun setEmail(email: String) {
        multipartBuilder.addFormDataPart("email", null, RequestBody.create(MediaType.parse("text/plain"), email))
    }


    companion object {
        fun newBuilder(multipartBuilder: MultipartBody.Builder): MultipartBodyBuilderEdit = MultipartBodyBuilderEdit(multipartBuilder)
    }

}
