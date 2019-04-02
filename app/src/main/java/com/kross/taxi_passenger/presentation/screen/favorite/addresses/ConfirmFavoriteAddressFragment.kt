package com.kross.taxi_passenger.presentation.screen.favorite.addresses

import android.os.Bundle
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.Coords
import com.kross.taxi_passenger.data.repository.server.pojo.FavoriteAddress
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress

class ConfirmFavoriteAddressFragment : BaseConfirmFavoriteAddressFragment() {
    companion object {
        fun newInstance(address: StubWaypointAddress): ConfirmFavoriteAddressFragment {
            val fragment = ConfirmFavoriteAddressFragment()

            val favoriteAddress = FavoriteAddress(address = address.address, coords = Coords.from(address.latLng), name = null)

            fragment.arguments = Bundle()
                    .apply { putParcelable(ADDRESS_EXTRA, favoriteAddress) }

            return fragment
        }
    }

    override fun getTitle() = R.string.add_the_address

    override fun onSave(address: FavoriteAddress) {
        address.coords = null
        viewModel.addFavoriteAddress(address) {
            fragmentManager?.popBackStack()
        }
    }
}