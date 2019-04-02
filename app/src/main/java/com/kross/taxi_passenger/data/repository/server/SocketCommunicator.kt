package com.kross.taxi_passenger.data.repository.server

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.OrderRepository
import com.kross.taxi_passenger.data.repository.server.socket.Parameter
import com.kross.taxi_passenger.data.repository.server.socket.Parser
import com.kross.taxi_passenger.data.repository.server.socket.Request
import com.kross.taxi_passenger.data.repository.server.socket.Response
import com.kross.taxi_passenger.utils.errorLog
import com.kross.taxi_passenger.utils.getStringPreferenceSocketToken
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.EngineIOException
import okhttp3.OkHttpClient
import org.jetbrains.anko.getStackTraceString
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SocketCommunicator(private val context: Context, val orderRepository: OrderRepository) {

    private var socketInstance: Socket? = null
    private lateinit var emitsListener: EmitsListener
    private lateinit var emitsListenerFull: EmitsListenerFull
    private val parser = Parser
    val sender = Sender()

    init {
        createConnection()
    }

    private fun createConnection() {
        val okHttpClient = createOkHttpClient()

        // default settings for all sockets
        IO.setDefaultOkHttpWebSocketFactory(okHttpClient)
        IO.setDefaultOkHttpCallFactory(okHttpClient)
        val option = IO.Options().apply {
            forceNew = true
            query = "token=${context.getStringPreferenceSocketToken(R.string.token)}"
            reconnection = true
            callFactory = okHttpClient
        }
        socketInstance = IO.socket(context.getString(R.string.socket_url), option).apply {
            initListeners()
           Log.d("Socket Communicator:","Socket id: " + socketInstance?.id())
            //   connect()
        }

    }

    private fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)

        return builder.build()
    }

    fun openConnection() {
        socketInstance?.connect()
    }

    fun closeConnection() {
        socketInstance?.disconnect()
//        socketInstance = null
    }


    fun checkConnectionStatus(): String {
        if (socketInstance?.connected() == false) createConnection()
        return when {
            socketInstance == null -> "ISocket: instance is null"
            socketInstance?.connected() == true -> "ISocket: is connected"
            socketInstance?.connected() == false -> "ISocket: is NOT connected"
            else -> "ISocket: Unknown state"
        }
    }

    fun setEmitsListener(emitsListener: EmitsListener) {
        this.emitsListener = emitsListener
    }

    fun setEmitsListenerFull(emitsListener: EmitsListenerFull) {
        this.emitsListenerFull = emitsListener
    }

    private fun Socket.initListeners() {
        // system events
        on(Socket.EVENT_CONNECT) { Log.d("Socket Communicator: ", "on connect - ${socketInstance?.id()}") }
        on(Socket.EVENT_RECONNECT) { Log.d("Socket Communicator: ", "on reconnect") }
        on(Socket.EVENT_DISCONNECT) { Log.d("Socket Communicator: ", "disconnect") }

        subscribeToEvent(Response.ERROR) { emitsListener.receiveErrorHandling(handleError(it)) }
        subscribeToEvent(Response.CONNECTION) { handleConnect(it) }
        subscribeToEvent(Response.ORDER_APPROVE) {
            Log.d("Socket Communicator: ", "order approve " + Parser.handleOrderStatus(it))
            emitsListenerFull.orderApprove(handleOrderStatus(it))
        }
        subscribeToEvent(Response.ORDER_FINISH) {
            emitsListenerFull.finishOrder(handleOrderStatus(it))
            Log.d("Socket Communicator: ", "order approve " + Parser.handleOrderStatus(it))
        }
        subscribeToEvent(Response.ORDER_ON_TRIP) {
            emitsListenerFull.orderOnTrip(handleOrderStatus(it))
            Log.d("Socket Communicator: ", "order on trip " + Parser.handleOrderStatus(it))
        }
        subscribeToEvent(Response.ORDER_DECLINE) { emitsListenerFull.driverDecline(handleOrderStatus(it))
            Log.d("Socket Communicator: ", "order decline")}
        subscribeToEvent(Response.ORDER_CHECK) { emitsListenerFull.checkStatus(handleOrderStatuss(it).toInt())
            Log.d("Socket Communicator: ", "order_check")}
        subscribeToEvent(Response.DRIVER_NOT_FOUND) { Log.d("Socket Communicator: ", "druver not found")
            emitsListenerFull.driverNotFound(handleOrderStatus(it).toInt())
            Log.d("Socket Communicator: ", "druver not found")}
        subscribeToEvent(Response.DRIVER_ARRIVED) {
            emitsListenerFull.driverArrived(handleOrderStatus(it))
            Log.d("Socket Communicator: ", "ARRIVED - " + handleOrderStatus(it))
        }
        subscribeToEvent(Response.DRIVER_LOCATION) { emitsListenerFull.getLocation(handleLatLng(it))
            Log.d("Socket Communicator: ", "LOCATION - " + handleLatLng(it))}
    }

    private fun Socket.subscribeToEvent(event: Response, onEvent: Parser.(JSONObject) -> Unit) {
        on(event.event) {
            if (it.isEmpty()) {
                errorLog(this, event.event)
                Log.e(Parser.TAG, "is empty - " + event.event)
                return@on
            }
            it.parseAndCall {
                onEvent.invoke(parser, this)
            }
        }
    }

    private fun Array<Any>.parseAndCall(onParsed: JSONObject.() -> Unit = {}) = try {
        val json = this[0]
        when (true) {
            json is EngineIOException -> parseIOException(onParsed, json)
            json is String -> onParsed.invoke(JSONObject(json.toString()))
            json is JSONObject -> onParsed.invoke(json as JSONObject)
            else -> {
                throw Exception("ISocket: Cannot parse JSON")
            }
        }
    } catch (e: Exception) {
        errorLog(this, e)
    }

    private fun parseIOException(onParsed: JSONObject.() -> Unit = {}, json: Any) {
        val throwable = json as EngineIOException
        val message = throwable.message ?: "Internal error"
        val jsonError = sender.createJson(Parameter.CODE_ERROR to 0, Parameter.MESSAGE to message, Parameter.ERROR_MESSAGE to 0)
        errorLog(SocketCommunicator::javaClass, "Error - ${(throwable.getStackTraceString())}")
        onParsed.invoke(jsonError)
    }

    /**
     * OUTGOING EVENTS
     */
    //todo: refactor
    inner class Sender {

        fun createJson(vararg pairs: Pair<Parameter, Any?>): JSONObject {
            var typeName: String
            var value: Any?
            return JSONObject().apply {
                for (pair in pairs) {
                    typeName = pair.first.rawValue
                    value = pair.second
                    put(typeName, value)
                }
            }
        }

        private fun emit(event: Request, jsonObject: JSONObject?) {
            socketInstance?.emit(event.event, jsonObject)
        }

        fun sendOrderCancleInTrip(orderId: String, driverId: String?) {
            emit(Request.ORDER_CANCEL, createJson(Parameter.ORDER_ID to orderId, Parameter.DRIVER_ID to driverId))
            Log.e(Parser.TAG, "send order cancel without trip ${createJson(Parameter.ORDER_ID to orderId)}")
        }

        fun sendOrderCancle(orderId: String, driverId: String?) {
            emit(Request.ORDER_DECLINE, createJson(Parameter.ORDER_ID to orderId))
            Log.e(Parser.TAG, "send order cancel in trip ${createJson(Parameter.ORDER_ID to orderId)}")
        }

        fun checkOrderStatus(orderId: String?) {
            emit(Request.ORDER_CHECK, null)
          //  Log.e(Parser.TAG, "send order decline ${createJson(Parameter.ORDER_ID to orderId)}")
        }
    }

    interface EmitsListener {

        fun receiveErrorHandling(error: Triple<String, String, String>)
    }

    interface EmitsListenerFull : EmitsListener {

        fun finishOrder(orderId: String)
        fun driverArrived(orderId: String)
        fun driverDecline(orderId: String)
        fun orderApprove(orderId: String)
        fun orderOnTrip(orderId: String)
        fun getLocation(latLng: LatLng)
        fun checkStatus(status: Int)
        fun driverNotFound(orderId: Int)
    }

}
