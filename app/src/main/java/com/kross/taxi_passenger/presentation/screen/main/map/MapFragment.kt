package com.kross.taxi_passenger.presentation.screen.main.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import com.fondesa.kpermissions.extension.onAccepted
import com.fondesa.kpermissions.extension.onDenied
import com.fondesa.kpermissions.extension.onPermanentlyDenied
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.google.maps.android.ui.IconGenerator
import com.kross.taxi_passenger.MyFirebaseMessagingService
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.DriverEntity
import com.kross.taxi_passenger.data.repository.database.entity.OrderEntity
import com.kross.taxi_passenger.data.repository.server.SocketCommunicator
import com.kross.taxi_passenger.data.repository.server.pojo.request.Order
import com.kross.taxi_passenger.data.repository.server.pojo.request.Route
import com.kross.taxi_passenger.data.repository.server.pojo.request.RoutePoint
import com.kross.taxi_passenger.data.repository.server.pojo.request.TokenRequest
import com.kross.taxi_passenger.domain.MapViewModel
import com.kross.taxi_passenger.domain.entity.PaymentMenthod
import com.kross.taxi_passenger.domain.entity.PointState
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.managers.LastPointManager
import com.kross.taxi_passenger.presentation.dialog.BottomDialogChoseTripOwner
import com.kross.taxi_passenger.presentation.screen.cabinet.CabinetActivity
import com.kross.taxi_passenger.presentation.screen.leave_note_and_passenger_number.NoteAndNumberActivity
import com.kross.taxi_passenger.presentation.screen.main.MainActivity
import com.kross.taxi_passenger.presentation.screen.main.bottom.*
import com.kross.taxi_passenger.presentation.screen.payment.choose.ChoosePaymentActivity
import com.kross.taxi_passenger.presentation.screen.time_trip.SetTimeTripActivity
import com.kross.taxi_passenger.presentation.screen.trip_details.TripDetailsActivity
import com.kross.taxi_passenger.presentation.screen.trip_finished.TripFinishedActivity
import com.kross.taxi_passenger.presentation.screen.waypoints.WaypointsActivity
import com.kross.taxi_passenger.utils.*
import com.sano.testdrive.main.BaseMapFragment
import io.reactivex.functions.Action
import kotlinx.android.synthetic.main.bottom_dialog_trip_cost.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.pin_waypoint.view.*
import org.jetbrains.anko.runOnUiThread
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

// TODO Refactor this
class MapFragment : BaseMapFragment(), BottomDialogChoseTripOwner.OnClick, View.OnClickListener, SocketCommunicator.EmitsListenerFull {
    override fun orderOnTrip(orderId: String) {
        viewModel.updateOrderStatus(orderId.toInt(), State.TRIP_ON.status)
        handleOrderState()
    }

    override fun driverNotFound(orderId: Int) {
        viewModel.updateOrderStatus(orderId, State.TAXI_NOT_FOUND.status)
        Timer("SettingUp5sdfb", false).schedule(500) {
            handleOrderState()
        }
    }

    override fun checkStatus(status: Int) {
        Log.d("STATUS: ", " $status")
    }

    override fun getLocation(latLng: LatLng) {
        if (readyKey) delayedPositionChanged(latLng)
    }

    override fun driverDecline(orderId: String) {
        viewModel.updateOrderStatus(orderId.toInt(), State.TRIP_FINISHED.status)

        Handler().postDelayed({
            handleOrderState()
            context?.runOnUiThread {
                activity?.let { AlertDialogHelper.createDialog(it, R.string.driver_cancel_title, R.string.driver_cancel_text, "", "Ок") }
                googleMap.clear()
                myLocationMakerOn()
            }
        }, 500)
    }

    override fun orderApprove(orderId: String) {
        viewModel.updateOrderStatus(orderId.toInt(), State.TAXI_FOUND.status)
        //  Timer("SettingUp5sdfb", false).schedule(500) {
        handleOrderState()
        //  }
        context?.runOnUiThread {
            Handler().postDelayed({
                order?.let {
                    onMapStateChanged(it)
                    Log.d("MAPPPEERR_NOO11", "Car: ${driver?.id} ${driver?.firstName}")
                }
            }, 800)
        }
    }

