package com.kross.taxi_passenger.data.repository.server

import com.google.gson.JsonObject
import com.kross.taxi_passenger.data.repository.database.entity.ExampleEntity
import com.kross.taxi_passenger.data.repository.server.pojo.FavoriteAddress
import com.kross.taxi_passenger.data.repository.server.pojo.FavoriteRoute
import com.kross.taxi_passenger.data.repository.server.pojo.GetFavoritAddressResponse
import com.kross.taxi_passenger.data.repository.server.pojo.KrossRoute
import com.kross.taxi_passenger.data.repository.server.pojo.request.*
import com.kross.taxi_passenger.data.repository.server.pojo.response.*
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getMakes")
    fun getListCarMake(): Call<ResponseBody>

    @GET("https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getModels")
    fun getListCarModel(@Query("make") makeCar: String): Call<ResponseBody>

    @POST("email.verify")
    fun emailVerify(@Header("Content-Type") contentType: String,
                    @Header("Authorization") apiKey: String,
                    @Body verify: EmailVerifyBody): Single<Response<ResponseBody>>

    @POST("passenger/help.request")
    fun helpRequest(@Header("Authorization") apiKey: String,
                    @Header("Content-Type") contentType: String,
                    @Body routesRequest: HelpRequest): Completable


    @GET("/all")
    fun getAll(): Single<List<ExampleEntity>>

    @POST("auth/confirm")
    fun phoneConfirm(@Header("Content-Type") contentType: String,
                     @Header("Authorization") apiKey: String,
                     @Header("Cache-Control") cacheControl: String,
                     @Body jsonObject: JsonObject): Single<PhoneConfirmModel>

    @PUT("https://api-dev.kross.taxi/api/v1/passenger/driver.release/{driver_id}/{car_id}")
    fun driverRelease(@Header("Authorization") apiKey: String,
                      @Path("driver_id") driverId: Int,
                      @Path("car_id") carId: Int): Single<Response<ResponseBody>>

    @POST("https://api-dev.kross.taxi/api/v1/passenger/driver.proposal/{driver_id}/{car_id}")
    fun assignDriver(@Header("Authorization") apiKey: String,
                     @Path("driver_id") driverId: Int,
                     @Path("car_id") carId: Int): Single<Response<ResponseBody>>

    @DELETE("passenger/car.delete/{id}")
    fun deleteCar(@Header("Authorization") apiKey: String,
                  @Path("id") id: Int): Completable


    @POST("passenger/registration")
    fun registration(@Header("Authorization") apiKey: String,
                     @Body registration: Registration): Single<UserId>

    @POST("authorization")
    fun authorization(@Header("Content-Type") contentType: String,
                      @Header("Authorization") apiKey: String,
                      @Body authorization: Authorization): Single<Token>


    @PUT("password.recovery")
    fun passwordRecovery(@Header("Content-Type") contentType: String,
                         @Header("Authorization") apiKey: String,
                         @Body jsonObject: JsonObject): Single<Response<ResponseBody>>

    @Multipart
    @POST("passenger/become.owner")
    fun becomeOwner(//@Header("Content-Type") contentType: String,
            @Header("Authorization") token: String,
            @Part multipartBody: List<MultipartBody.Part>): Single<Response<ResponseBody>>



    @Multipart
    @POST("passenger/profile.edit")
    fun profileEdit(@Header("Authorization") token: String,
                    @Part multipartBody: List<MultipartBody.Part>): Single<Response<ResponseBody>>


    @Multipart
    @POST("passenger/car.add")
    fun addCar(//@Header("Content-Type") contentType: String,
                    @Header("Authorization") token: String,
                    @Part multipartBody: List<MultipartBody.Part>): Single<AddCarResponce>

    @GET("passenger/profile.info")
    fun getProfileInfo(@Header("Content-Type") contentType: String,
                       @Header("Authorization") token: String): Single<ProfileInfo>


    @GET("passenger/car.statistic/{id}")
    fun getCarStatistic(@Header("Authorization") token: String,
                        @Path("id") id: Int,
                        @Query("date_from") dateStart: String,
                        @Query("date_to") dateEnd: String): Single<Cars>


    @GET("passenger/order.info/{id}")
    fun getOrderInfo(@Header("Authorization") token: String,
                     @Path("id") id: Int): Single<OrderInfo>

    @GET("passenger/wallet")
    fun getWallet(@Header("Authorization") token: String): Single<Wallet>

    @GET("https://maps.googleapis.com/maps/api/directions/json")
    fun directions(
            @Query("key") key: String,
            @Query("origin") origin: String,
            @Query("destination") destination: String,
            @Query("waypoints") waypoints: String): Single<DirectionsResponse>


    @POST("passenger/imageView.add")
    fun addAddress(@Header("Authorization") apiKey: String,
                   @Body addressRequest: FavoriteAddress): Single<FavoriteAdressResponce>

    @PUT("passenger/imageView.edit/{id}")
    fun editAddress(@Header("Authorization") apiKey: String,
                    @Header("Content-Type") contentType: String,
                    @Path("id") id: Int,
                    @Body addressRequest: FavoriteAddress): Completable

    @DELETE("passenger/imageView.delete/{id}")
    fun deleteAddress(@Header("Authorization") apiKey: String,
                      @Path("id") id: Int): Completable

    @POST("passenger/route.add")
    fun addRoute(@Header("Authorization") apiKey: String,
                 @Header("Content-Type") contentType: String,
                 @Body routesRequest: FavoriteRoute):  Single<FavoriteRouteResponce>

    @GET("passenger/imageView.list")
    fun getAddresses(@Header("Authorization") apiKey: String,
                     @Query("amount") amount: Int,
                     @Query("offset") offset: Int): Single<GetFavoritAddressResponse>

    @GET("passenger/notification.list")
    fun getNotificationsList(@Header("Authorization") apiKey: String,
                             @Query("amount") amount: Int): Single<GetFavoritAddressResponse>

    @GET("passenger/route.list")
    fun getRoutes(@Header("Authorization") apiKey: String,
                  @Query("amount") amount: Int,
                  @Query("offset") offset: Int): Single<List<KrossRoute>>



    @GET("passenger/cars.list")
    fun getCars(@Header("Authorization") apiKey: String): Single<CarsList>

    @DELETE("passenger/route.delete/{id}")
    fun deleteRoute(@Header("Authorization") apiKey: String,
                    @Path("id") id: Int): Completable

    // TODO Fix after BE changes
    @PUT("passenger/route.edit/{id}")
    fun editRoute(@Header("Authorization") apiKey: String,
                  @Header("Content-Type") contentType: String,
                  @Path("id") id: Int,
                  @Body favoriteRoute: FavoriteRoute): Completable


    @POST("https://api-dev.kross.taxi/api/v1/passenger/order.rate/{id}")
    fun rateDriver(@Header("Authorization") apiKey: String,
                  @Header("Content-Type") contentType: String,
                  @Path("id") id: Int,
                  @Body rate: RateDriver): Completable

    @PUT("passenger/pass.change")
    fun changePassword(@Header("Content-Type") contentType: String,
                       @Header("Authorization") apiKey: String,
                       @Body jsonObject: JsonObject): Single<Response<ResponseBody>>


    @PUT("passenger/token")
    fun putPushToken(@Header("Content-Type") contentType: String,
                     @Header("Authorization") apiKey: String,
                     @Body pushToken: TokenRequest): Single<Response<ResponseBody>>

    @POST("passenger/order.add")
    fun sendNewOrder(@Header("Authorization") token: String,
                     @Body order: Order): Single<OrderId>

    @POST("passenger/bank.add")
    fun addBankAccount(@Header("Authorization") apiKey: String,
                       @Body jsonObject: JsonObject): Completable

    @POST("passenger/payment.withdraw")
    fun paymentWithDraw(@Header("Authorization") apiKey: String,
                        @Body jsonObject: JsonObject): Single<Response<ResponseBody>>

    @GET("/passenger/about")
    fun getInfoAboutApp(@Header("Authorization") apiKey: String): Single<Response<ResponseBody>>



    @GET("https://api-dev.kross.taxi/api/v1/passenger/driver.search")
    fun getDrivers(@Header("Authorization") apiKey: String,
                   @Query("amount") amount: Int): Single<DriversList>


    @GET("https://api-dev.kross.taxi/api/v1/passenger/driver.info/{id}")
    fun getDriverByIdFromServer(@Header("Authorization") apiKey: String,
                                @Path("id") id: Int,
                                @Query("amount") amount: Int): Single<Driver>

    @GET("https://api-dev.kross.taxi/api/v1/passenger/car.trips/{id}")
    fun getCarTripsStatistic(@Header("Authorization") apiKey: String,
                                @Path("id") id: Int,
                                @Query("date") date: String): Single<TripsStatistic>

    @GET("https://api-dev.kross.taxi/api/v1/passenger/news.list?")
    fun getAppNews(@Header("Authorization") apiKey: String,
                   @Query("amount") amount: Int,
                   @Query("offset") offset: Int): Single<News>

    @GET("https://api-dev.kross.taxi/api/v1/passenger/help.list?")
    fun getHelpList(@Header("Authorization") apiKey: String,
                   @Query("type") value: Int): Single<List<HelpList>>

    @GET("https://api-dev.kross.taxi/api/v1/passenger/about")
    fun getAboutText(@Header("Authorization") apiKey: String): Single<About>


    @GET("https://api-dev.kross.taxi/api/v1/passenger/faq.list")
    fun getFaQList(@Header("Authorization") apiKey: String): Single<List<FAQ>>


    @GET("https://api-dev.kross.taxi/api/v1/passenger/car.chart/{id}")
    fun getCarChart(@Header("Authorization") apiKey: String,
                    @Path("id") id: Int, @Query("month") month: String): Single<Response<Map<String, Int>>>

    @GET("https://api-dev.kross.taxi/api/v1/passenger/faq.list")
    fun getFAQ(@Header("Authorization") apiKey: String): Single<Response<ResponseBody>>

}