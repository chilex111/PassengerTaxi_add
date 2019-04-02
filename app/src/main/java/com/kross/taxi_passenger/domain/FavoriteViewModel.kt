package com.kross.taxi_passenger.domain

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.maps.model.LatLng
import com.kross.taxi_passenger.data.repository.OrderRepository
import com.kross.taxi_passenger.data.repository.Repository
import com.kross.taxi_passenger.data.repository.server.pojo.FavoriteAddress
import com.kross.taxi_passenger.data.repository.server.pojo.FavoriteRoute
import com.kross.taxi_passenger.data.repository.server.pojo.KrossRoute
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.presentation.widget.SingleLiveEvent
import com.kross.taxi_passenger.utils.MapHelper
import com.kross.taxi_passenger.utils.Stub
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign


//TODO refactor responces
class FavoriteViewModel(application: Application, val repository: Repository, val geoDataClient: GeoDataClient, val orderRepository: OrderRepository) : BaseViewModel(application) {

    val disposables = CompositeDisposable()
    private val loadingLiveData = SingleLiveEvent<Boolean>()
    private val isSuccessLiveData = SingleLiveEvent<Boolean>()
    private val favoriteAddressesLiveData = MutableLiveData<List<FavoriteAddress>>()
    private val favoriteRoutesLiveData = MutableLiveData<List<KrossRoute>>()
    val errorLiveData = SingleLiveEvent<String>()
    val progressLiveData = SingleLiveEvent<Boolean>()

    val addFavoriteAddressListener = MutableLiveData<Stub>()
    val addFavoriteRouteListener = MutableLiveData<Stub>()

    @SuppressLint("CheckResult")
    fun getDirection(mapsKey: String, waypoints: List<LatLng>,
                     onDirections: (Pair<Int, String>) -> Unit, onError: (String?) -> Unit) {
        repository
                .getDirection(mapsKey, waypoints)
                .doOnSubscribe { loadingLiveData.value = true }
                .doAfterTerminate { loadingLiveData.value = false }
                .subscribe(
                        {
                            onDirections.invoke(Pair(it.routes[0].getTotalDistance(), MapHelper.buildFullPolyline(it)))
                        },
                        {
                            it.printStackTrace()
                            onError.invoke(it.message)
                        })

    }

    //  fun insertOrder{}

    fun insertOrder(id: Int = 0,
                    carType: Int = 0,
                    date: String? = null,
                    comment: String = "",
                    orderFor: String? = null,
                    costRange: String = "",
                    paymentType: Int = 2,
                    cardNumber: String? = null,
                    polyline: String = "",
                    distance: Int,
                    orderStatus: Int = 1,
                    listRoutePoint: List<StubWaypointAddress>,
                    priority: Int = 0) {
        orderRepository.insertOrder(id, carType, date, comment, orderFor, costRange, paymentType, cardNumber, polyline, distance, orderStatus, listRoutePoint, priority)
    }

    private fun Completable.completableResponce() {
        this.subscribe({
            progressLiveData.value = false
        }, {
            errorLiveData.value = it.message
            it.printStackTrace()
            progressLiveData.value = false
        })
                .apply { disposables.add(this) }
    }


    // TODO Move to pagination architecture component
    fun addFavoriteAddress(favoriteAddress: FavoriteAddress, listener: (() -> Unit)) {
        progressLiveData.value = true
        repository.addFavoriteAddress(token, favoriteAddress).subscribe({
            progressLiveData.value = false
            listener.invoke()
        }, {
            errorLiveData.value = it.message
            it.printStackTrace()
            progressLiveData.value = false
        })
                .apply { disposables.add(this) }

    }


    // TODO Refactor! move id check up to fragment maybe
    fun editFavoriteAddress(favoriteAddress: FavoriteAddress, listener: () -> Unit) {
        val id = favoriteAddress.id ?: return

        progressLiveData.value = true
        repository.editFavoriteAddress(token, contentType, id, favoriteAddress).completableResponce()
    }

    fun deleteFavoriteAddress(id: Int, listener: () -> Unit) {
        progressLiveData.value = true
        repository.deleteFavoriteAddress(token, id).completableResponce()
    }

    fun addFavoriteRoute(favoriteRoute: FavoriteRoute, listener: () -> Unit) {
        progressLiveData.value = true
        repository.addFavoriteRoute(token, contentType, favoriteRoute)
                .subscribe({
                    progressLiveData.value = false
                    listener.invoke()
                }, {
                    errorLiveData.value = it.message
                    it.printStackTrace()
                    progressLiveData.value = false
                })
                .apply { disposables.add(this) }
    }

    fun getFavoriteAddresses(amount: Int, offset: Int): LiveData<List<FavoriteAddress>> {
        progressLiveData.value = true
        repository
                .getFavoriteAddresses(token, amount, offset)
                .subscribe(
                        {
                            favoriteAddressesLiveData.value = it.addresses
                            progressLiveData.value = false
                        },
                        {
                            errorLiveData.value = it.message
                            it.printStackTrace()
                            progressLiveData.value = false
                        })
                .let { disposables.add(it) }

        return favoriteAddressesLiveData
    }

    fun getFavoriteRoutes(amount: Int, offset: Int): LiveData<List<KrossRoute>> {
        repository
                .getFavoriteRoute(token, amount, offset)
                .subscribe(
                        { favoriteRoutesLiveData.value = it },
                        {
                            errorLiveData.value = it.message
                            it.printStackTrace()
                        })
                .let { disposables.add(it) }

        return favoriteRoutesLiveData
    }

    fun notifyAddFavoriteAddressClick() {
        addFavoriteAddressListener.value = Stub()
    }

    fun notifyAddFavoriteRouteClick() {
        addFavoriteRouteListener.value = Stub()
    }

    override fun onCleared() {
        super.onCleared()

        disposables.clear()
    }

    fun getPolyline(mapsKey: String, waypoints: List<LatLng>, onPolyline: (Pair<Int, String>) -> Unit,
                    onError: (String?) -> Unit) =
            repository
                    .getDirection(mapsKey, waypoints)
                    .subscribe(
                            {
                                onPolyline.invoke(Pair(it.routes[0].getTotalDistance(), MapHelper.buildFullPolyline(it)))
                            },
                            {
                                it.printStackTrace()
                                onError.invoke(it.message)
                            })
                    .let { disposables += it }

    fun deleteFavoriteRoute(id: Int) {
        progressLiveData.value = true
        repository.deleteFavoriteRoute(token, id).completableResponce()
    }

    fun editFavoriteRoute(id: Int, favoriteRoute: FavoriteRoute, listener: () -> Unit) {
        repository
                .editFavoriteRoute(token, contentType, id, favoriteRoute).completableResponce()
    }
}