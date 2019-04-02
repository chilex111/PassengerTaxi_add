package com.kross.taxi_passenger.presentation.screen.cabinet.trips

import android.os.Bundle
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.screen.base.BaseFragment
import com.kross.taxi_passenger.presentation.screen.favorite.TripsPager
import com.kross.taxi_passenger.presentation.screen.favorite.UniversalPagerAdapter
import kotlinx.android.synthetic.main.fragment_favorite_view_pager.*

class TripsPagerFragment: BaseFragment() {

    companion object {
        fun newInstance() = TripsPagerFragment()
    }

    override fun getLayout() = R.layout.fragment_favorite_view_pager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pager.adapter = UniversalPagerAdapter(TripsPager.allValues, requireContext(), childFragmentManager)
        tabLayout.setupWithViewPager(pager)
    }

}