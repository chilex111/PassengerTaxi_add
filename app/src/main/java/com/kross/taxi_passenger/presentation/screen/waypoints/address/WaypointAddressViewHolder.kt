package com.kross.taxi_passenger.presentation.screen.waypoints.address

import android.support.v7.widget.RecyclerView
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.utils.onTextChanged
import com.kross.taxi_passenger.utils.visibleOrGone
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_waypoint_address.*

class WaypointAddressViewHolder(override val containerView: View,
                                onCloseListener: (Int) -> Unit,
                                onAddressChangeListener: (String, Int) -> Unit,
                                focusChangeListener: (Int) -> Unit) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    private var addressEntity :StubWaypointAddress? = null

    private var ignoreUpdate: Boolean = false

    // TODO: - connect to obsrever(after merge) in extension,
    // use debounce to handle text changes with timer

    init {
        etAddress.onTextChanged {
            if(ignoreUpdate) return@onTextChanged
            onAddressChangeListener.invoke(it, adapterPosition)
        }
        ivClose.setOnClickListener { onCloseListener.invoke(adapterPosition) }
        etAddress.setOnFocusChangeListener { _, hasFocus -> if(hasFocus) focusChangeListener.invoke(adapterPosition) }
    }

    fun bind(anAddressEntity: StubWaypointAddress?, isLast: Boolean) {
        ivDots.visibleOrGone(!isLast)
        val resources = etAddress.resources

        etAddress.hint = when (adapterPosition) {
            0 -> resources.getString(R.string.label_from)
            1 -> resources.getString(R.string.label_to)
            else -> resources.getString(R.string.label_add_route_point)
        }

        addressEntity = anAddressEntity
        updateAddress(addressEntity)
    }

    private fun updateAddress(addressEntity: StubWaypointAddress?) {
        ivClose.visibleOrGone(addressEntity != null)

        ignoreUpdate = true
        etAddress.setText(addressEntity?.address)
        ignoreUpdate = false
    }

    fun reset() = updateAddress(addressEntity)
}