    override fun driverArrived(orderId: String) {
        context?.saveToSharedPreference(R.string.arrived_key, Calendar.getInstance().timeInMillis)
        viewModel.updateOrderStatus(orderId.toInt(), State.DRIVER_ARRIVED.status)
        //    handleOrderState()

        Handler().postDelayed({
            handleOrderState()
        }, 500)
    }


    override fun receiveErrorHandling(error: Triple<String, String, String>) {
        Log.d("Socket Communicator: ", "errror")
    }


    override fun finishOrder(orderId: String) {
        handleOrderState()
        stopDriving {}
        tripFinished()
    }


    enum class Letter(val value: Int) {
        letterA(0),
        letterB(1),
        letterC(2),
        letterD(3);

        companion object {
            fun from(findValue: Int): Letter = Letter.values().first { it.value == findValue }
        }

        var letter: String = "A"
            get() {
                return when (this) {
                    letterA -> "A"
                    letterB -> "B"
                    letterC -> "C"
                    letterD -> "D"
                }
            }
    }

    companion object {

        const val CHOOSE_PAYMENT_REQUEST_CODE: Int = 11204
        const val CHOOSE_DATE_TRIP_REQUEST_CODE: Int = 11205
        const val CHOOSE_NUMBER_REQUEST_CODE: Int = 11206
        const val LEAVE_NOTE_REQUEST_CODE: Int = 11207
        const val PICK_CONTACT_REQUEST_CODE: Int = 11208
        const val TRIP_CANCELED_REQUEST_CODE: Int = 11209

        fun newInstance(type: Int): MapFragment {
            val fragment = MapFragment()
            val bundle = Bundle()
            bundle.putSerializable("TYPE", type)
            fragment.arguments = bundle
            return fragment
        }

    }


    private lateinit var marker: Marker
    private lateinit var myAddress: StubWaypointAddress
    private lateinit var googleMap: GoogleMap
    private var dialogTripOn: TripOnBottomDialog? = null
    private lateinit var dialogSetTripOwn: PassengerBottomDialog
    private lateinit var tripCostDialog: TripCostBottomDialog
    private var markers = mutableListOf<Marker>()
    private var handler: Handler = Handler()
    private var pushType = -1
    private var isDriving: Boolean = false
    private var readyKey = false

    private val viewModel: MapViewModel by viewModel()
    private var orderId: Int = 0
    private var order: OrderEntity? = null

    private var rout: Route? = null
    override fun getLayout() = R.layout.fragment_main


