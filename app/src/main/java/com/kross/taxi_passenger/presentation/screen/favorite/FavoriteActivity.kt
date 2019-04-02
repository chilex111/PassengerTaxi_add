package com.kross.taxi_passenger.presentation.screen.favorite

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.MenuItem
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.server.pojo.FavoriteAddress
import com.kross.taxi_passenger.data.repository.server.pojo.KrossRoute
import com.kross.taxi_passenger.domain.FavoriteViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.favorite.addresses.AddFavoriteAddressFragment
import com.kross.taxi_passenger.presentation.screen.favorite.routes.ModifyFavoriteRouteFragment
import com.kross.taxi_passenger.utils.visibleOrGone
import kotlinx.android.synthetic.main.activity_container_toolbar.*
import org.koin.android.viewmodel.ext.android.viewModel

class FavoriteActivity : BaseActivity() {
    companion object {
        const val ADDRESS_RESPONSE_EXTRA = "ADDRESS_RESPONSE_EXTRA"
        const val ROUTE_RESPONSE_EXTRA = "ROUTE_RESPONSE_EXTRA"

        fun newInstance(fragment: Fragment, requestCode: Int) {
            val intent = Intent(fragment.activity, FavoriteActivity::class.java)
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    private val viewModel: FavoriteViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_container_toolbar)

        if (savedInstanceState == null) openFavoritePagerFragment()

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)

        appBarLayout.post { appBarLayout.elevation = .0f }

        viewModel.addFavoriteAddressListener.observe(this, Observer {
            if (it != null) {
                openAddFavoriteAddressFragment()
            }
        })

        viewModel.addFavoriteRouteListener.observe(this, Observer {
            if (it != null) {
                openAddFavoriteRouteFragment()
            }
        })

        viewModel.progressLiveData.observe(this, Observer {
            it?.let {
                progressBar.visibleOrGone(it)
            }
        })
    }

    private fun openFavoritePagerFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, FavoritePagerFragment.newInstance())
                .addToBackStack(null)
                .commit()
    }

    private fun openAddFavoriteAddressFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, AddFavoriteAddressFragment.newInstance())
                .addToBackStack(null)
                .commit()
    }

    private fun openAddFavoriteRouteFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, ModifyFavoriteRouteFragment.newInstance())
                .addToBackStack(null)
                .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    fun deliverResult(address: FavoriteAddress) =
            Intent()
                    .apply { putExtra(ADDRESS_RESPONSE_EXTRA, address) }
                    .finishWithResult()

    fun deliverResult(route: KrossRoute) =
            Intent()
                    .apply { putExtra(ROUTE_RESPONSE_EXTRA, route) }
                    .finishWithResult()

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
        overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
    }
}