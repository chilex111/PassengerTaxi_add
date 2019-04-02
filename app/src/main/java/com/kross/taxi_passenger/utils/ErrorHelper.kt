package com.kross.taxi_passenger.utils

import android.content.Context
import com.kross.taxi_passenger.R
import org.json.JSONObject
import retrofit2.HttpException
import java.lang.IllegalStateException

object ErrorHelper {

    fun parseErrorAndGetString(context: Context, throwable: Throwable): String {
        return parseErrorAndGetString(context, getErrorCode(throwable))
    }

    fun parseErrorAndGetString(context: Context, errorCode: Int): String {
        var resultError = context.getString(R.string.txt_auth_screen_error_message)
        when (errorCode) {
            404 -> resultError = context.getString(R.string.txt_error_code_registr)
            14 -> resultError = context.getString(R.string.txt_error_message_number_not_match)
            15 -> resultError = context.getString(R.string.txt_error_phone_number_alredy_exist)
            17 -> resultError = context.getString(R.string.txt_error_user_not_found)
            10 -> resultError = context.getString(R.string.txt_error_email_alredy_exist)
            79 -> resultError = context.getString(R.string.txt_error_message_license_already_exist)
            25 -> resultError = context.getString(R.string.txt_error_message_not_a_car_owner)
        }
        return resultError
    }

    fun getErrorCode(throwable: Throwable): Int {
        var resultCode = -1
        try {
            val responseBody = (throwable as HttpException).response().errorBody()
            if (responseBody != null) {
                val jsonObject = JSONObject(responseBody.string())
                val errorObject = jsonObject.getJSONObject("error")
                resultCode = errorObject.optInt("code", -1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return resultCode
    }

}

fun illegalState(why: String): Nothing = throw IllegalStateException(why)