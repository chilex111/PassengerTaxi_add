package com.kross.taxi_passenger.presentation.screen.onboarding

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class OnboardingAdapter(val list: List<Benefit>, fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
       return OnboardingFragment.newInstance(list.get(position))
    }

    override fun getCount(): Int {
        return list.size
    }
}