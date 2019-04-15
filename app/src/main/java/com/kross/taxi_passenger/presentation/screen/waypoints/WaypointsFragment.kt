package com.kross.taxi_passenger.presentation.screen.waypoints

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.Places
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.OrderEntity
import com.kross.taxi_passenger.data.repository.database.entity.UserPointEntity
import com.kross.taxi_passenger.data.repository.server.pojo.FavoriteAddress
import com.kross.taxi_passenger.data.repository.server.pojo.KrossRoute
import com.kross.taxi_passenger.domain.WaypointsViewModel
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.presentation.screen.base.BasePointFragment
import com.kross.taxi_passenger.presentation.screen.favorite.FavoriteActivity
import com.kross.taxi_passenger.presentation.screen.favorite.FavoriteActivity.Companion.ADDRESS_RESPONSE_EXTRA
import com.kross.taxi_passenger.presentation.screen.favorite.FavoriteActivity.Companion.ROUTE_RESPONSE_EXTRA
import com.kross.taxi_passenger.presentation.screen.waypoints.addPoint.AddPointFragment
import com.kross.taxi_passenger.presentation.screen.waypoints.address.PointsAdapter
import com.kross.taxi_passenger.presentation.screen.waypoints.address.WaypointAddressViewHolder
import com.kross.taxi_passenger.presentation.screen.waypoints.address_by_pin.AddressByMapPinActivity
import com.kross.taxi_passenger.presentation.screen.waypoints.recent.RecentPointAdapter
import com.kross.taxi_passenger.presentation.screen.waypoints.suggestion.SuggestionAdapter
import com.kross.taxi_passenger.utils.*
import kotlinx.android.synthetic.main.fragment_waypoints.*
import org.koin.android.viewmodel.ext.android.viewModel

class WaypointsFragment : BasePointFragment() {

    companion object {
        private const val NO_EDIT_POSITION = -1
        private const val INITIAL_ADDRESS_EXTRA = "INITIAL_ADDRESS_EXTRA"
        private const val FAVORITE_REQUEST_CODE = 17890

        fun newInstance(address: StubWaypointAddress): WaypointsFragment {
            val fragment = WaypointsFragment()

            fragment.arguments = Bundle()
                    .apply {
                        putParcelable(INITIAL_ADDRESS_EXTRA, address)
                    }

            return fragment
        }
    }

    private var currentChangePosition = NO_EDIT_POSITION

    override fun getLayout() = R.layout.fragment_waypoints

    private lateinit var suggestAdapter: SuggestionAdapter
    private lateinit var addressAdapter: PointsAdapter
    private lateinit var recentAdapter: RecentPointAdapter

    private lateinit var geoDataClient: GeoDataClient

    private val waypointsViewModel: WaypointsViewModel by viewModel()

    private var homePoint: UserPointEntity? = null
    private var workPoint: UserPointEntity? = null

    private var recentWayPointsList: MutableList<StubWaypointAddress> = ArrayList()
    private lateinit var currentPoint: StubWaypointAddress

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentPoint = argOrThrow(INITIAL_ADDRESS_EXTRA)
        geoDataClient = Places.getGeoDataClient(requireContext())
        rv_addresses.layoutManager = LinearLayoutManager(context)

        addressAdapter = PointsAdapter(
                this::handleAddressTyping,
                this::handleAddressChangeFocus,
                this::handleItemChanged)

        addressAdapter.addItem(currentPoint)
        rv_addresses.adapter = addressAdapter
        rv_search_results.layoutManager = LinearLayoutManager(context)

        suggestAdapter =
                SuggestionAdapter(geoDataClient,
                        currentPoint.latLng.toBounds(10000.0),
                        LocationHelper.addressAutocompleteFilter,
                        this::handleSuggestionClick)

        rv_search_results.adapter = suggestAdapter

        btn_add_home.setOnClickListener { onHomeClicked() }
        btn_add_work.setOnClickListener { onWorkClicked() }
        btn_my_favorite.setOnClickListener { onFavoriteClicked() }
        btn_destination_later.setOnClickListener { onDestinationLaterClicked() }
        btn_set_pin.setOnClickListener {
            activity?.let { it1 -> AddressByMapPinActivity.start(it1) }
        }

        btnNext.setOnClickListener { onNextClicked() }

        btn_add_home.text = getString(R.string.add_home)
        btn_add_work.text = getString(R.string.add_work)

        waypointsViewModel.getRecent().observe(this, Observer {
                createRecentList(it!!)

        })

