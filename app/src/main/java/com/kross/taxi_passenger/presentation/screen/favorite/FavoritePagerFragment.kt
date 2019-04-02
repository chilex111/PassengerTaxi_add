package com.kross.taxi_passenger.presentation.screen.favorite

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.domain.FavoriteViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_favorite_view_pager.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class FavoritePagerFragment: BaseFragment() {

    companion object {
        fun newInstance() = FavoritePagerFragment()
    }

    private val viewModel: FavoriteViewModel by sharedViewModel()

    override fun getLayout() = R.layout.fragment_favorite_view_pager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pager.adapter = UniversalPagerAdapter(FavouritePager.allValues, requireContext(), childFragmentManager)
        tabLayout.setupWithViewPager(pager)

        viewModel.errorLiveData.observe(this, Observer { it ->
            it?.let { showToast(it) }
        })
    }
}