    val NOT_FOUND_STATUS_PUSH = 5
    val DRIVER_DECLINE_STATUS_PUSH = 4
    val ARRIVED_STATUS_PUSH = 3
    val DRIVER_FOUND_STATUS_PUSH = 2
    val FINISH_STATUS_PUSH = 8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let { (it as MainActivity).socketCommunicator.setEmitsListenerFull(this) }
        //  ordersId()
    }


    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { viewModel.sendPushToken("application/json", context!!.getStringPreference(R.string.token), TokenRequest(context!!.getStringPreference(R.string.PUSH_TOKEN))) }
        val args = arguments
        pushType = args?.getInt(getString(R.string.PUSH_TYPE), -1)!!
        setListenersViews()
        handleLoadingLiveData()
        handleOrderState()
        viewModel.checkStatus(null)
        listenFinish()
        if (order?.status() != State.TRIP_FINISHED && order?.status() != State.TRIP_FINISHED && order?.status() != State.DEFAULT) {
            viewModel.openConnection()
        }
    }

    private fun listenFinish() {
        MyFirebaseMessagingService.liveDataNotificationEvent.observe(this, Observer { it ->
            when (it) {
                DRIVER_FOUND_STATUS_PUSH -> {
//                    Log.d("Socket Communicator:", "order approve  push $it")
//                         viewModel.updateOrderStatus(orderId, State.TAXI_FOUND.status)
//                    handleOrderState()
//                    Timer("SettingUp63", false).schedule(1500) {
//                        //                        handleOrderState()
//                        handleOrderState()
//                    }
                }
                FINISH_STATUS_PUSH -> {
                    viewModel.updateOrderStatus(orderId, State.TRIP_FINISHED.status)
                    tripFinished()
                    stopDriving { }
                    Timer("SettingUp6", false).schedule(500) {
                    }
                }
                NOT_FOUND_STATUS_PUSH -> {
                    viewModel.updateOrderStatus(orderId, State.TAXI_NOT_FOUND.status)
                    Timer("SettingUp44", false).schedule(500) {
                        handleOrderState()
                    }
                }
                DRIVER_DECLINE_STATUS_PUSH -> Handler().postDelayed({
                    activity?.let { AlertDialogHelper.createDialog(it, R.string.driver_cancel_title, R.string.driver_cancel_text, "", "Ок") }
                    googleMap.clear()
                    myLocationMakerOn()
                }, 500)
                ARRIVED_STATUS_PUSH -> activity?.let { AlertDialogHelper.createDialog(it, R.string.driver_arrive_title, R.string.driver_arrive_text, "", "Ок") }
            }
        })
    }


    private fun handleLoadingLiveData() {
        viewModel.getLoadingLiveData().observe(this, Observer {
            it?.let { flagLoading -> progressBar.visibleOrGone(flagLoading) }
        })
    }


    private fun updateStatus(id: Int) {
        if (pushType == NOT_FOUND_STATUS_PUSH) {
            viewModel.updateOrderStatus(orderId, State.TAXI_NOT_FOUND.status)
            handleOrderState()
            return
        }

        if (pushType == DRIVER_DECLINE_STATUS_PUSH) {
            Handler().postDelayed({
                context?.runOnUiThread {
                    activity?.let { AlertDialogHelper.createDialog(it, R.string.driver_cancel_title, R.string.driver_cancel_text, "", "Ок") }
                    googleMap.clear()
                    myLocationMakerOn()
                }
            }, 500)
            return
        }
        if (pushType == ARRIVED_STATUS_PUSH) {
            activity?.let { AlertDialogHelper.createDialog(it, R.string.driver_arrive_title, R.string.driver_arrive_text, "", "Ок") }
            return
        }
        viewModel.updateOrder(context!!.getStringPreference(R.string.token), id).observe(this, Observer {
            if (it != null && it) {
                handleOrderState()
            }
        })
    }


    private fun handleSocketConnection(status: State) {
        if (status == State.TRIP_FINISHED || status == State.TRIP_END) {
            viewModel.closeConnection()
        } else {
            viewModel.openConnection()
        }
    }

    private fun handleOrderState(priority: Int = 0) {
        viewModel.getOrderPriority(priority).observe(this, Observer { it ->
            if (it != null && it.isNotEmpty()) {
                val firstOrder = it.first()
                handleSocketConnection(firstOrder.status())
                if (firstOrder.id != orderId || firstOrder.status() != order?.status()) {
                    updateStatus(firstOrder.id)

                    checkOrderStatus(firstOrder)
                    onMapStateChanged(firstOrder)
                }
                if (firstOrder.orderFor != order?.orderFor) {
                    dialogSetTripOwn.initView(firstOrder.orderFor)
                }
                orderId = firstOrder.id
                order = firstOrder
            } else {
                cleanUpOrder()
            }
        })
    }

    private fun checkOrderStatus(order: OrderEntity) {
        if (order.status() != State.TRIP_FINISHED && order.status() != State.DEFAULT) {
            putWayPointsOnMap(order.revertPoints())
            drawLine(order.polyline)
            driver = order.driverId?.let { it1 -> viewModel.getDriver(it1) }
            Log.d("MAPPPEERR_NOO", "Car: ${driver?.id} ${driver?.firstName}")

            rout = Route(order.polyline, mapToQuery(order.revertPoints()))
        } else {
            tripFinished()
            myLocationMakerOn()
        }
    }

    private fun mapToQuery(waypoints: List<StubWaypointAddress>): List<RoutePoint> {
        val listRoutePoint: ArrayList<RoutePoint> = ArrayList()
        for (item in waypoints) {
            listRoutePoint.add(RoutePoint(com.kross.taxi_passenger.data.repository.server.pojo.request.Coords(item.latLng.latitude, item.latLng.longitude), item.address))
        }
        return listRoutePoint
    }


    private fun cleanUpOrder() {
        order = null
        orderId = 0
        container_bottom.gone()
        setupMainScreenView(State.DEFAULT)
    }

    private fun setListenersViews() {
        btn_account.setOnClickListener(this)
        btn_call.setOnClickListener(this)
        tv_where_to.setOnClickListener(this)
    }

    private fun getCurrentAddress() {
        val activityInstance = activity ?: return
        getLocationName(activityInstance, marker.position, {
            myAddress = StubWaypointAddress(it.getAddressLine(0), marker.position, it.thoroughfare, it.subThoroughfare)
        }, {
            showToast(it)
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_account -> activity?.let { CabinetActivity.start(it) }
            R.id.btn_call -> makeACall()
            R.id.tv_where_to -> {
                val activityInstance = activity ?: return
                WaypointsActivity.start(activityInstance, myAddress, MainActivity.WAYPOINTS_REQUEST_CODE)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        readyKey = true
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_map_grey)) // use R.raw.style_map_dark for night mode
        marker = googleMap.addMarker(MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin))
                .position(LatLng(.0, .0))
                .alpha(0f))

        subscribeToConnectivityChanges(viewModel.connectionLiveData) { it ->
            if (!it) showSnack(getString(R.string.txt_error_no_connection))

            with(tv_where_to) {
                isClickable = it

                getColor(if (it) R.color.text_color_main else R.color.where_to_text_disabled)
                        ?.let { setTextColor(it) }

                googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.position))
            }
        }

        viewModel.providerLiveData.observe(this, Observer { requestSettings() })
    }

    private fun makeACall() {
        AlertDialog.Builder(context)
                .setTitle(R.string.title_dlg_call)
                .setMessage(R.string.message_dlg_call)
                .setPositiveButton(R.string.yes) { _, _ ->
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:0123456789")
                    startActivity(intent)
                }
                .setNegativeButton(R.string.no) { dialog, _ -> dialog.cancel() }
                .create()
                .show()
    }

    override fun onLocationChanged(location: Location?) {
        location ?: return
        if (order?.status() == State.TRIP_ON && !isDriving) {
            onTripMarker(LatLng(location.latitude, location.longitude))
            return
        }
        if (!viewModel.isConnected() || orderId != 0) return

        LastPointManager.currentPoint = LatLng(location.latitude, location.longitude)
        marker.position = LatLng(location.latitude, location.longitude)
        marker.setAnchor(0.5f, 0.5f)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.position, if (marker.alpha == 0f) 18f else googleMap.cameraPosition.zoom))
        if (marker.alpha == 0f) getCurrentAddress()
        marker.alpha = 1f
    }


    private fun deleteOrderState(orderId: Int) {
        viewModel.deleteOrder(orderId)
        if (order?.driverId == null) {
            viewModel.cancelOrder(orderId, order?.driverId)
        } else {
            viewModel.cancelOrderInTrip(orderId, order?.driverId)
        }
        viewModel.closeConnection()
        stopDriving {}
    }

    private fun stopDriving(completed: (Boolean) -> Unit) {
        isDriving = false
        delayedPositionChange(listOf(), 0)
        Handler().postDelayed({
            handler.removeCallbacksAndMessages(null)
            myLocationMakerOn()
        }, 1000)
    }

    private fun myLocationMakerOn() {
        googleMap.clear()
        googleMap.setPadding(0, 0, 0, 0)
        marker = googleMap.addMarker(MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin))
                .position(marker.position)
                .alpha(1f))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.position, googleMap.cameraPosition.zoom))
    }

    private fun onMapStateChanged(order: OrderEntity) {
        container_bottom.visible()
        when (order.status()) {
            State.TRIP_ON ->
                showOnTripDialog(order)
            State.DEFAULT, State.TRIP_FINISHED -> {
                container_bottom.gone()
                tripFinished()
                myLocationMakerOn()
                cleanUpOrder()
            }
            State.PAYMENT_PARAMS ->
                showTripCostBottomDialog(order)
            State.TAXI_FOR ->
                showSetOwnerTripDialog(order)
            State.TAXI_SEARCH ->
                showSearchDriverDialog()
            State.TAXI_NOT_FOUND ->
                showDriverNotFoundDialog()
            State.TAXI_FOUND ->

                showDriverFoundDialog(order, false)


            State.DRIVER_ARRIVED ->
                showDriverFoundDialog(order, true)

        }
        setupMainScreenView(order.status())
    }

    private fun setupMainScreenView(status: State) {
        view?.post {
            if (container_bottom.isVisible()) {
                googleMap.setPadding(0, 0, 0, container_bottom.height)
                btn_call.visibleOrGone(status == State.TRIP_ON)
                btn_account.visibleOrGone(status == State.TRIP_ON)
                tv_where_to.visibleOrGone(false)
            } else {
                //googleMap.clear()
                googleMap.setPadding(0, 0, 0, 0)
                btn_call.visibleOrGone(true)
                btn_account.visibleOrGone(true)
                tv_where_to.visibleOrGone(true)
            }
        }
    }

    fun setWaypoints(waypoints: List<StubWaypointAddress>) {
        if (waypoints.size == 1) {
            viewModel.insertOrder(distance = 0, listRoutePoint = waypoints, polyline = "", priority = if (orderId == 0) 0 else 1)
            putWayPointsOnMap(waypoints)
            return
        }

        googleMap.clear()
        viewModel.getDirection(getString(R.string.google_maps_directions_key),
                waypoints.map { it.latLng },
                {
                    viewModel.insertOrder(distance = it.first, listRoutePoint = waypoints, polyline = it.second, priority = if (orderId == 0) 0 else 1)
                    putWayPointsOnMap(waypoints)
                    drawLine(it.second)
                },
                { s ->
                    s?.let { showToast(it) }
                })
    }

    private fun checkPointState() {
        if (order == null) return

        val result = order?.listRoutePoint
                ?.filter {
                    it.pointState() != PointState.passed
                }
        result?.forEach {
            val distance = SphericalUtil.computeDistanceBetween(marker.position, it.position())
            if (distance > 0f && distance < 50f && it.pointState() != PointState.on) {
                viewModel.updatePoint(it, PointState.on, order)
                if (result.size == 1) {
                    tripFinished()
                }
            }
            if (distance > 50f && distance < 100f && it.pointState() == PointState.on) {
                viewModel.updatePoint(it, PointState.passed, order)
            }

        }
        updateMarkersState(order?.revertPoints())
    }


    private fun tripFinished() {
        if (order == null) return
        viewModel.updateOrderStatus(order!!.id, State.TRIP_FINISHED.status)
        TripFinishedActivity.start(this, order!!.id)
        stopDriving {
            cleanUpOrder()
        }
    }

    private fun drawLine(path: String) {
        val points = PolyUtil.decode(path)
        if (points.isEmpty()) return
        googleMap.addPolyline(PolylineOptions()
                .addAll(points)
                .width(6f)
                .color(ContextCompat.getColor(context!!, R.color.purple_dark)))
        val builder = LatLngBounds.builder()
        points.forEach {
            builder.include(it)
        }
        Handler().postDelayed({
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 30))
        }, 500)
    }

    // TODO: - hide marker inside imageView state

    private fun updateMarkersState(list: List<StubWaypointAddress>?) {
        markers.forEach {
            it.remove()
        }
        markers = mutableListOf()
        list?.forEach {
            val option = googleMap.addMarker(MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(it.state.icon))
                    .position(it.latLng)
            )
            markers.add(option)
        }

        order?.let { dialogTripOn?.updateList(it) }
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun putWayPointsOnMap(list: List<StubWaypointAddress>) {
        googleMap.clear()
        if (order?.status() == State.TRIP_ON) {
            updateMarkersState(list)
        } else {
            list.forEach {
                val generator = IconGenerator(context)
                val view = layoutInflater.inflate(R.layout.pin_waypoint, null, false)
                view.tvAddress.text = it.streetName + ", " + it.streetNum
                view.tvPointName.text = MapFragment.Letter.from(list.indexOf(it)).letter
                generator.setContentView(view)
                generator.setBackground(null)
                val markerOptions = MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(generator.makeIcon()))
                        .position(it.latLng)
                        .anchor(generator.anchorU, generator.anchorV)

                googleMap.addMarker(markerOptions)
            }
        }
    }

    private var cost = "0"

    private fun showTripCostBottomDialog(order: OrderEntity) {
        tripCostDialog = TripCostBottomDialog(container_bottom, order)

        cost = tvProxPrice.text.toString().replace("₦", "")
        tripCostDialog.closeListener = { deleteOrderState(order.id) }
        tripCostDialog.driverNoteListener = {
            NoteAndNumberActivity.start(this, LEAVE_NOTE_REQUEST_CODE, this.order?.comment)
        }
        //  tripCostDialog.carTypeListener = { handleCarTypeSelection() }
        tripCostDialog.cardListener = { activity?.let { ChoosePaymentActivity.start(this, CHOOSE_PAYMENT_REQUEST_CODE) } }
        tripCostDialog.timeListener = { activity?.let { SetTimeTripActivity.start(this, CHOOSE_DATE_TRIP_REQUEST_CODE) } }
        tripCostDialog.nextListener = {

            viewModel.updateOrderStatus(order.id, State.TAXI_FOR.status)
        }
    }

    private fun showSetOwnerTripDialog(order: OrderEntity) {
        dialogSetTripOwn = PassengerBottomDialog(container_bottom, order.orderFor)

        dialogSetTripOwn.closeListener = { viewModel.updateOrderStatus(order.id, State.PAYMENT_PARAMS.status) }
        dialogSetTripOwn.nextListener = {
            viewModel.updateOrderStatus(order.id, State.TAXI_SEARCH.status)
        }
        dialogSetTripOwn.txtMeListener = { viewModel.updateOrderFor(order.id, null) }
        dialogSetTripOwn.txtSomeoneElseListener = { showBootomShetDialogNumber() }
    }

    private fun showOnTripDialog(order: OrderEntity) {
        dialogTripOn = TripOnBottomDialog(container_bottom, order, context!!)
        dialogTripOn?.changeTripListener = { it ->
            val order = it
            activity?.let { TripDetailsActivity.start(this, order.id) }
            // FIXME: - incorrect usage, user can cancel trip while waiting for the car
        }
        dialogTripOn?.tripDetailsListener = {
            handleOrderState(if (order.priority == 0) 1 else 0)
            // TODO: - redraw polyline
        }



        dialogTripOn?.startDriveListener = {
            startCarDrive()
        }
        dialogTripOn?.deleteOrderListener = {
            deleteOrderState(orderId)
        }

    }

    private fun onTripMarker(position: LatLng) {

        if (marker != null) {
            marker.remove()
            marker = googleMap.addMarker(MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car))
                    .position(position)
                    .anchor(0.5f, 0.5f)
                    .alpha(1f))
            checkPointState()
        }

    }

    private fun startCarDrive() {
        isDriving = true
        delayedPositionChange(PolyUtil.decode(order?.polyline), 0)
    }

    private fun delayedPositionChange(array: List<LatLng>, index: Int) {
        if (index > array.count() - 1 || order == null) return
        MarkerAnimation.animateMarkerToICS(marker, array[index], LatLngInterpolator.LinearFixed())
        handler.postDelayed({
            delayedPositionChange(array, index + 1)
            checkPointState()
        }, 400)
        MarkerAnimation.rotateMarker(marker, marker.position.angle(array[index]))
    }

    private fun delayedPositionChanged(array: LatLng) {
//        MarkerAnimation.animateMarkerToICS(marker, array, LatLngInterpolator.LinearFixed())
        context?.runOnUiThread {
            onTripMarker(array)
            MarkerAnimation.rotateMarker(marker, marker.position.angle(array))
        }
    }

    private fun showBootomShetDialogNumber() {
        val dialogChoseTripOwner = BottomDialogChoseTripOwner.newInstance()
        dialogChoseTripOwner.setOnClickListener(this)
        dialogChoseTripOwner.show(fragmentManager, tag)
    }

    private fun showSearchDriverDialog() {
        val dialogSearchDriver = SearchDriverBottomDialog(container_bottom)
        dialogSearchDriver.cancelListener = { showDialogCancelTripWithoutDriver() }

        val order: Order? = rout?.let { Order(it, carType, order?.date, order?.comment, order?.orderFor, cost, order?.paymentType) }
        if (order != null) {
            viewModel.addOrder(context?.getStringPreference(R.string.token).toString(), order).observe(this, Observer {
                if (it != null) {
                    viewModel.openConnection()
                    viewModel.updateOrderId(orderId, it.orderId)
                    viewModel.updateOrder(context?.getStringPreference(R.string.token).toString(), it.orderId)
                    orderId = it.orderId
                }
            })
        }
    }

    private fun showDriverNotFoundDialog() {
        val dialogDriverNotFound = DriverNotFoundBottomDialog(container_bottom)
        dialogDriverNotFound.cancelListener = { deleteOrderState(orderId) }
        dialogDriverNotFound.tryOneMoreTimeListener = {}
    }

    private var driver: DriverEntity? = null
    private var MINUTES_5 = (60 * 1000) * 5

    private fun showDriverFoundDialog(order: OrderEntity, isArrived: Boolean) {
//        Timer("SettingUp6113", false).schedule(1500) {
//            handleOrderState()
//        }
        val dialogDriverFound =
                order.carId?.let { it1 ->
                    val carr = viewModel.getCar(it1)
                    val driverr = order.driverId?.let { viewModel.getDriver(it) }
                    Log.d("MAPPPEERR_NOO1", "Car: ${carr?.id} ${carr?.make}")
                    Log.d("MAPPPEERR_NOO1", "DRIVER: ${driver?.id} ${driver?.firstName}")
                    driverr?.let { carr?.let { it2 -> DriverFoundBottomDialog(container_bottom, context, it, it2, isArrived) } }
                }

        dialogDriverFound?.cancelTripListener = {
            if (Calendar.getInstance().timeInMillis - context!!.getLongPreference(R.string.arrived_key) < MINUTES_5) {
                if (TimeUnit.MILLISECONDS.toMinutes(Calendar.getInstance().timeInMillis - context!!.getLongPreference(R.string.arrived_key)) >= 1)
                    showDialogCancelTrip(TimeUnit.MILLISECONDS.toMinutes(Calendar.getInstance().timeInMillis - context!!.getLongPreference(R.string.arrived_key)))
                else
                    showDialogCancelTrip(1)
            } else {
                showDialogCancelCharged()
            }
        }
        dialogDriverFound?.phoneListener = { callToDriver() }
    }

    private fun callToDriver() {
        activity?.let { driver?.callToDriver(it) }
    }


    private fun showDialogCancelTripWithoutDriver() {
        AlertDialogHelper.createDialog(container_bottom.context,
                R.string.cancel_trip_title,
                R.string.cancel_trip_text,
                getString(R.string.no),
                getString(R.string.yes),
                Action {
                    deleteOrderState(orderId)
                    showDialogTripCanceled()
                })
    }

    private fun showDialogCancelTrip(time: Long) {
        showAlertDialog {
            setTitle(getString(R.string.cancel_titlee))
            setMessage(getString(R.string.alert_ordered_text) + time.toString() + getString(R.string.alert_ordered_text_next))
            positiveButton(getString(R.string.yes)) {
                deleteOrderState(orderId)
                showDialogTripCanceled()
            }
            negativeButton(getString(R.string.no)) {
            }
        }
    }


    private fun showDialogCancelCharged() {
        AlertDialogHelper.createDialog(container_bottom.context,
                R.string.cancel_trip_title_ch,
                R.string.cancel_trip_text_ch,
                getString(R.string.no),
                getString(R.string.yes),
                Action { deleteOrderState(orderId) })
    }

    private fun showDialogTripCanceled() {
        AlertDialogHelper.createDialog(container_bottom.context,
                R.string.txt_alert_title,
                R.string.txt_alert_text,
                "",
                getString(R.string.ok),
                Action { deleteOrderState(orderId) })
    }

    override fun onClickItem(view: View) {
        when (view.id) {
            R.id.txtBottomDialog -> {
                requestContact()
            }
            R.id.txtBottomDialogDelete -> NoteAndNumberActivity.start(this, CHOOSE_NUMBER_REQUEST_CODE, "")
        }
    }

    private fun requestContact() {
        containerMapScreen.post {
            val request = permissionsBuilder(Manifest.permission.READ_CONTACTS).build()
            request.onAccepted { openContact() }
                    .onDenied { showSnack(getString(R.string.txt_permission_contact_denied)) }
                    .onPermanentlyDenied { showSnackbarSetting() }
            request.send()
        }
    }

    private fun openContact() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, PICK_CONTACT_REQUEST_CODE)
    }

    private fun showSnackbarSetting() {
        activity?.let {
            showSnackBarAction(it, containerMapScreen,
                    getString(R.string.txt_permission_contact_denied),
                    getString(R.string.txt_setting),
                    { _ -> viewModel.openApplicationSettings(it, this@MapFragment, 1) })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                CHOOSE_PAYMENT_REQUEST_CODE -> handleResponsePaymentRequest(data)
                CHOOSE_DATE_TRIP_REQUEST_CODE -> handleResponseTimeTripRequest(data)
                LEAVE_NOTE_REQUEST_CODE -> {
                    handleResponseLeaveNote(data)
                }
                CHOOSE_NUMBER_REQUEST_CODE -> {
                    handleResponseEnterPhoneNumber(data)
                }
                PICK_CONTACT_REQUEST_CODE -> {
                    handleResponseContact(data)
                }
                TRIP_CANCELED_REQUEST_CODE -> {
                    showDialogTripCanceled()
                }
            }
        }
    }

    private var carType = 0

