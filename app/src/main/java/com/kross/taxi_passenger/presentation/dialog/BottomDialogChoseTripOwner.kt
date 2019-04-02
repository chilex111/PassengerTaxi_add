package com.kross.taxi_passenger.presentation.dialog

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.utils.visible
import kotlinx.android.synthetic.main.dialog_bottom.*
import org.jetbrains.anko.textColor

class BottomDialogChoseTripOwner : BottomSheetDialogFragment() {

    private lateinit var itemClick: OnClick


    companion object {
        fun newInstance(): BottomDialogChoseTripOwner{
            val fragment = BottomDialogChoseTripOwner()
            return fragment
        }
    }

    private val internalListener = View.OnClickListener {
        itemClick.onClickItem(it)
        dismiss()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_bottom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        txtBottomDialog.text = getString(R.string.txt_choose_from_phonebook)
        txtBottomDialogDelete.text = getString(R.string.txt_enter_number)
        txtBottomDialogDelete.textColor = ContextCompat.getColor(view.context, R.color.text_view_selectable)
        txtBottomDialogDelete.visible()
        txtBottomDialog.setOnClickListener(internalListener)
        txtBottomDialogDelete.setOnClickListener(internalListener)
    }

    fun setOnClickListener(listener: OnClick){
        itemClick = listener
    }

    interface OnClick {

        fun onClickItem(view: View)

    }
}