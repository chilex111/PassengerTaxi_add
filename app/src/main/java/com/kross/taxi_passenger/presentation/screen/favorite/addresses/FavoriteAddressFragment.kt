package com.kross.taxi_passenger.presentation.screen.favorite.addresses

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.domain.FavoriteViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseFragment
import com.kross.taxi_passenger.presentation.screen.favorite.FavoriteActivity
import com.kross.taxi_passenger.utils.gone
import com.kross.taxi_passenger.utils.visible
import kotlinx.android.synthetic.main.fragment_favorite_addresses.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

//TODO Add pagination
class FavoriteAddressFragment : BaseFragment() {

    companion object {
        fun newInstance() = FavoriteAddressFragment()
    }

    private val viewModel: FavoriteViewModel by sharedViewModel()
    private lateinit var adapter: FavoriteAddressAdapter

    override fun getLayout() = R.layout.fragment_favorite_addresses

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel
                .getFavoriteAddresses(10, 0)
                .observe(this, Observer {
                    if (it == null || it.isEmpty()) {
                        tvTitleAddFavorite.visible()
                        tvSubtitleAddFavorite.visible()
                        rvFavoriteAddresses.gone()
                        return@Observer
                    }

                    tvTitleAddFavorite.gone()
                    tvSubtitleAddFavorite.gone()
                    rvFavoriteAddresses.visible()

                    adapter.setItems(it)
                })

        fab.setOnClickListener {
            viewModel.notifyAddFavoriteAddressClick()
        }

        getSupportActionBar()?.title = getString(R.string.txt_cabinet_my_favorite)

        rvFavoriteAddresses.layoutManager = LinearLayoutManager(context)

        adapter = FavoriteAddressAdapter(
                chooseListener = {
                    (activity as? FavoriteActivity)?.deliverResult(it)
                },
                editListener = {
                    (activity as FavoriteActivity).supportFragmentManager
                            ?.beginTransaction()
                            ?.replace(R.id.container, EditFavoriteAddressFragment.newInstance(it))
                            ?.addToBackStack(null)
                            ?.commit()
                },
                deleteListener = {
                    val id = it.id ?: return@FavoriteAddressAdapter
                    viewModel.deleteFavoriteAddress(id) {
                        viewModel.getFavoriteAddresses(10, 0)
                    }
                })

        rvFavoriteAddresses.adapter = adapter
    }
}