package com.kross.taxi_passenger.presentation.screen.favorite.addresses

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.google.android.gms.location.places.AutocompletePrediction
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.domain.FavoriteViewModel
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.managers.LastPointManager.currentPoint
import com.kross.taxi_passenger.presentation.screen.base.BaseFragment
import com.kross.taxi_passenger.presentation.screen.waypoints.suggestion.SuggestionAdapter
import com.kross.taxi_passenger.utils.*
import kotlinx.android.synthetic.main.fragment_add_point.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class AddFavoriteAddressFragment: BaseFragment() {

    private lateinit var searchView: SearchView
    private lateinit var suggestAdapter: SuggestionAdapter
    private val viewModel: FavoriteViewModel by sharedViewModel()

    companion object {
        fun newInstance() = AddFavoriteAddressFragment()
    }

    override fun getLayout() = R.layout.fragment_add_point

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        suggestAdapter =
                SuggestionAdapter(viewModel.geoDataClient,
                        currentPoint?.toBounds(),
                        LocationHelper.addressAutocompleteFilter,
                        this::handleSuggestionClick,
                        this::handleFoundCount)
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = suggestAdapter

        btnAdd.gone()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu_on_black, menu)

        val item = menu.findItem(R.id.action_search)

        searchView = item.actionView as SearchView
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        searchView.queryHint = getQueryHint()
        item.expandActionView()

        item.setOnActionExpandListener(object: MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?) = true

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                fragmentManager?.popBackStack()
                return false
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if(!isAdded) return true

                suggestAdapter.setSearchQuery(newText)
                return false
            }
        })
    }

    private fun getQueryHint() = getString(R.string.add_favorite_address)

    private fun handleSuggestionClick(prediction: AutocompletePrediction) {
        val placeId = prediction.placeId ?: return

        viewModel.geoDataClient.getPlaceById(placeId) {
            it?.let {
                fragmentManager?.popBackStack()
                fragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.container, ConfirmFavoriteAddressFragment.newInstance(StubWaypointAddress.from(it)))
                        ?.addToBackStack(null)
                        ?.commit()
                showToast("Show save imageView screen")
            }
        }
    }


    private fun handleFoundCount(count: Int) {
        nothing_found?.visibleOrGone(count == 0 && !searchView.query.isNullOrEmpty())
    }

}