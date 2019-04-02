package com.kross.taxi_passenger.domain

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.arch.lifecycle.LiveData
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.kross.taxi_passenger.data.repository.OrderRepository
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.database.entity.CarEntity
import com.kross.taxi_passenger.data.repository.database.entity.DriverEntity
import com.kross.taxi_passenger.data.repository.database.entity.OrderEntity
import com.kross.taxi_passenger.data.repository.server.SocketCommunicator
import com.kross.taxi_passenger.data.repository.server.pojo.FavoriteAddress
import com.kross.taxi_passenger.data.repository.server.pojo.FavoriteRoute
import com.kross.taxi_passenger.data.repository.server.pojo.request.HelpRequest
import com.kross.taxi_passenger.data.repository.server.pojo.request.Order
import com.kross.taxi_passenger.data.repository.server.pojo.request.RateDriver
import com.kross.taxi_passenger.data.repository.server.pojo.request.TokenRequest
import com.kross.taxi_passenger.data.repository.server.pojo.response.OrderId
import com.kross.taxi_passenger.domain.entity.PointState
import com.kross.taxi_passenger.domain.entity.RoutePoint
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.managers.NetworkChangeReceiver
import com.kross.taxi_passenger.managers.ProviderChangeReceiver
import com.kross.taxi_passenger.presentation.screen.main.map.MapFragment
import com.kross.taxi_passenger.presentation.screen.main.map.State
import com.kross.taxi_passenger.presentation.widget.SingleLiveEvent
import com.kross.taxi_passenger.utils.MapHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class MapViewModel(application: Application,
                   private val networkReceiver: NetworkChangeReceiver,
                   private val repository: Repository,
                   private val orderRepository: OrderRepository,
                   private val socketCommunicator: SocketCommunicator,
                   providerReceiver: ProviderChangeReceiver) : BaseViewModel(application) {

    private val orderLiveData = SingleLiveEvent<List<OrderEntity>>()
    private val orderIdsLiveData = SingleLiveEvent<List<OrderEntity>>()
    private val orderIdLiveData = SingleLiveEvent<OrderId>()
    private val disposables = CompositeDisposable()
    private val loadingLiveData = SingleLiveEvent<Boolean>()
    private val isSuccessLiveData = SingleLiveEvent<Boolean>()

    val connectionLiveData = networkReceiver.connectivityLiveData
    fun isConnected(): Boolean {
        return connectionLiveData.value ?: networkReceiver.updateConnectivityState(getContext())
    }

    val providerLiveData = providerReceiver.providerChangeLiveData

    @SuppressLint("CheckResult")
    fun getDirection(mapsKey: String, waypoints: List<LatLng>,
                     onDirections: (Pair<Int, String>) -> Unit, onError: (String?) -> Unit) {
        repository
                .getDirection(mapsKey, waypoints)
                .doOnSubscribe { loadingLiveData.value = true }
                .doAfterTerminate { loadingLiveData.value = false }
                .subscribe(
                        {
                            if (!(it.routes.isEmpty())) onDirections.invoke(Pair(it.routes[0].getTotalDistance(), MapHelper.buildFullPolyline(it)))
                        },
                        {
                            it.printStackTrace()
                            onError.invoke(it.message)
                        })

    }

    fun cancelOrder(orderId: Int, driverId: Int?) {
        socketCommunicator.sender.sendOrderCancle(orderId.toString(), driverId?.toString())
    }

    fun cancelOrderInTrip(orderId: Int, driverId: Int?) {
        socketCommunicator.sender.sendOrderCancleInTrip(orderId.toString(), driverId?.toString())
    }


    fun checkStatus(orderId: Int?) {
        socketCommunicator.sender.checkOrderStatus(orderId.toString())
    }

    fun openConnection() {
        socketCommunicator.openConnection()
    }

    fun closeConnection() {
        socketCommunicator.closeConnection()
    }

    @SuppressLint("CheckResult")
    fun sendPushToken(contentType: String,
                      apiKey: String,
                      pushRequest: TokenRequest) {
        repository.putPushToken(contentType, apiKey, pushRequest)
                .subscribe({
                }, {
                    print("error sending_push")
                })
    }

    fun getContactInfo(intent: Intent, activity: Activity): String? {
        var phoneNumber: String? = null
        val cursor = activity.contentResolver.query(intent.data, null, null, null, null)
        while (cursor.moveToNext()) {
            val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))

            var hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

            hasPhone = if (hasPhone.equals("1", ignoreCase = true))
                "true"
            else
                "false"

            if (java.lang.Boolean.parseBoolean(hasPhone)) {
                val phones = activity.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null)
                while (phones.moveToNext()) {
                    phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER))
                }
                phones.close()
            }
        }
        cursor.close()
        return phoneNumber
    }

    @SuppressLint("CheckResult")
    fun updateOrder(token: String, orderId: Int): LiveData<Boolean> {
        repository.getOrderInfo(token, orderId).subscribe(
                {
                    it.first?.id?.let { it1 -> it.second?.id?.let { it2 -> updateOrderCarAndDriver(orderId, it1, it2) } }
                    it.third?.status?.let {
                        updateStatus(orderId, it)
                    }
                    isSuccessLiveData.value = it.third?.changed
                },
                {
                    it.printStackTrace()
                    isSuccessLiveData.value = false
                    Log.d("STATUS_SERVER", "fail")
                }
        )
        return isSuccessLiveData
    }


    private fun updateStatus(orderId: Int, status: Int) {
        when (status) {
            1 -> updateOrderStatus(orderId, 5)
            2 -> updateOrderStatus(orderId, 9)
            3 -> updateOrderStatus(orderId, 6)
            6 -> updateOrderStatus(orderId, 7)
            7 -> updateOrderStatus(orderId, 7)
            8 -> updateOrderStatus(orderId, 7)
        }
    }

    fun getCar(carId: Int): CarEntity? {
        Log.d("MAPPPEERR", carId.toString())
        return repository.getCar(carId)
    }

    fun getDriver(driverId: Int): DriverEntity? {
        return repository.getDriver(driverId)
    }

    fun openApplicationSettings(activity: Activity, fragment: MapFragment, requestCode: Int) {
        val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${activity.packageName}"))
        fragment.startActivityForResult(appSettingsIntent, requestCode)
    }

    fun openApplicationSettings(activity: Activity, requestCode: Int) {
        val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${activity.packageName}"))
        activity.startActivityForResult(appSettingsIntent, requestCode)
    }

    @SuppressLint("CheckResult")
    fun addOrder(token: String, order: Order): LiveData<OrderId> {
        repository.setNewOrder(token, order).subscribe({
            orderIdLiveData.value = it

        }, {
            print("error sending_push")

        })

        return orderIdLiveData
    }

    @SuppressLint("CheckResult")
    fun addHelp(help: HelpRequest, listener: () -> Unit) {
        loadingLiveData.value = true
        repository.sendHelpRequest(token, contentType, help)
                .subscribe({
                    loadingLiveData.value = false
                    listener.invoke()
                }, {
                    it.printStackTrace()
                    loadingLiveData.value = false
                })
    }

    private val progressLiveData = SingleLiveEvent<Boolean>()
    private val addressIdLiveData = SingleLiveEvent<Int>()
    private val routeIdLiveData = SingleLiveEvent<Int>()

    // TODO Move to pagination architecture component
    fun addFavoriteAddress(favoriteAddress: FavoriteAddress): LiveData<Int> {
        progressLiveData.value = true
        repository.addFavoriteAddress(token, favoriteAddress)
                .subscribe({
                    progressLiveData.value = false
                    addressIdLiveData.value = it.addressId
                }, {
                    it.printStackTrace()
                    progressLiveData.value = false
                })
                .apply {
                    disposables.add(this)
                }
        return addressIdLiveData
    }

    fun deleteFavoriteAddress(id: Int, listener: () -> Unit) {
        progressLiveData.value = true
        repository.deleteFavoriteAddress(token, id)
                .subscribe({
                    progressLiveData.value = false
                    listener.invoke()
                }, {
                    it.printStackTrace()
                    progressLiveData.value = false
                })
                .apply { disposables.add(this) }

    }

    fun addFavoriteRoute(favoriteRoute: FavoriteRoute): LiveData<Int> {
        progressLiveData.value = true
        repository.addFavoriteRoute(token, contentType, favoriteRoute)
                .subscribe({
                    progressLiveData.value = false
                    routeIdLiveData.value = it.routeId
                }, {
                    it.printStackTrace()
                    progressLiveData.value = false
                })
                .apply { disposables.add(this) }

        return routeIdLiveData
    }

    fun rateDriver(id: Int, rateDriver: RateDriver, listener: () -> Unit) {
        repository
                .rateDriver(token, contentType, id, rateDriver)
                .subscribe({
                    progressLiveData.value = false
                    listener.invoke()
                }, {
                    it.printStackTrace()
                    progressLiveData.value = false
                })
                .apply { disposables.add(this) }
    }

    fun deleteFavoriteRoute(id: Int) {
        progressLiveData.value = true
        repository.deleteFavoriteRoute(token, id)
                .subscribe({
                    progressLiveData.value = false
                }, {
                    progressLiveData.value = false
                    it.printStackTrace()
                })
                .apply { disposables.add(this) }
    }

    @SuppressLint("CheckResult")
    fun getOrderPriority(priority: Int = 0): LiveData<List<OrderEntity>> {
        orderRepository.getOrder().observeOn(AndroidSchedulers.mainThread()).subscribe {
            orderLiveData.value = if (priority == 0) it.sortedBy { it.priority }
            else it.sortedByDescending { it.priority }
        }
        return orderLiveData
    }

    @SuppressLint("CheckResult")
    fun getOrderId(priority: Int = 0): LiveData<List<OrderEntity>> {
        orderRepository.getOrder().observeOn(AndroidSchedulers.mainThread()).subscribe {
            orderIdsLiveData.value = if (priority == 0) it.sortedBy { it.priority }
            else it.sortedByDescending { it.priority }
        }
        return orderIdsLiveData
    }

    @SuppressLint("CheckResult")
    fun getOrder(): LiveData<List<OrderEntity>> {
        orderRepository.getOrder().observeOn(AndroidSchedulers.mainThread()).subscribe {
            orderLiveData.value = it
        }
        return orderLiveData
    }

    fun getOrders(status: State = State.DEFAULT): LiveData<List<OrderEntity>>? {
        return if (status == State.TRIP_FINISHED) orderRepository.getFinishedOrders() else orderRepository.getActiveOrders()
    }

    fun getOrderById(id: Int): OrderEntity? {
        return orderRepository.getOrderBy(id)
    }

    fun insertOrder(id: Int = 0,
                    carType: Int = 0,
                    date: String? = null,
                    comment: String = "",
                    orderFor: String? = null,
                    costRange: String = "",
                    paymentType: Int = 2,
                    cardNumber: String? = null,
                    polyline: String = "",
                    distance: Int,
                    orderStatus: Int = 1,
                    listRoutePoint: List<StubWaypointAddress>,
                    priority: Int = 0) {
        orderRepository.insertOrder(id, carType, date, comment, orderFor, costRange, paymentType, cardNumber, polyline, distance, orderStatus, listRoutePoint, priority)
    }

    fun deleteOrder(orderId: Int) = orderRepository.deleteOrder(orderId)

    fun cancelOrder(orderId: Int) {
        orderRepository.cancelOrder(orderId)
    }

    fun updateOrderPaymentType(orderId: Int, paymentType: Int) = orderRepository.updateOrderPaymentType(orderId, paymentType)

    private fun updateOrderCarAndDriver(orderId: Int, carId: Int, driverId: Int) = orderRepository.updateOrderCarAndDriver(orderId, carId, driverId)

    fun updatePaymentTypeAndCard(paymentType: Int, cardNumber: String, orderId: Int) = orderRepository.updatePaymentTypeAndCard(paymentType, cardNumber, orderId)

    fun updateOrderComment(comment: String, orderId: Int) = orderRepository.updateOrderComment(comment, orderId)

    fun updateOrderFor(orderId: Int, phoneNumber: String?) = orderRepository.updateOrderFor(orderId, phoneNumber)

    fun updateOrderStatus(orderId: Int, orderStatus: Int) = orderRepository.updateOrderStatus(orderId, orderStatus)

    fun updateOrderId(orderId: Int, newOrderId: Int) = orderRepository.updateOrderId(orderId, newOrderId)

    fun getLoadingLiveData(): LiveData<Boolean> = loadingLiveData

    fun updatePoint(point: RoutePoint, to: PointState, orderEntity: OrderEntity?) {
        orderEntity?.let { orderRepository.updatePoint(point, to, it) }
    }

}