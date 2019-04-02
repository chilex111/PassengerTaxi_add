package com.kross.taxi_passenger.data.mappers

import android.util.Log
import com.kross.taxi_passenger.data.repository.database.entity.CarEntity
import com.kross.taxi_passenger.data.repository.database.entity.DriverEntity
import com.kross.taxi_passenger.data.repository.server.pojo.response.OrderInfo
import io.reactivex.functions.Function

class OrderMapper(private val orderId: Int) : Function<OrderInfo, Triple<CarEntity?, DriverEntity?, OrderInfo?>> {

    override fun apply(order: OrderInfo): Triple<CarEntity?, DriverEntity?, OrderInfo?> {

    //    val driverEntity = DriverEntity(order.driver?.id, order.driver?.firstName, order.driver?.lastName, order.driver?.phoneNumber, order.driver?.driving_from, order.driver?.rating)
     //   val carEntity = order.car?.id?.let { CarEntity(it, order.car.make, order.car.model, order.car.year, null, order.car.plateNumber, 0f, 0, 4, null, order.driver?.id) }
        val carEntity =    order.car?.id?.let { CarEntity(it, order.car.make,  order.car.model,  order.car.year, null,  order.car.plateNumber, 0f, 0,4, null,  order.driver?.id)}
Log.d("MAPPPEERR", "Car: ${carEntity?.id} ${carEntity?.make}")
        val driverEntity = DriverEntity(order.driver?.id, order.driver?.firstName, order.driver?.lastName, order.driver?.phoneNumber, order.driver?.driving_from, null, null, null, null, null)
     //   val carEntity = order.car?.id?.let { CarEntity(it, order.car.make, order.car.model, order.car.year, null, order.car.plateNumber, 0f, 0, order.car.status!!, null, order.driver?.id) }
        Log.d("MAPPPEERR", "DRIVER: ${driverEntity?.id} ${driverEntity?.firstName}")

        return Triple(carEntity, driverEntity, order)

    }
}
