package com.kross.taxi_passenger.data.mappers

import com.kross.taxi_passenger.data.repository.database.entity.WalletEntity
import com.kross.taxi_passenger.data.repository.server.pojo.response.Wallet
import io.reactivex.functions.Function

class WalletEntityMappers(val passengerId: Int): Function<Wallet, WalletEntity>{
    override fun apply(wallet: Wallet): WalletEntity {
        return WalletEntity(passengerId, wallet.total)
    }
}