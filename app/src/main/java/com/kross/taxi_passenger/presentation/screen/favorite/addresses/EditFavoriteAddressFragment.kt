package com.kross.taxi_passenger.presentation.screen.favorite.addresses

import android.os.Bundle
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.FavoriteAddress

class EditFavoriteAddressFragment: BaseConfirmFavoriteAddressFragment() {
    companion object {
        fun newInstance(address: FavoriteAddress): EditFavoriteAddressFragment {
            val fragment = EditFavoriteAddressFragment()
            fragment.arguments = Bundle()
                    .apply { putParcelable(BaseConfirmFavoriteAddressFragment.ADDRESS_EXTRA, address) }

            return fragment
        }
    }

    override fun getTitle() = R.string.edit_the_address

    override fun onSave(address: FavoriteAddress) =
            viewModel.editFavoriteAddress(address) {
                fragmentManager?.popBackStack()
            }
}