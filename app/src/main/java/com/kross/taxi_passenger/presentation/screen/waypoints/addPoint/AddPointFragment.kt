package com.kross.taxi_passenger.presentation.screen.waypoints.addPoint

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
import com.google.android.gms.maps.model.LatLng
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.domain.AddPointViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseFragment
import com.kross.taxi_passenger.presentation.screen.waypoints.suggestion.SuggestionAdapter
import com.kross.taxi_passenger.utils.*
import kotlinx.android.synthetic.main.fragment_add_point.*
import org.jetbrains.anko.enabled
import org.koin.android.viewmodel.ext.android.sharedViewModel

class AddPointFragment : BaseFragment() {

    companion object {
        enum class Type {
            HOME,
            WORK
        }

        private const val CURRENT_POINT_EXTRA = "CURRENT_POINT_EXTRA"
        private const val ADD_POINT_TYPE_EXTRA = "ADD_POINT_TYPE_EXTRA"

        fun newInstance(point: LatLng, type: Type): AddPointFragment {
            val fragment = AddPointFragment()
            fragment.arguments = Bundle()
                    .apply {
                        putParcelable(CURRENT_POINT_EXTRA, point)
                        putSerializable(ADD_POINT_TYPE_EXTRA, type)
                    }

            return fragment
        }
    }

    private val viewModel: AddPointViewModel by sharedViewModel()
    private lateinit var suggestAdapter: SuggestionAdapter
    private lateinit var searchView: SearchView
    private var prediction: AutocompletePrediction? = null

    override fun getLayout() = R.layout.fragment_add_point

    private lateinit var type: Type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentPoint = argOrThrow<LatLng>(CURRENT_POINT_EXTRA)
        type = argOrThrow(ADD_POINT_TYPE_EXTRA)

        suggestAdapter =
                SuggestionAdapter(viewModel.geoDataClient,
                        currentPoint.toBounds(10000.0),
                        LocationHelper.addressAutocompleteFilter,
                        this::handleSuggestionClick,
                        this::handleFoundCount)

        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = suggestAdapter

        btnAdd.text = getQueryHint()
        btnAdd.setOnClickListener { handleBtnAddClick() }
    }

    private fun handleSuggestionClick(prediction: AutocompletePrediction) {
        setChosedPrediction(prediction)
    }

    private fun handleFoundCount(count: Int) {
        nothing_found?.visibleOrGone(count == 0 && !searchView.query.isNullOrEmpty())
    }

    private fun handleBtnAddClick() {
        activity?.hideKeyboardEx()

        prediction?.let {
            val placeId = it.placeId ?: return

            viewModel.geoDataClient.getPlaceById(placeId) {
                it?.let {
                    when (type) {
                        Type.HOME -> {
                            viewModel.saveHomePoint(it)
                        }
                        Type.WORK -> {
                            viewModel.saveWorkPoint(it)
                        }
                    }

                    fragmentManager?.popBackStack()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)

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
                setChosedPrediction(null)
                return false
            }
        })
    }

    private fun getQueryHint() =
            when (type) {
                Type.HOME -> getString(R.string.add_home)
                Type.WORK -> getString(R.string.add_work)
            }

    private fun setChosedPrediction(prediction: AutocompletePrediction?) {
        if(prediction != null) searchView.setQuery(prediction.getFullText(null), true)
        this.prediction = prediction
        btnAdd.enabled = prediction != null
    }
}