package com.kross.taxi_passenger.data.repository

import android.arch.lifecycle.LiveData
import com.kross.taxi_passenger.data.repository.database.Database
import com.kross.taxi_passenger.data.repository.database.entity.OrderEntity
import com.kross.taxi_passenger.data.repository.server.ServerCommunicator
import com.kross.taxi_passenger.data.repository.server.pojo.request.Order
import com.kross.taxi_passenger.data.repository.server.pojo.request.Route
import com.kross.taxi_passenger.domain.entity.PointState
import com.kross.taxi_passenger.domain.entity.RoutePoint
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.utils.doOnRxAsync
import io.reactivex.Flowable
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import java.util.*
import com.kross.taxi_passenger.data.repository.server.pojo.request.Coords as Coord
import com.kross.taxi_passenger.data.repository.server.pojo.request.RoutePoint as RoutePointServer

class OrderRepository(private val serverCommunicator: ServerCommunicator,
                      private val mainDatabase: Database) {

    fun getOrder(): Flowable<List<OrderEntity>> {
        return mainDatabase.orderDao().getOrder()
    }

    fun getFinishedOrders(): LiveData<List<OrderEntity>>? {
        synchronized(this) {
            val doAsyncResult = doAsyncResult { mainDatabase.orderDao().getFinishedOrders() }
            return doAsyncResult.get()
        }
    }

    fun getActiveOrders(): LiveData<List<OrderEntity>>? {
        synchronized(this) {
            val doAsyncResult = doAsyncResult { mainDatabase.orderDao().getActiveOrders() }
            return doAsyncResult.get()
        }
    }

    fun getOrderBy(id: Int): OrderEntity? {
        synchronized(this) {
            val doAsyncResult = doAsyncResult { mainDatabase.orderDao().findBy(id) }
            return doAsyncResult.get()
        }
    }

    fun insertOrder(id: Int,
                    carType: Int,
                    date: String?,
                    comment: String,
                    orderFor: String?,
                    costRange: String,
                    paymentType: Int,
                    cardNumber: String?,
                    polyline: String,
                    distance: Int,
                    orderStatus: Int,
                    listRoutePoint: List<StubWaypointAddress>,
                    priority: Int) {
        doAsync {
            mainDatabase.orderDao().insertOrder(OrderEntity(id, carType, date, comment, orderFor, costRange, paymentType,
                    cardNumber, polyline, distance, orderStatus, convert(listRoutePoint), priority, null, null))
        }
    }

    private fun convert(input: List<StubWaypointAddress>): List<RoutePoint> {
        val listRoute = ArrayList<RoutePoint>()
        input.forEach {
            listRoute.add(RoutePoint(System.currentTimeMillis(), it.address, it.address, it.latLng.latitude, it.latLng.longitude))
        }
        return listRoute
    }

    fun deleteOrder(orderId: Int) {
        doAsync {
            mainDatabase.orderDao().deleteOrderInfo(orderId)
        }
    }

    fun cancelOrder(orderId: Int) {
        doAsync {
            mainDatabase.orderDao().cancelOrderInfo(orderId)
        }
    }

    fun updateOrderPaymentType(orderId: Int, paymentType: Int) {
        doAsync {
            mainDatabase.orderDao().updateOrderPaymentType(orderId, paymentType)
        }
    }

    fun updateOrderCarAndDriver(orderId: Int, driverId: Int, carId: Int) {
        doAsync {
            mainDatabase.orderDao().updateOrderCarAndDriver(orderId, driverId, carId)
        }
    }

    fun updatePaymentTypeAndCard(paymentType: Int, cardNumber: String, orderId: Int) {
        doAsync {
            mainDatabase.orderDao().updatePaymentTypeAndCard(paymentType, cardNumber, orderId)
        }
    }

    fun updateOrderComment(comment: String, orderId: Int) {
        doAsync {
            mainDatabase.orderDao().updateOrderComment(comment, orderId)
        }
    }

    fun updateOrderFor(orderId: Int, phoneNumber: String?) {
        doAsync {
            mainDatabase.orderDao().updateOrderFor(orderId, phoneNumber)
        }
    }

    fun updateOrderStatus(orderId: Int, orderStatus: Int): Boolean {
        synchronized(this) {
            val doAsyncResult =     doAsyncResult { mainDatabase.orderDao().updateOrderStatus(orderId, orderStatus) }

            return doAsyncResult.isDone
        }
    }


    fun updateOrderId(oldOrderId: Int, newOrderId: Int) {
        doAsync {
            mainDatabase.orderDao().updateOrderId(oldOrderId, newOrderId)
        }
    }

    fun orderTransform(order: OrderEntity): Order {
        val listRoute = ArrayList<RoutePointServer>()
        order.listRoutePoint.forEach {
            listRoute.add(RoutePointServer(
                    Coord(it.lat, it.lng), it.street
            ))
        }
        return Order(Route(order.polyline, listRoute),
                order.carType,
                order.date,
                order.comment,
                order.orderFor,
                order.costRange,
                order.paymentType)
    }

    fun updatePoint(point: RoutePoint, to: PointState, order: OrderEntity) = doOnRxAsync {
        synchronized(this) {
            mainDatabase.orderDao().updatePoint(point, to, order)
        }
    }

}