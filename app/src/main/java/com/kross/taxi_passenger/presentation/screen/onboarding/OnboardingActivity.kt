package com.kross.taxi_passenger.presentation.screen.onboarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.view.ViewPager
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.screen.base.BaseActivity
import kotlinx.android.synthetic.main.activity_onboarding.*
import kotlinx.android.synthetic.main.fragment_onboarding.view.*


class OnboardingActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        viewPagerOnboarding.adapter = OnboardingAdapter(getBenefit(), supportFragmentManager)
        viewPagerOnboarding.addOnPageChangeListener(pageListener)
    }

    private val pageListener = object : ViewPager.SimpleOnPageChangeListener() {

        override fun onPageSelected(position: Int) {
            when(position){
                0 -> radioGroup.check(R.id.itemSelectorFirst)
                1 -> radioGroup.check(R.id.itemSelectorSecond)
                2 -> radioGroup.check(R.id.itemSelectorThird)
            }
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
//            val imageView = viewPagerOnboarding.imageView
//            imageView.x = positionOffsetPixels.toFloat() * 2
        }

    }

    private fun getBenefit(): List<Benefit>{
        return listOf(Benefit.COMFY, Benefit.OPPORTUNITIES, Benefit.BONUS)
    }

    companion object {
        fun start(activity: Activity){
            val intent = Intent(activity, OnboardingActivity::class.java)
            activity.startActivity(intent)
        }
    }

}