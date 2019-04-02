package com.kross.taxi_passenger.presentation.dialog

import android.support.design.widget.BottomSheetDialogFragment
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.kross.taxi_passenger.R
import kotlinx.android.synthetic.main.dialog_bottom.*


class BottomDialog : BottomSheetDialogFragment() {

    private lateinit var itemClick: OnClick

    private var idRequest: Int? = null

    private var message: String? = null

    private var flagDeleteButton: Boolean? = null

    companion object {

        fun newInstance(text: String?, flagDeleteButton: Boolean = false): BottomDialog{
            val bundle = Bundle()
            bundle.putString("message", text)
            bundle.putBoolean("flagDeleteButton", flagDeleteButton)
            val fragment = BottomDialog()
            fragment.arguments = bundle
            return fragment
        }

    }

    private val internalListener = View.OnClickListener {
        itemClick.onClickItem(it, idRequest)
        dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        message = arguments?.getString("message")
        flagDeleteButton = arguments?.getBoolean("flagDeleteButton")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_bottom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        message?.let { txtBottomDialog.text = it }
        txtBottomDialog.setOnClickListener(internalListener)

        flagDeleteButton?.let { visibilityDeleteTxt(it) }
    }

    private fun visibilityDeleteTxt(flagDeleteButton: Boolean){
        if(flagDeleteButton){
            txtBottomDialogDelete.visibility = View.VISIBLE
            txtBottomDialogDelete.setOnClickListener(internalListener)
        }
    }

    fun setOnClickListener(listener: OnClick){
        itemClick = listener
    }

    fun setIdRequest(id: Int){
        idRequest = id
    }


    interface OnClick{

        fun onClickItem(view: View, idRequest: Int?)

    }

}