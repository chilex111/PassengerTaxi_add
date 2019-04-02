package com.kross.taxi_passenger.presentation.screen.waypoints.suggestion

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.maps.model.LatLngBounds
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.utils.getAutocomplete

class SuggestionAdapter(private val mGeoDataClient: GeoDataClient,
                        private var mBounds: LatLngBounds?,
                        private val mPlaceFilter: AutocompleteFilter?,
                        private val suggestionClickListener: (AutocompletePrediction) -> Unit,
                        private val foundCountListener: ((Int) -> Unit)? = null) : RecyclerView.Adapter<SuggestionViewHolder>() {

    private val predictions: MutableList<AutocompletePrediction> = mutableListOf()
    private var searchQuery: String? = null

    private val clickListener: (Int) -> Unit = {
        suggestionClickListener.invoke(predictions[it])
    }

    override fun getItemCount(): Int {
        return predictions.size
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.bind(predictions[position], searchQuery)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.item_waypoint_suggestion, parent, false)
                .let { return SuggestionViewHolder(it, clickListener) }
    }

    fun setSearchQuery(query: String) {
        searchQuery = query

        if(TextUtils.isEmpty(query)) {
            predictions.clear()
            notifyDataSetChanged()
            foundCountListener?.invoke(0)
            return
        }

        mGeoDataClient.getAutocomplete(query, mBounds, mPlaceFilter) {
            predictions.clear()
            predictions.addAll(it)
            notifyDataSetChanged()

            foundCountListener?.invoke(it.size)
        }
    }

    private fun clearPredictions() {

    }
}