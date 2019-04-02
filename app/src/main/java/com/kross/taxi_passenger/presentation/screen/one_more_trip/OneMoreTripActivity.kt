package com.kross.taxi_passenger.presentation.screen.one_more_trip

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.provider.ContactsContract
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.InputFilter
import android.view.MenuItem
import android.view.View
import com.fondesa.kpermissions.extension.onAccepted
import com.fondesa.kpermissions.extension.onDenied
import com.fondesa.kpermissions.extension.onPermanentlyDenied
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.OrderEntity
import com.kross.taxi_passenger.domain.MapViewModel
import com.kross.taxi_passenger.domain.entity.CarType
import com.kross.taxi_passenger.domain.entity.PaymentMenthod
import com.kross.taxi_passenger.domain.entity.StubWaypointAddress
import com.kross.taxi_passenger.presentation.dialog.BottomDialogChoseTripOwner
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import com.kross.taxi_passenger.presentation.screen.leave_note_and_passenger_number.NoteAndNumberActivity
import com.kross.taxi_passenger.presentation.screen.main.map.MapFragment
import com.kross.taxi_passenger.presentation.screen.payment.choose.ChoosePaymentActivity
import com.kross.taxi_passenger.presentation.screen.time_trip.SetTimeTripActivity
import com.kross.taxi_passenger.presentation.screen.trip_details.adapter.TripAdapter
import com.kross.taxi_passenger.utils.*
import kotlinx.android.synthetic.main.activity_one_more_trip.*
import kotlinx.android.synthetic.main.dialog_bottom_car.*
import org.jetbrains.anko.image
import org.koin.android.viewmodel.ext.android.viewModel

const val BOTTOM_TAG: String = "tag"

class OneMoreTripActivity: BaseActivity(), BottomDialogChoseTripOwner.OnClick, View.OnClickListener {

