package com.kross.taxi_passenger.presentation.screen.favorite.routes

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.KrossRoutePoint
import com.kross.taxi_passenger.domain.FavoriteViewModel
import com.kross.taxi_passenger.domain.entity.RoutePoint
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.presentation.screen.base.BaseFragment
import com.kross.taxi_passenger.presentation.screen.favorite.FavoriteActivity
import com.kross.taxi_passenger.utils.gone
import com.kross.taxi_passenger.utils.visible
import kotlinx.android.synthetic.main.fragment_favorite_routes.*
import org.jetbrains.anko.onClick
import org.koin.android.viewmodel.ext.android.sharedViewModel

class FavoriteRoutesFragment : BaseFragment() {

    companion object {
        fun newInstance() = FavoriteRoutesFragment()
    }

    private val viewModel: FavoriteViewModel by sharedViewModel()

    override fun getLayout() = R.layout.fragment_favorite_routes



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FavoriteRouteAdapter(
                layoutInflater,
                chooseListener = {
                    val list = createSubWaypointsList(it.routePoints)
                    val id = it.id
                //    it.id?.let { it1 -> viewModel.insertOrder(distance = it1, listRoutePoint = createSubWaypointsList(it.routePoints), polyline = it.polyline, priority = if (it.id == 0) 0 else 1) }
                    (activity as FavoriteActivity).deliverResult(it)
                    viewModel.getDirection(getString(R.string.google_maps_directions_key),
                            list.map { it.latLng },
                            {
                                viewModel.insertOrder(distance = it.first, listRoutePoint =  list, polyline = it.second, priority = if (id == 0) 0 else 1)
                            },
                            { s ->
                                s?.let { showToast(it) }
                            })

                },
                editListener = {
                    activity?.supportFragmentManager
                            ?.beginTransaction()
                            ?.replace(R.id.container, ModifyFavoriteRouteFragment.newInstance(it))
                            ?.addToBackStack(null)
                            ?.commit()

                },
                deleteListener = {
                    it.id ?: return@FavoriteRouteAdapter
                  //  viewModel.insertOrder(it.id,)
                    viewModel.deleteFavoriteRoute(it.id)
                })

        viewModel.getFavoriteRoutes(100, 0)
                .observe(this, Observer {
                    if (it == null || it.isEmpty()) {
                        rvFavoriteRoute.gone()
                        tvTitleAddRoute.visible()
                        tvSubtitleAddRoute.visible()
                        return@Observer
                    }

                    rvFavoriteRoute.visible()
                    tvTitleAddRoute.gone()
                    tvSubtitleAddRoute.gone()
                    adapter.setItems(it)
                  // showToast(it.toString())
                })
        rvFavoriteRoute.layoutManager = LinearLayoutManager(context)
        rvFavoriteRoute.adapter = adapter

        fab.onClick {
            activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.container, ModifyFavoriteRouteFragment.newInstance())
                    ?.addToBackStack(null)
                    ?.commit()
        }
    }

    fun createSubWaypointsList(routes: List<KrossRoutePoint>): List<StubWaypointAddress>{
      val  addresses: MutableList<StubWaypointAddress> = mutableListOf()
        for(item in  routes){
            addresses.add(StubWaypointAddress.from(item))
        }
        return addresses
    }
}