        waypointsViewModel.getUserPoints().observe(this, Observer { it ->
            if (it == null) return@Observer
            it.forEach {
                val id = UserPointEntity.Companion.ID.values()[it.id]
                when (id) {
                    UserPointEntity.Companion.ID.HOME -> {
                        homePoint = it
                        btn_add_home.text = getString(R.string.home_point, it.name)
                    }
                    UserPointEntity.Companion.ID.WORK -> {
                        workPoint = it
                        btn_add_work.text = getString(R.string.work_point, it.name)
                    }
                }
            }
        })

        initActionBar()
    }

    private fun onFavoriteClicked() {
        FavoriteActivity.newInstance(this, FAVORITE_REQUEST_CODE)
    }

    private fun onNextClicked() {
        (activity as WaypointsActivity).deliverResult(addressAdapter.getItems())
    }

    private fun onDestinationLaterClicked() {
        btnNext.isEnabled = true
    }

    private fun initActionBar() {
        getSupportActionBar()?.title = getString(R.string.choose_your_route)
        getSupportActionBar()?.setDisplayShowTitleEnabled(true)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    private fun handleAddressTyping(address: String, position: Int) {
        currentChangePosition = position

        suggestAdapter.setSearchQuery(address)

        toggleSuggestionVisibility(!address.isEmpty())
    }

    private fun handleAddressChangeFocus(position: Int) {
        if (currentChangePosition == position) return
        rv_addresses.findViewHolderForAdapterPosition(currentChangePosition)?.let {
            (it as WaypointAddressViewHolder).reset()
        }

        currentChangePosition = NO_EDIT_POSITION

        toggleSuggestionVisibility(false)
    }

    private fun handleItemChanged(items: Int) {
        if (btnNext == null) return
        btnNext.isEnabled = items > 1
    }

    private fun toggleSuggestionVisibility(isVisible: Boolean) {
        rv_search_results.visibleOrGone(isVisible)
        group_under_suggest.visibleOrGone(!isVisible)
    }

    private fun handleSuggestionClick(prediction: AutocompletePrediction) {
        val placeId = prediction.placeId ?: return

        geoDataClient.getPlaceById(placeId) {
            val place = it ?: return@getPlaceById

            addressAdapter.setItem(currentChangePosition, StubWaypointAddress(place.address.toString(), it.latLng, place.name.toString()))
            toggleSuggestionVisibility(false)
            currentChangePosition = NO_EDIT_POSITION
            activity?.hideKeyboardEx()
        }
    }

    private fun createRecentList(entity: List<OrderEntity>) {
        val recentList = entity.takeLast(5)

        for (item in recentList) {
            if (item.listRoutePoint.size > 1)
                recentWayPointsList.add(StubWaypointAddress(item.listRoutePoint[1].street, item.listRoutePoint[1].position()))
        }

        recentAdapter = RecentPointAdapter(recentWayPointsList) {
            addressAdapter.addItem(it)
        }
        rv_recent.layoutManager = LinearLayoutManager(context)
        rv_recent.adapter = recentAdapter
    }

    private fun onHomeClicked() {
        if (homePoint == null) {
            (activity as? WaypointsActivity)?.openAddPointFragment(AddPointFragment.Companion.Type.HOME)
        } else {
            homePoint?.let { addPoint(StubWaypointAddress.from(it)) }
        }
    }

    private fun onWorkClicked() {
        if (workPoint == null) {
            (activity as? WaypointsActivity)?.openAddPointFragment(AddPointFragment.Companion.Type.WORK)
        } else {
            workPoint?.let { addPoint(StubWaypointAddress.from(it)) }
        }
    }

    private fun addPoint(point: StubWaypointAddress) {
        addressAdapter.addItem(point)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity?.finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            FAVORITE_REQUEST_CODE -> handleFavoriteResponse(data)
        }
    }

    private fun handleFavoriteResponse(data: Intent?) {
        if (data == null) return
        when {
            data.extras.containsKey(ADDRESS_RESPONSE_EXTRA) -> {
                val favoriteAddress: FavoriteAddress = data.argOrThrow(ADDRESS_RESPONSE_EXTRA)
                addressAdapter.addItem(StubWaypointAddress.from(favoriteAddress))
            }
            data.extras.containsKey(ROUTE_RESPONSE_EXTRA) -> {
                val favoriteRoutes: KrossRoute = data.argOrThrow(ROUTE_RESPONSE_EXTRA)
                addressAdapter.setItems(favoriteRoutes.routePoints.map { StubWaypointAddress.from(it) })
            }
            else -> throw IllegalArgumentException("Favorites without result")
        }
    }
}