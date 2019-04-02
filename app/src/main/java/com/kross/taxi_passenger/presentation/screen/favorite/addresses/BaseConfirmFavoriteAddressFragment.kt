package com.kross.taxi_passenger.presentation.screen.favorite.addresses

import android.os.Bundle
import android.support.annotation.StringRes
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.FavoriteAddress
import com.kross.taxi_passenger.domain.FavoriteViewModel
import com.kross.taxi_passenger.managers.LastPointManager
import com.kross.taxi_passenger.presentation.screen.base.BaseFragment
import com.kross.taxi_passenger.utils.LocationHelper
import com.kross.taxi_passenger.utils.getPlaceById
import com.kross.taxi_passenger.utils.toBounds
import kotlinx.android.synthetic.main.fragment_confirm_favorite_addresses.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

abstract class BaseConfirmFavoriteAddressFragment: BaseFragment() {

    companion object {
        const val ADDRESS_EXTRA = "ADDRESS_EXTRA"
    }

    protected val viewModel: FavoriteViewModel by sharedViewModel()
    private lateinit var address: FavoriteAddress
    private lateinit var adapter: PlaceAutocompleteAdapter

    override fun getLayout() = R.layout.fragment_confirm_favorite_addresses

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSupportActionBar()?.title = getString(getTitle())

        address = argOrThrow(ADDRESS_EXTRA)

        tvAddress.setText(address.address)

        adapter = PlaceAutocompleteAdapter(
                context!!,
                viewModel.geoDataClient,
                LastPointManager.currentPoint?.toBounds(),
                LocationHelper.addressAutocompleteFilter)

        tvAddress.setAdapter(adapter)

        tvAddress.setOnItemClickListener { _, _, position, _ ->
            val placeId = adapter.getItem(position).placeId ?: return@setOnItemClickListener

            viewModel.geoDataClient.getPlaceById(placeId) {
                it?.let {
                    address.fill(it)
                }
            }
        }

        tvAddress.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) tvAddress.setText(address.address)
        }

        btnSave.setOnClickListener {
            address.name = if(tvName.text.isBlank()) null else tvName.text.toString()
            onSave(address)
        }

        tvName.setText(address.name)
    }

    @StringRes
    abstract fun getTitle(): Int

    abstract fun onSave(address: FavoriteAddress)
}