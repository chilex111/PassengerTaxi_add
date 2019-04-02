package com.kross.taxi_passenger.presentation.screen.favorite.routes

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.Places
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.Coords
import com.kross.taxi_passenger.data.repository.server.pojo.FavoriteRoute
import com.kross.taxi_passenger.data.repository.server.pojo.KrossRoute
import com.kross.taxi_passenger.data.repository.server.pojo.KrossRoutePoint
import com.kross.taxi_passenger.domain.FavoriteViewModel
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.managers.LastPointManager
import com.kross.taxi_passenger.presentation.screen.base.BasePointFragment
import com.kross.taxi_passenger.presentation.screen.waypoints.address.PointsAdapter
import com.kross.taxi_passenger.presentation.screen.waypoints.address.WaypointAddressViewHolder
import com.kross.taxi_passenger.presentation.screen.waypoints.recent.RecentPointAdapter
import com.kross.taxi_passenger.presentation.screen.waypoints.suggestion.SuggestionAdapter
import com.kross.taxi_passenger.utils.*
import kotlinx.android.synthetic.main.fragment_edit_favorite_route.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ModifyFavoriteRouteFragment : BasePointFragment() {

    companion object {
        private const val NO_EDIT_POSITION = -1
        private const val ROUTE_EXTRA = "ROUTE_EXTRA"

        fun newInstance(route: KrossRoute? = null): ModifyFavoriteRouteFragment {
            val fragment = ModifyFavoriteRouteFragment()
            fragment.arguments = Bundle()
                    .apply { putParcelable(ROUTE_EXTRA, route) }

            return fragment
        }
    }

    override fun getLayout() = R.layout.fragment_edit_favorite_route

    private lateinit var suggestAdapter: SuggestionAdapter
    private lateinit var addressAdapter: PointsAdapter
    private lateinit var recentAdapter: RecentPointAdapter

    private lateinit var geoDataClient: GeoDataClient
    private val viewModel: FavoriteViewModel by sharedViewModel()

    private var currentChangePosition = NO_EDIT_POSITION

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        geoDataClient = Places.getGeoDataClient(requireContext())

        rv_addresses.layoutManager = LinearLayoutManager(context)

        addressAdapter = PointsAdapter(
                this::handleAddressTyping,
                this::handleAddressChangeFocus,
                this::handleItemChanged)

        rv_addresses.adapter = addressAdapter

        val route: KrossRoute? = argOrNull(ROUTE_EXTRA)

        if (route != null) {
            addressAdapter.addItems(route.routePoints.map { StubWaypointAddress.from(it) })
        }

        rv_search_results.layoutManager = LinearLayoutManager(context)

        suggestAdapter =
                SuggestionAdapter(geoDataClient,
                        LastPointManager.currentPoint?.toBounds(10000.0),
                        LocationHelper.addressAutocompleteFilter,
                        this::handleSuggestionClick)

        rv_search_results.adapter = suggestAdapter

        btnSave.setOnClickListener { onSaveClicked() }

        initActionBar()

        recentAdapter = RecentPointAdapter(emptyList()) {
            addressAdapter.addItem(it)
        }
    }

    private fun onSaveClicked() {
        viewModel.getPolyline(getString(R.string.google_maps_directions_key),
                addressAdapter.getItems().map { it.latLng },
                {
                    val krossRoutePoints: MutableList<KrossRoutePoint> = mutableListOf()

                    addressAdapter.getItems().forEach {
                        if(it.streetName == null) {
                            krossRoutePoints.add(KrossRoutePoint(it.address, Coords.from(it.latLng)))
                        } else {
                            krossRoutePoints.add(KrossRoutePoint(it.address, Coords.from(it.latLng)))
                        }
                    }

//                    if(isEditMode()) {
//                        val id = argOrThrow<KrossRoute>(ROUTE_EXTRA).id!!
//
//                        viewModel.editFavoriteRoute(id, FavoriteRoute(KrossRoute(it.second, krossRoutePoints))) {
//                            fragmentManager?.popBackStack()
//                        }
//                    } else {
                        viewModel.addFavoriteRoute(FavoriteRoute(KrossRoute(it.second, krossRoutePoints))) {
                            fragmentManager?.popBackStack()
//                        }
                  }
                },
                onError = {
                    it?.let { showToast(it) }
                })

      //  showToast("on save favorite clicked")
    }

    private fun initActionBar() {
        getSupportActionBar()?.title = getString(R.string.add_a_route)
    }

    private fun handleAddressTyping(address: String, position: Int) {
        currentChangePosition = position

        suggestAdapter.setSearchQuery(address)

        toggleSuggestionVisibility(!address.isNullOrEmpty())
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
        btnSave.isEnabled = items > 1
    }

    private fun toggleSuggestionVisibility(isVisible: Boolean) {
        rv_search_results.visibleOrGone(isVisible)
        group_under_suggest.visibleOrGone(!isVisible)
    }

    private fun handleSuggestionClick(prediction: AutocompletePrediction) {
        val placeId = prediction.placeId ?: return

        geoDataClient.getPlaceById(placeId) {
            val place = it ?: return@getPlaceById
            val context: Context = context ?: return@getPlaceById

            getLocationName(context, place.latLng,
                    onReceive = {
                        addressAdapter.setItem(currentChangePosition,
                                StubWaypointAddress(place.address.toString(), place.latLng,
                                        it.thoroughfare, it.subThoroughfare))

                        onLocationReceive()
                    },
                    onError = {
                        addressAdapter.setItem(currentChangePosition,
                                StubWaypointAddress(place.address.toString(), place.latLng))

                        onLocationReceive()
                    })
        }
    }

    private fun onLocationReceive() {
        toggleSuggestionVisibility(false)
        currentChangePosition = NO_EDIT_POSITION
        activity?.hideKeyboardEx()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity?.finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isEditMode() = arguments?.containsKey(ROUTE_EXTRA) ?: false
}