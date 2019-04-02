package com.kross.taxi_passenger.data.repository.server.socket

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject


/**
 * INCOMING EVENTS
 */
object Parser {

    val TAG = "Socket Communicator: "

    fun handleConnect(jsonObject: JSONObject){
        val userId = jsonObject.getInt(Parameter.USER_ID.rawValue)
        Log.d(TAG, "User id - $userId")
    }


    fun handleOrderStatus(jsonObject: JSONObject): String{
        val orderId = jsonObject.getString(Parameter.ORDER_ID.rawValue)
        Log.d(TAG, "Order id - $orderId")
        return orderId
    }

    fun handleOrderStatuss(jsonObject: JSONObject): String{
        val orderStatus = jsonObject.getString(Parameter.STATUS.rawValue)
        return orderStatus
    }

    fun handleError(jsonObject: JSONObject): Triple<String, String, String>{
        val codeError = jsonObject.getString(Parameter.CODE_ERROR.rawValue)
        val message = jsonObject.getString(Parameter.MESSAGE.rawValue)
        val errorMessage = jsonObject.getString(Parameter.ERROR_MESSAGE.rawValue)
        Log.e(TAG, "Error code - $codeError " + "message - $message error message - $errorMessage")
        return Triple(codeError, message, errorMessage)
    }

    fun handlePosition(jsonObject: JSONObject){
        val lat = jsonObject.getDouble(Parameter.LATITUDE.rawValue)
        val lng = jsonObject.getDouble(Parameter.LONGITUDE.rawValue)
        Log.e(TAG, "Driver position: lat $lat; lng $lng")
    }

    fun handleLatLng(jsonObject: JSONObject): LatLng {
        val lat = jsonObject.getDouble(Parameter.LATITUDE.rawValue)
        val lng = jsonObject.getDouble(Parameter.LONGITUDE.rawValue)
        Log.e(TAG, "Driver position: lat $lat; lng $lng")
        return LatLng(lat, lng)
    }

    fun handleOrder(jsonObject: JSONObject): Int{
        Log.e(TAG, "new order obtain $jsonObject")
        val order = jsonObject.getInt(Parameter.ORDER_ID.rawValue)
        return order
    }

    fun handleTimeOutOrder(jsonObject: JSONObject): String{
        val orderId = jsonObject.getString(Parameter.ORDER_ID.rawValue)
        Log.e(TAG, "time out delete order $orderId")
        return orderId
    }
}
