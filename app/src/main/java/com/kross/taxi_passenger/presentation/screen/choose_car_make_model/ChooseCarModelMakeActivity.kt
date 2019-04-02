package com.kross.taxi_passenger.presentation.screen.choose_car_make_model

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.domain.CarOwnerRegistrationViewModel
import com.kross.taxi_passenger.domain.ChooseCarMakeViewModel
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.car_owner_registration.CarOwnerRegistrationActivity
import com.kross.taxi_passenger.presentation.widget.BaseAdapter
import com.kross.taxi_passenger.utils.ErrorHelper
import com.kross.taxi_passenger.utils.showSnack
import com.test.client_taxi_kross_driver_android.data.server.pojo.response.Make
import com.test.client_taxi_kross_driver_android.data.server.pojo.response.ModelCar
import com.test.client_taxi_kross_driver_android.presentation.ui.choose_car_make_model.AdapterChooseCarModel
import com.test.client_taxi_kross_driver_android.presentation.ui.choose_car_make_model.AdapterChoseCarMake
import kotlinx.android.synthetic.main.activity_choose_car_model_make.*
import kotlinx.android.synthetic.main.empty_view.*
import org.koin.android.viewmodel.ext.android.getViewModel

class ChooseCarModelMakeActivity : BaseActivity(), SearchView.OnQueryTextListener,
        BaseAdapter.OnItemClickListener<Make>, AdapterChooseCarModel.OnItemClickListener{


    private var carMake: String? = null

    private var lisrCareMake:List<Make>? = null

    private var lisrCareModel:List<ModelCar>? = null

    private lateinit var viewModel: ChooseCarMakeViewModel

    private val adapterChoseCarMake = AdapterChoseCarMake()

    private val adapterChoseCarModel = AdapterChooseCarModel(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_car_model_make)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white)
        viewModel = getViewModel()

        val intent = getIntent()
        carMake = intent.getStringExtra(resources.getString(R.string.KEY_INTENT_CARE_MAKE))
        rvCarMakeModel.layoutManager = LinearLayoutManager(this)

        if(carMake == null){
            obtainListCareMake()
            rvCarMakeModel.adapter = adapterChoseCarMake
            adapterChoseCarMake.setOnClickListener(this)
        }else{
            supportActionBar?.setTitle(R.string.txt_choose_car_model_toolbar_title)
            carMake?.let { obtainListCareModel(it) }
            rvCarMakeModel.adapter = adapterChoseCarModel
        }
    }

    fun obtainListCareMake(){
        viewModel.getListCarMake()
        viewModel.getLiveDataCarMake().observe(this, Observer {
            it?.let { adapterChoseCarMake.addAll(it)
                lisrCareMake = it}
        })

        viewModel.getLiveDataError().observe(this, Observer {
            it?.let {
                showSnack(ErrorHelper.parseErrorAndGetString(this, it))
                rvCarMakeModel.visibility = View.GONE
                emptyView.visibility = View.VISIBLE}
        })

        viewModel.getLiveDataLoading().observe(this, Observer {
            it?.let { progressBarChooseCareMake.visibility = it }
        })
    }

    fun obtainListCareModel(carMake: String){
        viewModel.getListCarModel(carMake)
        viewModel.getLiveDataCarModel().observe(this, Observer {
            it?.let {
                adapterChoseCarModel.addAll(it)
                lisrCareModel = it
            }
        })

        viewModel.getLiveDataError().observe(this, Observer {
            it?.let {
                showSnack(ErrorHelper.parseErrorAndGetString(this, it))
                rvCarMakeModel.visibility = View.GONE
                emptyView.visibility = View.VISIBLE}
        })

        viewModel.getLiveDataLoading().observe(this, Observer {
            it?.let { progressBarChooseCareMake.visibility = it }
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean { return false }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let {
            lisrCareMake?.let { filterListMake(newText) }
            lisrCareModel?.let { filterListModel(newText) }

        }
        return false
    }

    fun filterListMake(text: String){
        if(adapterChoseCarMake.filter(text, lisrCareMake)){
            emptyView.visibility = View.GONE
            rvCarMakeModel.visibility = View.VISIBLE
        }else{
            emptyView.visibility = View.VISIBLE
            rvCarMakeModel.visibility = View.GONE
        }
    }

    fun filterListModel(text: String){
        if(adapterChoseCarModel.filter(text, lisrCareModel)){
            emptyView.visibility = View.GONE
            rvCarMakeModel.visibility = View.VISIBLE
        }else{
            emptyView.visibility = View.VISIBLE
            rvCarMakeModel.visibility = View.GONE
        }
    }

    override fun onItemClick(view: View, make: Make) {
        val intent = Intent(this, CarOwnerRegistrationActivity::class.java)
        intent.putExtra(resources.getString(R.string.KEY_INTENT_CARE_MAKE_MODEL), make.makeName)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onItemClick(view: View, carModel: ModelCar) {
        val intent = Intent(this,  CarOwnerRegistrationActivity::class.java)
        intent.putExtra(resources.getString(R.string.KEY_INTENT_CARE_MAKE_MODEL), carModel.modelName)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = getString(R.string.txt_choose_car_make_hint_search)
        searchView.setOnQueryTextListener(this)
        searchView.isIconified = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}