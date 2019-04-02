package com.kross.taxi_passenger.presentation.screen.payment.mycards

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.data.repository.database.entity.Card
import com.kross.taxi_passenger.domain.MyCardsViewModel
import com.kross.taxi_passenger.presentation.screen.add_card.AddCardActivity
import com.kross.taxi_passenger.presentation.screen.base.BaseFragment
import com.kross.taxi_passenger.utils.gone
import com.kross.taxi_passenger.utils.visible
import kotlinx.android.synthetic.main.fragment_my_cards.*
import org.jetbrains.anko.AlertDialogBuilder
import org.jetbrains.anko.onClick
import org.koin.android.viewmodel.ext.android.viewModel

class MyCardsFragment : BaseFragment() {

    companion object {
        fun newIntstance() = MyCardsFragment()
    }

    private val viewModel: MyCardsViewModel by viewModel()
    lateinit var adapter: MyCardsAdapter

    override fun getLayout() = R.layout.fragment_my_cards

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MyCardsAdapter(
                defaultCardListener = { viewModel.setDefaultCard(it) },
                deleteCardListener = { handleDeleteCard(it) },
                chooseListener = { deliverResult(it) })

        viewModel.getUserCards().observe(this, Observer {
            if (it == null || it.isEmpty()) {
                emptyState.visible()
                rvCards.gone()
            } else {
                emptyState.gone()
                rvCards.visible()
                adapter.setCards(it.sortedBy { !it.defaultFlag })
            }
        })

        rvCards.adapter = adapter
        rvCards.layoutManager = LinearLayoutManager(context)

        fab.onClick {
            activity?.let {
                AddCardActivity.startForMyCards(it)
            }
        }
    }

    private fun handleDeleteCard(it: Card) =
            AlertDialog
                .Builder(context)
                .setMessage(getString(R.string.delete_card_placeholder, it.getShortNumber()))
                .setPositiveButton(R.string.yes) { dialog: DialogInterface, _: Int ->
                    viewModel.deleteCard(it)
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.no) { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }
                .show()


    private fun deliverResult(it: Card) {
        (activity as? MyCardsActivity)?.deliverResult(it)
    }
}