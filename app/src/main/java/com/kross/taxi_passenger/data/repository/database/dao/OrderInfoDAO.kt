package com.kross.taxi_passenger.data.repository.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.kross.taxi_passenger.data.repository.database.entity.OrderEntity
import com.kross.taxi_passenger.domain.entity.PointState
import com.kross.taxi_passenger.domain.entity.RoutePoint
import io.reactivex.Flowable


@Dao
abstract class OrderInfoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOrder(order: OrderEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateOrder(order: OrderEntity)

    @Query("select * from order_entity where order_status != 7")
    abstract fun getOrder(): Flowable<List<OrderEntity>>

    @Query("select * from order_entity")
    abstract fun getAllOrders(): List<OrderEntity>

    @Query("select * from order_entity")
    abstract fun getAllOrdersRecent():  LiveData<List<OrderEntity>>

    @Query("select * from order_entity where order_status != 7")
    abstract fun getActiveOrders(): LiveData<List<OrderEntity>>

    @Query("select * from order_entity where order_status = 7")
    abstract fun getFinishedOrders(): LiveData<List<OrderEntity>>

    @Query("select * from order_entity where id = :orderId")
    abstract fun findBy(orderId: Int): OrderEntity?


    @Query("select driver_id from order_entity where driver_id = :driverId")
    abstract fun getDriverId(driverId: Int): Int?


    @Query("select car_id from order_entity where  car_id= :carId")
    abstract fun getCarId(carId: Int): Int?

    @Query("delete from order_entity where id = :orderId")
    abstract fun deleteOrderInfo(orderId: Int)

    @Query("update order_entity set order_status = 7 where id = :orderId")
    abstract fun cancelOrderInfo(orderId: Int)

    @Query("update order_entity set payment_type = :paymentType where id = :orderId")
    abstract fun updateOrderPaymentType(orderId: Int, paymentType: Int)

    @Query("update order_entity set car_id = :carId,driver_id = :driverId  where id = :orderId")
    abstract fun updateOrderCarAndDriver(orderId: Int, carId: Int, driverId: Int)

    @Query("update order_entity set payment_type = :paymentType, card_number = :cardNumber where id = :orderId")
    abstract fun updatePaymentTypeAndCard(paymentType: Int, cardNumber: String, orderId: Int)

    @Query("update order_entity set comment = :comment where id = :orderId")
    abstract fun updateOrderComment(comment: String, orderId: Int)

    @Query("update order_entity set order_for = :phoneNumber where id = :orderId")
    abstract fun updateOrderFor(orderId: Int, phoneNumber: String?)

    @Query("update order_entity set order_status = :orderStatus where id = :orderId")
    abstract fun updateOrderStatus(orderId: Int, orderStatus: Int)

    @Query("update order_entity set order_status = :orderStatus where id = :oldOrderId")
    abstract fun updateStatus(oldOrderId: Int,orderStatus: Int)

    @Query("update order_entity set id = :newOrderId where id = :oldOrderId")
    abstract fun updateOrderId(oldOrderId: Int, newOrderId: Int)

    @Query("update order_entity set order_status = :orderStatus, polyline = :polyline where id = :orderId")
    abstract fun acceptOrder(orderId: Int, orderStatus: String, polyline: String?)

    @Query("update order_entity set polyline = :polyline where id = :orderId")
    abstract fun updatePolyline(orderId: Int, polyline: String?)

    @Transaction
    @Query("")
    fun updatePoint(point: RoutePoint, to: PointState, order: OrderEntity) {
        point?.state = to.ordinal
        updateOrder(order)
    }

}