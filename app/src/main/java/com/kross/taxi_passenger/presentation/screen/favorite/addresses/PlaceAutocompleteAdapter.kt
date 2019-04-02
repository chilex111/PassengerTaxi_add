package com.kross.taxi_passenger.presentation.screen.favorite.addresses

import android.content.Context
import android.graphics.Typeface
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.common.data.DataBufferUtils
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.android.gms.tasks.Tasks
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class PlaceAutocompleteAdapter
(context: Context,
 private val geoDataClient: GeoDataClient,
 private var bounds: LatLngBounds?,
 private val placeFilter: AutocompleteFilter?) : ArrayAdapter<AutocompletePrediction>(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1), Filterable {

    companion object {
        private val STYLE_BOLD = StyleSpan(Typeface.BOLD)
    }

    private var mResultList: ArrayList<AutocompletePrediction>? = null

    fun setBounds(bounds: LatLngBounds) {
        this.bounds = bounds
    }

    override fun getCount(): Int {
        return mResultList?.size ?: 0
    }

    override fun getItem(position: Int): AutocompletePrediction {
        return mResultList!![position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val row = super.getView(position, convertView, parent)

        val item = getItem(position)

        val textView1 = row.findViewById<View>(android.R.id.text1) as TextView
        val textView2 = row.findViewById<View>(android.R.id.text2) as TextView
        textView1.text = item.getPrimaryText(STYLE_BOLD)
        textView2.text = item.getSecondaryText(STYLE_BOLD)

        return row
    }

    override fun getFilter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            val results = Filter.FilterResults()
            var filterData: ArrayList<AutocompletePrediction>? = ArrayList()
            if (constraint != null) {
                filterData = getAutocomplete(constraint)
            }

            results.values = filterData
            if (filterData != null) {
                results.count = filterData.size
            } else {
                results.count = 0
            }

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults?) {

            if (results != null && results.count > 0) {
                mResultList = results.values as ArrayList<AutocompletePrediction>?
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }

        override fun convertResultToString(resultValue: Any): CharSequence {
            return if (resultValue is AutocompletePrediction) {
                resultValue.getFullText(null)
            } else {
                super.convertResultToString(resultValue)
            }
        }
    }

    private fun getAutocomplete(constraint: CharSequence): ArrayList<AutocompletePrediction>? {
        val results = geoDataClient.getAutocompletePredictions(constraint.toString(), bounds,
                placeFilter)
        Tasks.await<AutocompletePredictionBufferResponse>(results, 60, TimeUnit.SECONDS)
//        try {
//            Tasks.await<AutocompletePredictionBufferResponse>(results, 60, TimeUnit.SECONDS)
//        } catch (e: ExecutionException) {
//            e.printStackTrace()
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        } catch (e: TimeoutException) {
//            e.printStackTrace()
//        }

        return try {
            val autocompletePredictions = results.result
            DataBufferUtils.freezeAndClose(autocompletePredictions)
        } catch (e: RuntimeExecutionException) {
            null
        }
    }
}