//    private fun handleCarTypeSelection() {
//        val actionSheet = activity?.let { BottomSheetDialog(it) }
//        actionSheet?.setContentView(R.layout.dialog_bottom_car)
//        actionSheet?.standardLayout?.setOnClickListener {
//            tripCostDialog.updateCarType(CarType.STANDARD)
//            actionSheet.dismiss()
//        }
//        actionSheet?.premiumLayout?.setOnClickListener {
//            tripCostDialog.updateCarType(CarType.PREMIUM)
//            carType = 1
//            actionSheet.dismiss()
//        }
//        actionSheet?.show()
//    }

    private fun handleResponsePaymentRequest(data: Intent) {
        val paymentMethod: PaymentMenthod = data.argOrThrow(ChoosePaymentActivity.PAYMENT_TYPE_EXTRA)
        val card = data.extras.getLong(ChoosePaymentActivity.CARD_RESULT_EXTRA)
        if (paymentMethod == PaymentMenthod.CARD) {
            viewModel.updatePaymentTypeAndCard(paymentMethod.paymentMethod, card.toString(), orderId)
        } else {
            viewModel.updateOrderPaymentType(orderId, paymentMethod.paymentMethod)
        }
        tripCostDialog.updatePayment(paymentMethod, card.toString())
    }

    private fun handleResponseTimeTripRequest(data: Intent) {
        // TODO: - ask for date format?
        tripCostDialog.updateDate(data.extras.getString(SetTimeTripActivity.TIME_DATE_TRIP))
    }

    private fun handleResponseLeaveNote(data: Intent) {
        val comment = data.extras.getString(NoteAndNumberActivity.NOTE_RESULT_KEY)
        viewModel.updateOrderComment(comment, orderId)
        tripCostDialog.updateCommentView(comment)
    }

    private fun handleResponseEnterPhoneNumber(data: Intent) {
        val phoneNumber = data.extras.getString(NoteAndNumberActivity.PHONE_CODE_KEY)
        viewModel.updateOrderFor(orderId, phoneNumber)
    }

    private fun handleResponseContact(data: Intent) {
        activity?.let {
            val phoneNumber = viewModel.getContactInfo(data, it)
            viewModel.updateOrderFor(orderId, phoneNumber)
        }
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        intent?.putExtra(getString(R.string.txt_request_code), requestCode)
        super.startActivityForResult(intent, requestCode)
    }
}
