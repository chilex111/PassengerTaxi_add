package com.kross.taxi_passenger.data.repository

import android.arch.lifecycle.LiveData
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonObject
import com.kross.taxi_passenger.data.mappers.CarsMapper
import com.kross.taxi_passenger.data.mappers.OrderMapper
import com.kross.taxi_passenger.data.mappers.PassengerEntityMapper
import com.kross.taxi_passenger.data.mappers.WalletEntityMappers
import com.kross.taxi_passenger.data.repository.database.Database
import com.kross.taxi_passenger.data.repository.database.entity.*
import com.kross.taxi_passenger.data.repository.server.ServerCommunicator
import com.kross.taxi_passenger.data.repository.server.pojo.FavoriteAddress
import com.kross.taxi_passenger.data.repository.server.pojo.FavoriteRoute
import com.kross.taxi_passenger.data.repository.server.pojo.request.*
import com.kross.taxi_passenger.data.repository.server.pojo.response.*
import com.kross.taxi_passenger.utils.RxUtil
import com.kross.taxi_passenger.utils.RxUtil.completableTransformer
import com.kross.taxi_passenger.utils.RxUtil.singleTransformer
import io.reactivex.Single
import kotlinx.coroutines.experimental.async
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import retrofit2.Call
import retrofit2.Response

