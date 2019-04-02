package com.kross.taxi_passenger.presentation.screen.waypoints.suggestion

import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.View
import android.widget.TextView
import com.google.android.gms.location.places.AutocompletePrediction
import com.kross.taxi_passenger.R

class SuggestionViewHolder(view: View, private val clickListener: (Int) -> Unit): RecyclerView.ViewHolder(view) {
    private val addressLine1: TextView = view.findViewById(R.id.tv_address_line_1)
    private val addressLine2: TextView = view.findViewById(R.id.tv_address_line_2)

    init {
        view.setOnClickListener { clickListener.invoke(adapterPosition) }
    }

    fun bind(suggestion: AutocompletePrediction, search: String?) {
        addressLine1.text = suggestion.getPrimaryText(null)
        addressLine2.text = suggestion.getSecondaryText(null)

        search?.let {
            highlightSearch(addressLine1, search)
            highlightSearch(addressLine2, search)
        }
    }

    private fun highlightSearch(textView: TextView, search: String) {
        val originalString = textView.text.toString()

        val startPos = originalString.toLowerCase().indexOf(search.toLowerCase())
        val endPos = startPos + search.length

        if (startPos != -1) {
            val spannable = SpannableString(originalString)
            val span = TextAppearanceSpan(textView.context, R.style.SearchTextSpan)
            spannable.setSpan(span, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            textView.text = spannable
        }
    }
}