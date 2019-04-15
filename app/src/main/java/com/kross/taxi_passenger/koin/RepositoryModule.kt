package com.kross.taxi_passenger.koin

import android.arch.persistence.room.Room
import android.content.Context
import android.content.IntentFilter
import android.location.LocationManager
import android.net.ConnectivityManager
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.android.gms.location.places.Places
import com.google.gson.GsonBuilder
import com.kross.taxi_passenger.BuildConfig
import com.kross.taxi_passenger.data.repository.OrderRepository
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.database.Database
import com.kross.taxi_passenger.data.repository.server.ApiService
import com.kross.taxi_passenger.data.repository.server.ServerCommunicator
import com.kross.taxi_passenger.data.repository.server.SocketCommunicator
import com.kross.taxi_passenger.managers.NetworkChangeReceiver
import com.kross.taxi_passenger.managers.ProviderChangeReceiver
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

val repositoryModule: Module = module {
    single { provideDatabase(androidApplication().applicationContext) }
    single { provideServerCommunicator() }
    single { provideSharedPrefs(androidApplication().applicationContext) }
    single { provideNetworkChangeReceiver(androidApplication().applicationContext) }
    single { provideProviderChangeReceiver(androidApplication()) }
    single { provideGeoDataClient(get()) }

    single { Repository(get(), get(), get()) }
    single { OrderRepository(get(), get())}
    single { provideSocketCommunicator(androidApplication().applicationContext, get()) }
}

private fun provideGeoDataClient(context: Context) = Places.getGeoDataClient(context)

private fun provideDatabase(context: Context) = Room.databaseBuilder(context, Database::class.java, "kroos-taxi-passenger-database")
        .fallbackToDestructiveMigration()
        .build()

private fun provideSharedPrefs(context: Context) = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
private fun provideSocketCommunicator(context: Context, orderRepository: OrderRepository) = SocketCommunicator(context, orderRepository)

private const val API_URL = "https://driver-dev.kross.app/api/v1/"
private fun provideServerCommunicator(): ServerCommunicator {
    val okHttpClientBuilder = OkHttpClient.Builder()
            .connectionPool(ConnectionPool(5, 60, TimeUnit.SECONDS))
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)

    if (BuildConfig.DEBUG) {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        okHttpClientBuilder.addInterceptor(httpLoggingInterceptor)
    }

    val retrofitBuilder = Retrofit.Builder()
            .client(okHttpClientBuilder
                    .addNetworkInterceptor(StethoInterceptor())
                    .build())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(getGson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

    val retrofit = retrofitBuilder.baseUrl(API_URL).build()
    val apiService = retrofit.create<ApiService>(ApiService::class.java)
    return ServerCommunicator(apiService)
}

private fun getGson() = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

private fun provideNetworkChangeReceiver(context: Context) = NetworkChangeReceiver()
        .apply {
            context.registerReceiver(this, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }


private fun provideProviderChangeReceiver(context: Context) = ProviderChangeReceiver()
        .apply {
            context.registerReceiver(this, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
        }

