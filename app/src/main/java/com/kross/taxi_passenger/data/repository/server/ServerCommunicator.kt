package com.kross.taxi_passenger.data.repository.server

import com.google.gson.JsonObject
import com.kross.taxi_passenger.data.repository.database.entity.ExampleEntity
import com.kross.taxi_passenger.data.repository.server.pojo.FavoriteAddress
import com.kross.taxi_passenger.data.repository.server.pojo.FavoriteRoute
import com.kross.taxi_passenger.data.repository.server.pojo.request.*
import com.kross.taxi_passenger.data.repository.server.pojo.response.*
import com.kross.taxi_passenger.utils.RxUtil.completableTransformer
import com.kross.taxi_passenger.utils.RxUtil.singleTransformer
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

// TODO: move apiKey to apiService

class ServerCommunicator(private val apiService: ApiService) {


    fun getAll(): Single<List<ExampleEntity>> = apiService.getAll().compose(singleTransformer())

    fun phoneConfirm(contentType: String,
                     apiKey: String,
                     cacheControl: String,
                     jsonObject: JsonObject): Single<PhoneConfirm> =
            apiService
                    .phoneConfirm(contentType, apiKey, cacheControl, jsonObject)
                    .compose(singleTransformer())

    fun registration(apiKey: String,
                     registration: Registration): Single<UserId> =
            apiService
                    .registration(apiKey, registration)
                    .compose(singleTransformer())

    fun authorization(contentType: String,
                      apiKey: String,
                      authorization: Authorization): Single<Token> =
            apiService
                    .authorization(contentType, apiKey, authorization)
                    .compose(singleTransformer())

   fun emailVerify(contentType: String,
                      apiKey: String,
                      verify: EmailVerifyBody): Single<Response<ResponseBody>> =
            apiService
                    .emailVerify(contentType, apiKey, verify)
                    .compose(singleTransformer())


    fun becomeOwner(contentType: String,
                    apiKey: String,
                    multipartBody: List<MultipartBody.Part>): Single<Response<ResponseBody>> =
            apiService
                    .becomeOwner(apiKey, multipartBody)
                    .compose(singleTransformer())


  fun profileEdit(contentType: String,
                    apiKey: String,
                    multipartBody: List<MultipartBody.Part>): Single<Response<ResponseBody>> =
            apiService
                    .profileEdit(apiKey, multipartBody)
                    .compose(singleTransformer())


    fun addCar(contentType: String,
               apiKey: String,
               multipartBody: List<MultipartBody.Part>): Single<AddCarResponce> =
            apiService
                    .addCar(apiKey, multipartBody)
                    .compose(singleTransformer())

    fun passwordRecovery(contentType: String,
                         apiKey: String,
                         jsonObject: JsonObject): Single<Response<ResponseBody>> =
            apiService
                    .passwordRecovery(contentType, apiKey, jsonObject)
                    .compose(singleTransformer())


    fun releaseDriver(apiKey: String,
                     driverId: Int,
                     carId: Int): Single<Response<ResponseBody>> =
            apiService
                    .driverRelease( apiKey, driverId, carId)
                    .compose(singleTransformer())


  fun assignDriver(apiKey: String,
                     driverId: Int,
                     carId: Int): Single<Response<ResponseBody>> =
            apiService
                    .assignDriver( apiKey, driverId, carId)
                    .compose(singleTransformer())


    fun getProfileInfo(contentType: String,
                       token: String): Single<ProfileInfo> =
            apiService
                    .getProfileInfo(contentType, token)

    fun getOrderInfo(contentType: String,
                     orderId: Int): Single<OrderInfo> =
            apiService
                    .getOrderInfo(contentType, orderId).compose(singleTransformer())

    fun getWallet(token: String): Single<Wallet> =
            apiService
                    .getWallet(token)

    fun getListCarMake(): Call<ResponseBody> = apiService.getListCarMake()


    fun getAboutText(apiKey: String): Single<About> = apiService.getAboutText(apiKey).compose(singleTransformer())

    fun getFaQList(apiKey: String): Single<List<FAQ>> = apiService.getFaQList(apiKey).compose(singleTransformer())

  fun getHelpList(apiKey: String, value: Int):
          Single<List<HelpList>> = apiService.getHelpList(apiKey,value).compose(singleTransformer())

    fun getListCarModel(makeCar: String): Call<ResponseBody> = apiService.getListCarModel(makeCar)

