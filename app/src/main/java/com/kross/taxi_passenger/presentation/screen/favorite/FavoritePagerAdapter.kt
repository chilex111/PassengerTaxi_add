package com.kross.taxi_passenger.presentation.screen.favorite

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.screen.base.BaseFragment
import com.kross.taxi_passenger.presentation.screen.cabinet.trips.past.PastTripsPagerFragment
import com.kross.taxi_passenger.presentation.screen.cabinet.trips.scheduled.ScheduledTripsPagerFragment
import com.kross.taxi_passenger.presentation.screen.favorite.addresses.FavoriteAddressFragment
import com.kross.taxi_passenger.presentation.screen.favorite.routes.FavoriteRoutesFragment

interface PagerInterface {

    val item: BaseFragment
    val title: Int

    // using companion object in descendants, because enum cannot be init
    // in case we can use object with enum inside

}

enum class FavouritePager: PagerInterface {

    address, route;

    companion object {
        val allValues = values() as Array<PagerInterface>
    }

    override val item: BaseFragment
        get() {
            return when (this) {
                address -> FavoriteAddressFragment.newInstance()
                route -> FavoriteRoutesFragment.newInstance()
            }
        }

    override val title: Int
        get() {
            when(this) {
                address -> return R.string.tab_addresses
                route -> return R.string.tab_routes
            }
        }
}

enum class TripsPager: PagerInterface {

    past, scheduled;

    companion object {
        val allValues = values() as Array<PagerInterface>
    }

    override val item: BaseFragment
        get() {
            return when(this) {
                past -> PastTripsPagerFragment.newInstance()
                scheduled -> ScheduledTripsPagerFragment.newInstance()
            }
        }

    override val title: Int
        get() {
            return when(this) {
                past -> R.string.title_past_tab
                scheduled -> R.string.title_scheduled_tab
            }
        }

}

class UniversalPagerAdapter( private val pager: Array<PagerInterface>,
                             val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? = pager[position].item

    override fun getCount() = pager.size

    override fun getPageTitle(position: Int): String = context.getString(pager[position].title)

}