    private lateinit var pointsAdapter: TripAdapter
    private lateinit var order: OrderEntity
    private val viewModel: MapViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_more_trip)
        initAppBar()
        initViews()
        addListeners()
        loadOrderData()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        overridePendingTransition(R.anim.slide_in_left_to_right, R.anim.slide_in_right_to_left)
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && data != null){
            when (requestCode) {
                MapFragment.CHOOSE_PAYMENT_REQUEST_CODE ->  handleResponsePaymentRequest(data)
                MapFragment.CHOOSE_DATE_TRIP_REQUEST_CODE -> handleResponseTimeTripRequest(data)
                MapFragment.CHOOSE_NUMBER_REQUEST_CODE -> { handleResponseEnterPhoneNumber(data) }
                MapFragment.PICK_CONTACT_REQUEST_CODE -> { handleResponseContact(data) }
            }
        }
    }

    // MARK: - Actions

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.icFacePurple -> forMe()
            R.id.icFaceWhite -> showBottomSheetDialogNumber()
            R.id.txtBottomDialog -> { requestContact() }
            R.id.txtBottomDialogDelete -> NoteAndNumberActivity.start(this, MapFragment.CHOOSE_NUMBER_REQUEST_CODE, "")
            R.id.ivCarType -> handleCarTypeSelection()
            R.id.ivTime -> SetTimeTripActivity.start(this, MapFragment.CHOOSE_DATE_TRIP_REQUEST_CODE)
            R.id.ivCard -> ChoosePaymentActivity.start(this, MapFragment.CHOOSE_PAYMENT_REQUEST_CODE)
            R.id.orderButton -> postNewOrder()
        }
    }

    override fun onClickItem(view: View) {
        onClick(view)
    }

    // MARK: - Helper methods

    private fun addListeners() {
        icFaceWhite.setOnClickListener(this)
        icFacePurple.setOnClickListener(this)
        ivCarType.setOnClickListener(this)
        ivTime.setOnClickListener(this)
        ivCard.setOnClickListener(this)
        inputText.filters = arrayOf(InputFilter.LengthFilter(40))
    }

    private fun initAppBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_black)
    }

    private fun initViews() {
        tableView.layoutManager = LinearLayoutManager(this)
        pointsAdapter = TripAdapter(this::handleEditTapped)
  //      recyclerView.setNestedScrollingEnabled(false)
        tableView.adapter = pointsAdapter
        initView(null)
    }

    private fun loadOrderData() {
        viewModel.getOrder().observe(this, Observer {
            if (it != null && it.isNotEmpty()) {
                order = it.filter {
                    it.priority == 1
                }.first()
                initView(order.orderFor)
                pointsAdapter.setItems(order.revertPoints())
            }
        })
    }

    private fun initView(chosenItem: String?){
        icFacePurple.isActivated = chosenItem == null
        icFaceWhite.isActivated = chosenItem != null
        txtSomeoneElse.isActivated = chosenItem != null
        txtMe.isActivated = chosenItem == null
        txtSomeoneElse.text = chosenItem ?: txtSomeoneElse.context.getString(R.string.txt_someone_else)
        txtSomeoneElse.updateTypeface( if(chosenItem == null) Typeface.NORMAL else Typeface.BOLD)
        txtMe.updateTypeface( if(chosenItem == null) Typeface.BOLD else Typeface.NORMAL)
        icFacePurple.requestLayout()
        icFaceWhite.requestLayout()
    }

    private fun forMe() {
        viewModel.updateOrderFor(order.id, null)
    }

    private fun showBottomSheetDialogNumber(){
        val dialogChoseTripOwner = BottomDialogChoseTripOwner.newInstance()
        dialogChoseTripOwner.setOnClickListener(this)
        dialogChoseTripOwner.show(supportFragmentManager, BOTTOM_TAG)
    }

    private fun requestContact(){
        val request = permissionsBuilder(Manifest.permission.READ_CONTACTS).build()
        request.onAccepted { openContact() }
                .onDenied { showSnack(getString(R.string.txt_permission_contact_denied)) }
                .onPermanentlyDenied { showSnackBarSetting() }
        request.send()
    }

    private fun openContact(){
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, MapFragment.PICK_CONTACT_REQUEST_CODE)
    }

    private fun showSnackBarSetting(){
        showSnackBarAction( getString(R.string.txt_permission_contact_denied),
                getString(R.string.txt_setting),
                { viewModel.openApplicationSettings(this, 1) })
    }

    private fun handleResponseEnterPhoneNumber(data: Intent){
        val phoneNumber = data.extras.getString(NoteAndNumberActivity.PHONE_CODE_KEY)
        viewModel.updateOrderFor(order.id, phoneNumber)
    }

    private fun handleResponseContact(data: Intent){
        val phoneNumber = viewModel.getContactInfo(data, this)
        viewModel.updateOrderFor(order.id, phoneNumber)
    }

    private fun handleResponsePaymentRequest(data: Intent){
        val paymentMethod: PaymentMenthod = data.argOrThrow(ChoosePaymentActivity.PAYMENT_TYPE_EXTRA)
        ivCard.image = getDrawable(paymentMethod.image)
        if(paymentMethod == PaymentMenthod.CARD) {
            val card = data.extras.getLong(ChoosePaymentActivity.CARD_RESULT_EXTRA)
            val cardNumber = card.toString()
            viewModel.updatePaymentTypeAndCard(paymentMethod.paymentMethod, cardNumber, order.id)
            tvCard.text = "**${cardNumber.substring(cardNumber.length - 4)}"
        } else {
            tvCard.text = getString(paymentMethod.shortDescription)
            viewModel.updateOrderPaymentType(order.id, paymentMethod.paymentMethod)
        }
    }

    private fun handleCarTypeSelection() {
        val actionSheet = BottomSheetDialog(this)
        actionSheet.setContentView(R.layout.dialog_bottom_car)
        actionSheet.standardLayout.setOnClickListener {
            updateCarType(CarType.STANDARD)
            actionSheet.dismiss()
        }
        actionSheet.premiumLayout.setOnClickListener {
            updateCarType(CarType.PREMIUM)
            actionSheet.dismiss()
        }
        actionSheet.show()
    }

    private fun updateCarType(carType: CarType) {
        ivCarType.setImageResource(carType.image)
        tvCarType.setText(carType.title)
    }

    private fun handleResponseTimeTripRequest(data: Intent){
        tvTime.text = data.extras.getString(SetTimeTripActivity.TIME_DATE_TRIP) ?: getString(R.string.now)
    }

    private fun postNewOrder() {
        // TODO: - connect API to post second order
    }

    private fun handleEditTapped(index: Int, point: StubWaypointAddress, isSeleckted: Boolean) {
    }

}