class Repository(private val serverCommunicator: ServerCommunicator,
                 private val mainDatabase: Database,
                 private val oderRepository: OrderRepository) {

    fun getAll(): LiveData<List<ExampleEntity>> {
        serverCommunicator.getAll().subscribe({ it -> mainDatabase.mainDao().updateAll(it) }, { it -> it.printStackTrace() })
        return mainDatabase.mainDao().getAll()
    }

    fun emailVerify(contentType: String,
                    apiKey: String,
                    verify: EmailVerifyBody): Single<Response<ResponseBody>> {
        return serverCommunicator.emailVerify(contentType, apiKey, verify)
    }



    fun addNew(mainEntity: ExampleEntity) {
        async { mainDatabase.mainDao().add(mainEntity) }
    }

    fun addCard(card: Card) {
        async {
            val cards = mainDatabase.cardDao().getUserCardsSync(card.userId)
            if (cards == null || cards.isEmpty()) {
                card.defaultFlag = true
            }

            mainDatabase.cardDao().insert(card)
        }
    }

    fun getUserCards(userId: Int) = mainDatabase.cardDao().getUserCard(userId)

    fun phoneConfirm(contentType: String,
                     apiKey: String,
                     cacheControl: String,
                     jsonObject: JsonObject): Single<PhoneConfirm> {
        return serverCommunicator.phoneConfirm(contentType, apiKey, cacheControl, jsonObject)
    }

    fun registration(apiKey: String,
                     registration: Registration): Single<UserId> {
        return serverCommunicator.registration(apiKey, registration)
    }

    fun authorization(contentType: String,
                      apiKey: String,
                      authorization: Authorization): Single<Token> {
        return serverCommunicator.authorization(contentType, apiKey, authorization)
    }


    fun becomeCarOwner(contentType: String,
                       apiKey: String,
                       multipartBody: List<MultipartBody.Part>): Single<Response<ResponseBody>> {
        return serverCommunicator.becomeOwner(contentType, apiKey, multipartBody)
    }

    fun profileEdit(contentType: String,
                       apiKey: String,
                       multipartBody: List<MultipartBody.Part>): Single<Response<ResponseBody>> {
        return serverCommunicator.profileEdit(contentType, apiKey, multipartBody)
    }

    fun addCar(contentType: String,
                       apiKey: String,
                       multipartBody: List<MultipartBody.Part>): Single<AddCarResponce> {
        return serverCommunicator.addCar(contentType, apiKey, multipartBody)
    }

    fun passwordRecovery(contentType: String,
                         apiKey: String,
                         jsonObject: JsonObject): Single<Response<ResponseBody>> {
        return serverCommunicator.passwordRecovery(contentType, apiKey, jsonObject)
    }

    fun releaseDriver(apiKey: String,
                      driverId: Int,
                      carId: Int): Single<Response<ResponseBody>> {
        return serverCommunicator.releaseDriver(apiKey, driverId, carId)
    }


    fun assignDriver(apiKey: String,
                     driverId: Int,
                     carId: Int): Single<Response<ResponseBody>> {
        return serverCommunicator.assignDriver(apiKey, driverId, carId)
    }


    fun changePassword(contentType: String,
                       apiKey: String,
                       jsonObject: JsonObject): Single<Response<ResponseBody>> {
        return serverCommunicator.changePassword(contentType, apiKey, jsonObject)
    }

    fun putPushToken(contentType: String,
                     apiKey: String,
                     pushTokenRequest: TokenRequest): Single<Response<ResponseBody>> {
        return serverCommunicator.putPushToken(contentType, apiKey, pushTokenRequest)
    }

    fun getPassenger(passengerId: Int): LiveData<PassengerEntity> {
        return mainDatabase.passengerDao().getPassengerById(passengerId)
    }

    fun getProfileInfo(contentType: String,
                       token: String,
                       passengerId: Int): Single<PassengerEntity> {
        return serverCommunicator.getProfileInfo(contentType, token)
                .map(PassengerEntityMapper(passengerId))
                .flatMap { passengerEntity: PassengerEntity ->
                    mainDatabase.passengerDao().insertPassenger(passengerEntity)
                    return@flatMap Single.just(passengerEntity)
                }
                .compose(RxUtil.singleTransformer())
    }

    fun getOrderInfo(token: String, orderId: Int): Single<Triple<CarEntity?, DriverEntity?, OrderInfo?>> {

        //     return serverCommunicator.getOrderInfo(token, orderId)
        return serverCommunicator.getOrderInfo(token, orderId)
                .map(OrderMapper(orderId)).flatMap {
                    async {
                        it.first?.let { it1 -> mainDatabase.carDao().insertCar(it1) }
                        it.second?.let { it1 -> mainDatabase.carDao().insertDriver(it1) }
                    }
                    return@flatMap Single.just(it)
                }.compose(RxUtil.singleTransformer())

    }


    fun getRecent(): LiveData<List<OrderEntity>> {
        return mainDatabase.orderDao().getAllOrdersRecent()
    }

    fun getCarsFromServer(token: String): Single<Triple<List<CarEntity>, List<DriverEntity>, List<LocationEntity>>> {
        return serverCommunicator.getCars(token).map(CarsMapper())
                .flatMap {
                    doAsync {
                        mainDatabase.carDao().insertCars(it.first)
                        mainDatabase.carDao().insertDrivers(it.second)
                        mainDatabase.carDao().insertLocations(it.third)
                    }
                    return@flatMap Single.just(it)
                }.compose(RxUtil.singleTransformer())
    }

    fun insertCar(carEntity: CarEntity) {
        val asyncResult = doAsyncResult { mainDatabase.carDao().insertCar(carEntity) }
    }

    fun insertDriver(driverEntity: DriverEntity) {
        val asyncResult = doAsyncResult { mainDatabase.carDao().insertDriver(driverEntity) }
    }

    fun getDriversFromServer(token: String, amount: Int): Single<DriversList> {
        return serverCommunicator.getDrivers(token, amount).compose(RxUtil.singleTransformer())
    }

    fun getDriverByIdFomServer(token: String, id: Int, amount: Int): Single<Driver> {
        return serverCommunicator.getDriverByIdFomServer(token, id, amount).compose(RxUtil.singleTransformer())
    }

    fun getCarStatisticFromServer(token: String, id: Int, dateStart: String, dateEnd: String): Single<Cars> {
        return serverCommunicator.getCarStatistic(token, id, dateStart, dateEnd).compose(RxUtil.singleTransformer())
    }


    fun getCarTtipsStatisticFromServer(token: String, id: Int, date: String): Single<TripsStatistic> {
        return serverCommunicator.getCarTripsStatistic(token, id, date).compose(RxUtil.singleTransformer())
    }


    fun getCarChart(token: String, id: Int, dateStart: String): Single<Response<Map<String, Int>>> {
        val asyncResult = doAsyncResult { serverCommunicator.getCarChart(token, id, dateStart) }
        return asyncResult.get()
    }

    fun getCars(): List<CarEntity>? {
        val asyncResult = doAsyncResult { mainDatabase.carDao().getCars() }
        return asyncResult.get()
    }


    fun getLocation(locId: Int): LocationEntity? {
        val asyncResult = doAsyncResult { mainDatabase.carDao().getCarLocation(locId) }
        return asyncResult.get()
    }

    fun getWallet(passengerId: Int): LiveData<WalletEntity> {
        return mainDatabase.passengerDao().getWalletById(passengerId)
    }

    fun getWallet(token: String, passengerId: Int): Single<WalletEntity> {
        return serverCommunicator.getWallet(token)
                .map(WalletEntityMappers(passengerId))
                .flatMap { walletEntity: WalletEntity ->
                    mainDatabase.passengerDao().insertWallet(walletEntity)
                    return@flatMap Single.just(walletEntity)
                }
                .compose(RxUtil.singleTransformer())
    }

    fun getBankAccount(): BankAccount? {
        val asyncResult = doAsyncResult { mainDatabase.bankDao().getBankAccount() }
        val result = asyncResult.get()
        return if (result.isNotEmpty()) result.first() else null
    }

    fun getDriver(driverId: Int): DriverEntity? {
        val asyncResult = doAsyncResult { mainDatabase.carDao().getCarDriver(driverId) }
        return asyncResult.get()
    }


    fun getCar(carId: Int): CarEntity? {
        val asyncResult = doAsyncResult { mainDatabase.carDao().getCar(carId) }
        val rr = asyncResult.get()
        Log.d("MAPPPEERR_REPO", "Car: ${rr?.id}  ${rr?.make}")
        return rr
    }


    fun deleteCar(carEntity: CarEntity) {
        doAsync { mainDatabase.carDao().delete(carEntity) }

    }


    fun getDriverId(carId: Int): Long {
        val asyncResult = doAsyncResult { mainDatabase.carDao().getDriverId(carId) }
        return asyncResult.get()
    }

    fun saveUserPoint(id: UserPointEntity.Companion.ID, position: LatLng, name: String) {
        async {
            mainDatabase.passengerDao().insertUserPointEntity(UserPointEntity(id.ordinal, position, name))
        }
    }

    fun getUserPoints() = mainDatabase.passengerDao().getUserPoints()

    fun getDirection(key: String,
                     listPoint: List<LatLng>): Single<DirectionsResponse> {
        var points = mutableListOf<LatLng>()
        points.addAll(listPoint)
        val parOriginal = String.format("%s,%s", points.first().latitude, points.first().longitude)
        val preDestination = String.format("%s,%s", points.last().latitude, points.last().longitude)
        points.removeAt(0)
        points.removeAt(points.size - 1)
        val parWayPoints: String = points.map {
            "${it.latitude},${it.longitude}"
        }.joinToString("|")
        return serverCommunicator.direction(key, parOriginal, preDestination, parWayPoints).compose(singleTransformer())
    }

    fun setDefaultCard(userId: Int, card: Card) {
        async {
            mainDatabase.cardDao().resetDefaults(userId)
            mainDatabase.cardDao().updateCard(card)
        }
    }

    fun deleteCard(card: Card) {
        async {
            val cards = mainDatabase.cardDao().getUserCardsSync(card.userId)
            if (cards.size > 1) {
                val cardNewDefault = cards.first { !it.defaultFlag }
                cardNewDefault.defaultFlag = true
                mainDatabase.transactionDao().deleteAndUpdateDefaultCard(cardNewDefault, card.id)
            } else {
                mainDatabase.cardDao().deleteCard(card.id)
            }
        }
    }
    fun getHelpList(apiKey: String, value: Int) = serverCommunicator.getHelpList(apiKey, value)

    fun addFavoriteAddress(apiKey: String, favoriteAddress: FavoriteAddress) =
            serverCommunicator.addAddress(apiKey, favoriteAddress).compose( singleTransformer())

    fun getFaQList(apiKey: String) = serverCommunicator.getFaQList(apiKey)

    fun getAppNews(amount: Int, offset: Int, apiKey: String) = serverCommunicator.appNews(amount,offset,apiKey)

    fun addFavoriteRoute(apiKey: String, contentType: String, favoriteRoute: FavoriteRoute) =
            serverCommunicator.addRoute(apiKey, contentType, favoriteRoute).compose(singleTransformer())

    fun sendHelpRequest(apiKey: String, contentType: String, help: HelpRequest) =
            serverCommunicator.helpRequest(apiKey, contentType, help)

    fun getFavoriteAddresses(apiKey: String, amount: Int, offset: Int) =
            serverCommunicator.getAddresses(apiKey, amount, offset)

    fun getFavoriteRoute(token: String, amount: Int, offset: Int) =
            serverCommunicator.getRoutes(token, amount, offset)

    fun editFavoriteAddress(apiKey: String, contentType: String, id: Int, favoriteAddress: FavoriteAddress) =
            serverCommunicator.editAddress(apiKey, contentType, id, favoriteAddress)

    fun deleteFavoriteAddress(apiKey: String, id: Int) =
            serverCommunicator.deleteAddress(apiKey, id)

    fun deleteCar(apiKey: String, id: Int) =
            serverCommunicator.deleteCar(apiKey, id)

    fun deleteFavoriteRoute(apiKey: String, id: Int) =
            serverCommunicator.deleteFavoriteRoute(apiKey, id)

    fun editFavoriteRoute(token: String, contentType: String, id: Int, favoriteRoute: FavoriteRoute) =
            serverCommunicator.editRoute(token, contentType, id, favoriteRoute)

    fun rateDriver(token: String, contentType: String, id: Int, rateDriver: RateDriver) =
            serverCommunicator.rateDriver(token, contentType, id, rateDriver)

    fun addBankAccountToDB(account: BankAccount) {
        doAsync {
            if (getBankAccount() != null) mainDatabase.bankDao().update(account) else mainDatabase.bankDao().insert(account)
        }
    }

    fun addBankAccount(token: String, jsonObject: JsonObject) = serverCommunicator.addBankAccount(token, jsonObject)


    fun setNewOrder(token: String, order: Order) = serverCommunicator.sendNewOrder(token, order)

    fun paymentWithDraw(token: String, jsonObject: JsonObject) = serverCommunicator.paymentWithDraw(token, jsonObject)

    fun getListCarMake(): Call<ResponseBody> {
        return serverCommunicator.getListCarMake()
    }

    fun getAboutText(apiKey: String) = serverCommunicator.getAboutText(apiKey)

    fun getListCarModel(makeCar: String): Call<ResponseBody> {
        return serverCommunicator.getListCarModel(makeCar)
    }



}