    fun direction(key: String, origin: String, destination: String, waypoints: String) =
            apiService
                    .directions(key, origin, destination, waypoints)


  fun addAddress(apiKey: String, address: FavoriteAddress) =
          apiService
                  .addAddress(apiKey, address)


  fun editAddress(apiKey: String, contentType: String, id: Int, address: FavoriteAddress) =
          apiService
                  .editAddress(apiKey, contentType, id, address)
                  .compose(completableTransformer())

  fun deleteAddress(apiKey: String, id: Int) =
          apiService
                  .deleteAddress(apiKey, id)
                  .compose(completableTransformer())

  fun addRoute(apiKey: String, contentType: String, route: FavoriteRoute) =
          apiService
                  .addRoute(apiKey, contentType, route)




  fun deleteCar(apiKey: String, id: Int) =
            apiService
                    .deleteCar(apiKey, id)
                    .compose(completableTransformer())



    fun helpRequest(apiKey: String, contentType: String, help: HelpRequest) =
            apiService
                    .helpRequest(apiKey, contentType, help)
                    .compose(completableTransformer())

  fun getAddresses(apiKey: String, amount: Int, offset: Int) =
          apiService
                  .getAddresses(apiKey, amount, offset)
                  .compose(singleTransformer())

  fun getRoutes(apiKey: String, amount: Int, offset: Int) =
          apiService
                  .getRoutes(apiKey, amount, offset)
                  .compose(singleTransformer())


    fun getCars(apiKey: String) = apiService.getCars(apiKey) .compose(singleTransformer())

    fun getDrivers(apiKey: String, amount: Int) = apiService.getDrivers(apiKey, amount)


    fun getDriverByIdFomServer(apiKey: String, id: Int, amount: Int) = apiService.getDriverByIdFromServer(apiKey, id, amount)


    fun getCarTripsStatistic(apiKey: String, id: Int, date: String) = apiService.getCarTripsStatistic(apiKey, id, date)

    fun getCarStatistic(apiKey: String, id: Int, dateStart: String, dateEnd: String) = apiService.getCarStatistic(apiKey, id, dateStart, dateEnd)


    fun getCarChart(apiKey: String, id: Int, dateStart: String) = apiService.getCarChart(apiKey, id, dateStart).compose(singleTransformer())

    fun deleteFavoriteRoute(apiKey: String, id: Int) =
            apiService
                    .deleteRoute(apiKey, id)
                    .compose(completableTransformer())

    fun editRoute(apiKey: String, contentType: String, id: Int, favoriteRoute: FavoriteRoute) =
            apiService
                    .editRoute(apiKey, contentType, id, favoriteRoute)
                    .compose(completableTransformer())


    fun rateDriver(apiKey: String, contentType: String, id: Int, rate: RateDriver) =
            apiService
                    .rateDriver(apiKey, contentType, id, rate)
                    .compose(completableTransformer())

    fun changePassword(contentType: String,
                       apiKey: String,
                       jsonObject: JsonObject): Single<Response<ResponseBody>> =
            apiService
                    .changePassword(contentType, apiKey, jsonObject)
                    .compose(singleTransformer())

    fun putPushToken(contentType: String,
                     apiKey: String,
                     pushTokenRequest: TokenRequest): Single<Response<ResponseBody>> =
            apiService
                    .putPushToken(contentType, apiKey, pushTokenRequest)
                    .compose(singleTransformer())


    fun sendNewOrder(token: String, order: Order) = apiService.sendNewOrder(token, order).compose(singleTransformer())

    fun addBankAccount(apiKey: String, jsonObject: JsonObject) =
            apiService
                    .addBankAccount(apiKey, jsonObject)
                    .compose(completableTransformer())

    fun paymentWithDraw(apiKey: String, jsonObject: JsonObject): Single<Response<ResponseBody>> =
            apiService
                    .paymentWithDraw(apiKey, jsonObject)
                    .compose(singleTransformer())

    fun aboutAppInfo(apiKey: String): Single<Response<ResponseBody>> = apiService.getInfoAboutApp(apiKey)

    fun appNews(amount: Int, offset: Int, apiKey: String):
            Single<News> = apiService.getAppNews(apiKey, amount, offset)
            .compose(singleTransformer())

    //un FAQ(apiKey: String): Single<Response<ResponseBody>> = apiService.getFAQLo(apiKey)

}