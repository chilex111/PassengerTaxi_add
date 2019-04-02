package com.kross.taxi_passenger.presentation.screen.onboarding

import android.support.annotation.DrawableRes
import android.support.annotation.IntegerRes
import android.support.annotation.StringRes
import com.kross.taxi_passenger.R

enum class Benefit(@StringRes val title: Int,
                   @StringRes val description: Int,
                   @DrawableRes val image: Int,
                   @IntegerRes val index: Int) {

    COMFY(R.string.title_onboarding_first, R.string.description_onboarding_first, R.drawable.onboarding_1_bonuses, 0),
    OPPORTUNITIES(R.string.title_onboarding_second, R.string.description_onboarding_second, R.drawable.onboarding_1_bonuses, 1),
    BONUS(R.string.title_onboarding_third, R.string.description_onboarding_third, R.drawable.onboarding_1_bonuses, 2)
}