package com.kross.taxi_passenger.presentation.screen.main.bottom

import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import org.jetbrains.anko.layoutInflater

abstract class BottomDialog(val parent: ViewGroup, layoutId: Int): LayoutContainer {

    final override val containerView: View

    init {
        parent.removeAllViews()
        containerView = parent.context.layoutInflater.inflate(layoutId, parent, true)
    }
}