package com.kross.taxi_passenger.data.mappers

import com.kross.taxi_passenger.data.repository.database.entity.PassengerEntity
import com.kross.taxi_passenger.data.repository.server.pojo.response.ProfileInfo
import io.reactivex.functions.Function

class PassengerEntityMapper(val passengerId: Int) : Function<ProfileInfo, PassengerEntity> {

    override fun apply(profileInfo: ProfileInfo): PassengerEntity {
        return PassengerEntity(passengerId, profileInfo.firstName, profileInfo.lastName, profileInfo.email, profileInfo.phoneNumber, profileInfo.emailVerified, profileInfo.referralCode, profileInfo.type, profileInfo.status, profileInfo.photoUrl)
    }
}