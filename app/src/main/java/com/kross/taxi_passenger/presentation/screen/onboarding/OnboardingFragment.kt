package com.kross.taxi_passenger.presentation.screen.onboarding

import android.os.Bundle
import android.view.View
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.presentation.screen.base.BaseFragment
import com.kross.taxi_passenger.presentation.screen.login.LoginActivity
import com.kross.taxi_passenger.utils.saveToSharedPreference
import kotlinx.android.synthetic.main.fragment_onboarding.*
import org.jetbrains.anko.displayMetrics


const val BENEFIT_KEY = "benefit"

class OnboardingFragment : BaseFragment(), View.OnClickListener{

    private lateinit var benefit: Benefit

    companion object {

        fun newInstance(benefit: Benefit): OnboardingFragment {
            val bundle = Bundle()
            bundle.putString(BENEFIT_KEY, benefit.name)
            val fragment = OnboardingFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(BENEFIT_KEY)?.let { benefit = Benefit.valueOf(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtOnboardingSkip.setOnClickListener(this)
        imageOnboarding.setImageResource(benefit.image)
        txtTitleOnboarding.setText(benefit.title)
        txtDescriptionOnboarding.setText(benefit.description)
//        when (benefit.index) {
//            0 -> imageView.x = 0f
//            1 -> imageView.x = view.context.displayMetrics.widthPixels.toFloat() / 2 - imageView.width * 2
//            2 -> imageView.x = view.context.displayMetrics.widthPixels.toFloat() - imageView.width * 2
//        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_onboarding
    }

    override fun onClick(v: View?) {
       activity?.let {
           it.saveToSharedPreference(R.string.onboarding_passed_key, true)
           LoginActivity.start(it)
           it.finish()
       }
    }
}