package com.kross.taxi_passenger.presentation.screen.driver_rate.adapter

import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.screen.driver_rate.FinishRate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_rate.*
import org.jetbrains.anko.forEachChild
import org.jetbrains.anko.image

enum class DriverRate {

    overall, image, header, air, car, driver;

    companion object {

        val allValues: List<DriverRate> = listOf(overall, image, header, air, car, driver, image)
        val cutValues: List<DriverRate> = listOf(overall, image)

    }

    var title: Int = 0
    get() {
        return when (this) {
            overall -> R.string.txt_overral_rating
            air -> R.string.txt_air_condition
            car -> R.string.txt_car_neatness
            driver -> R.string.txt_attitude
            else -> 0
        }
    }

    var layout: Int = 0
    get() {
        return when (this) {
            overall, air, car, driver -> R.layout.item_rate
            image -> R.layout.item_dotted_image
            header -> R.layout.item_header_title
        }
    }

}

class ItemRate(override val containerView: View,
               private val finishRate: FinishRate,
               private val listener: (Boolean) -> Unit): RecyclerView.ViewHolder(containerView), LayoutContainer {

    private val MAXIMUM: Int = 5 // maximum rate
    private val MINIMUM: Int = 1 // minimum rate
    private val ANGLE: Float = 72f // 360 - circle / 5 angles of a star
    private val DURATION: Long = 200

    private lateinit var globalRate: DriverRate

    fun configure(rate: DriverRate, stars: Int) {
        globalRate = rate
        textView?.text = containerView.context.getString(rate.title)
        textView?.setOnClickListener {
            fillStars(0)
        }
        fillStars(stars)
    }

    private fun fillStars(active: Int) {
        val context = containerView.context
        if (globalRate == DriverRate.overall && finishRate.overall != active) {
            finishRate.changeValueAt(globalRate, active)
            listener.invoke(active != 0)
        }
        finishRate.changeValueAt(globalRate, active)
        starsLayout?.forEachChild {
            val imageView = it as ImageView
            val tag = it.tag.toString().toInt()
            val isBigger = tag > active
            if (isBigger) {
                Handler().postDelayed({
                    imageView.animate().rotation(-ANGLE).setDuration(DURATION).setInterpolator(LinearInterpolator()).start()
                    imageView.image = context.getDrawable(R.drawable.ic_star_empty)
                }, (MAXIMUM - tag).toLong() * DURATION)
            } else {
                Handler().postDelayed({
                    imageView.animate().rotation(ANGLE).setDuration(DURATION).setInterpolator(LinearInterpolator()).start()
                    imageView.image = context.getDrawable(R.drawable.ic_star_filled)
                }, (tag - MINIMUM).toLong() * DURATION)
            }
            imageView.setOnClickListener {
                fillStars(it.tag.toString().toInt())
            }
        }
    }

}

class RateAdapter( private val rate: FinishRate,
                   private val onRateListener: (Boolean) -> Unit): RecyclerView.Adapter<ItemRate>() {

    private var array: List<DriverRate> = DriverRate.cutValues
    set(value) {
        if (value != array) {
            field = value
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = array.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemRate {
        LayoutInflater
                .from(parent.context)
                .inflate(array[viewType].layout, parent, false)
                .let {
                    return ItemRate(it, rate, onRateListener)
                }
    }

    override fun onBindViewHolder(holder: ItemRate, position: Int) {
        holder.configure(array[position], rate.valueAt(array[position]))
    }

    fun changeUI(showAll: Boolean) {
        array = if (showAll) DriverRate.allValues else DriverRate.cutValues
    }

}