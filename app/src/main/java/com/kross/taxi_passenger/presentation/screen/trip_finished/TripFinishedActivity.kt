package com.kross.taxi_passenger.presentation.screen.trip_finished

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.OrderEntity
import com.kross.taxi_passenger.data.repository.server.pojo.*
import com.kross.taxi_passenger.domain.MapViewModel
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.cabinet.wallet.adapter.ListSuperClass
import com.kross.taxi_passenger.presentation.screen.cabinet.wallet.adapter.WXListAdapter
import com.kross.taxi_passenger.presentation.screen.driver_rate.DriverRateActivity
import com.kross.taxi_passenger.presentation.screen.trip_details.adapter.AddressHolderEnum
import com.kross.taxi_passenger.presentation.screen.trip_details.adapter.TripAdapter
import com.kross.taxi_passenger.presentation.screen.trip_finished_help.HelpActivity
import com.kross.taxi_passenger.utils.visibleOrGone
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_trip_finished.*
import kotlinx.android.synthetic.main.item_add_to_fav_address.*
import org.jetbrains.anko.startActivity
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.HashMap

enum class TripDetailsEnum : ListSuperClass {

    base, time, disatnce, toll;

    override var titleString: String = ""
    override var details: Int = 0
    override var index: Int = ordinal
    override var title: Int = 0
        get() {
            return when (this) {
                base -> R.string.txt_base_fare
                time -> R.string.txt_time
                disatnce -> R.string.txt_distance
                toll -> R.string.txt_toll
            }
        }

}

class FavouriteAddressViewHolder(override val containerView: View,
                                 editListener: (Int, StubWaypointAddress, Boolean) -> Unit) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    private lateinit var point: StubWaypointAddress

    init {
        imageButton.setOnClickListener {
            imageButton.isSelected = !imageButton.isSelected
            imageButton.setImageResource(if (imageButton.isSelected) R.drawable.ic_bookmarked else R.drawable.ic_bookmark_border)

            editListener(adapterPosition, point, imageButton.isSelected)
        }
    }

    fun configure(address: StubWaypointAddress, isLast: Boolean) {
        point = address
        addressTextView.text = address.address
        dots.visibleOrGone(!isLast)
    }

}

const val ORDER_ENTITY: String = "order_entity"

class TripFinishedActivity : BaseActivity() {

    companion object {

        fun start(fragment: Fragment, order: Int) {
            val intent = Intent(fragment.context, TripFinishedActivity::class.java)
            intent.putExtra(ORDER_ENTITY, order)
            fragment.startActivity(intent)
        }
    }

    private var order: OrderEntity? = null
    private val viewModel: MapViewModel by viewModel()
    private var isSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_finished)
        loadOrder()
        toolbarSettings()
        initAdapters()
        buttonHelp.setOnClickListener {
            startActivity<HelpActivity>()
        }
        buttonNext.setOnClickListener {
            order?.id?.let { it1 -> DriverRateActivity.start(this, it1) }
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    fun addToFavouriteTapped(v: View) {
        v.isSelected = !v.isSelected
        isSelected = v.isSelected
        bookmarkImageView.setImageResource(if (v.isSelected) R.drawable.ic_bookmarked else R.drawable.ic_bookmark_border)
        onSaveClicked()
    }

    private fun toolbarSettings() {
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initAdapters() {
        tripRecyclerView.layoutManager = LinearLayoutManager(this)
        pointsRecyclerView.layoutManager = LinearLayoutManager(this)
        tripRecyclerView.adapter = WXListAdapter(TripDetailsEnum.values() as Array<ListSuperClass>, null)
        val pointsAdapter = TripAdapter(this::handleEditTapped, AddressHolderEnum.favourite)
        pointsRecyclerView.adapter = pointsAdapter
        pointsAdapter.setItems(order!!.revertPoints())
    }

    private fun loadOrder() {
        val orderId = intent.getIntExtra(ORDER_ENTITY, 1)
        order = viewModel.getOrderById(orderId)
        order?.costRange
    }

    private var addressId: MutableList<Int> = ArrayList()
    private var routeId = 0

    private fun handleEditTapped(index: Int, point: StubWaypointAddress, isSelected: Boolean) {
        if (isSelected) {
            viewModel.addFavoriteAddress(FavoriteAddress(address = point.address, name = point.streetName)).observe(this, Observer {
                it?.let { it1 -> addressId.add(index, it1) }
                Toast.makeText(this, getString(R.string.adress_added_to_favorite_toast), Toast.LENGTH_LONG).show()
            })
        } else {
            viewModel.deleteFavoriteAddress(addressId[index]) {
                if (addressId.size > 1)
                    addressId.remove(index)
                else addressId.remove(0)
                Toast.makeText(this, getString(R.string.adress_removed_from_favorite), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun onSaveClicked() {
        val krossRoutePoints: MutableList<KrossRoutePoint> = mutableListOf()
        order?.listRoutePoint?.forEach {
            krossRoutePoints.add(KrossRoutePoint(it.street + " " + it.streetNumber, Coords.from(it.position())))
        }
        if (isSelected) {
            viewModel.addFavoriteRoute(FavoriteRoute(KrossRoute(order?.polyline!!, krossRoutePoints))).observe(this, Observer {
                if (it != null) routeId = it
                Toast.makeText(this, getString(R.string.route_added_to_favorite), Toast.LENGTH_LONG).show()
            })
        } else {
            viewModel.deleteFavoriteRoute(routeId)
            Toast.makeText(this, getString(R.string.route_removed_from_favorite), Toast.LENGTH_LONG).show()
        }
    }

    // MARK: - Navigation

}
