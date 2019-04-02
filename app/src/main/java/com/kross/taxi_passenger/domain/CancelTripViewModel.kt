package com.kross.taxi_passenger.domain

import android.app.Application
import com.kross.taxi_passenger.data.repository.OrderRepository
import com.kross.taxi_passenger.data.repository.database.entity.OrderEntity
import com.kross.taxi_passenger.data.repository.server.ServerCommunicator
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable

class CancelTripViewModel(application: Application,
                          private val repository: OrderRepository,
                          private val communicator: ServerCommunicator): BaseViewModel(application) {

    fun cancelOrder(order: OrderEntity, reasons:Pair<List<Int>, String>, block: (Boolean) -> Unit) {

        // MARK: - method will work with sockets
        // TODO: - change ServerCommunicator to SocketCommunicator
        // TODO: - use block if needed, else delete it
        val otherReason = reasons.second
        val checkedReasons = reasons.first

        repository.cancelOrder(order.id)
        block.invoke(true)

    }

    fun getOrderBy(id: Int): OrderEntity? {
        return repository.getOrderBy(id)
    }

}