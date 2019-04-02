package com.kross.taxi_passenger.presentation.screen.base

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import com.kross.taxi_passenger.R
import com.kross.taxi_passenger.utils.hideKeyboardEx

abstract class BaseActivity : AppCompatActivity() {

    private var mToolbar: Toolbar? = null
    protected open val PERMISSION_REQUEST = 5
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    protected fun initializeToolbar(toolbar: Toolbar) {
        mToolbar = toolbar
        mToolbar?.apply {
            setNavigationOnClickListener { onBackPressed() }
            setActionBar(this)
            actionBar?.title = ""
        }
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down)
    }

    protected fun getToolbar(): Toolbar? = mToolbar

    protected fun setToolbarTitle(title: CharSequence) {
        mToolbar?.title = title
    }

    private fun findViewAt(viewGroup: ViewGroup, x: Int, y: Int): View? {
        (0 until viewGroup.childCount)
                .map { viewGroup.getChildAt(it) }
                .forEach {
                    when (it) {
                        is ViewGroup -> {
                            val foundView = findViewAt(it, x, y)
                            if (foundView?.isShown!!) return foundView
                        }
                        else -> {
                            val location = IntArray(2)
                            it.getLocationOnScreen(location)
                            val rect = Rect(location[0], location[1], location[0] + it.width, location[1] + it.height)
                            if (rect.contains(x, y)) return it
                        }
                    }
                }
        return null
    }

    fun hideKeyboard() = this.hideKeyboardEx()

    inline fun <reified T> getArgOrThrow(arg: String): T {
        return getArgOrThrow(intent, arg)
    }

    inline fun <reified T> getArgOrThrow(intent: Intent, arg: String): T {
        if(intent.extras == null || !intent.extras!!.containsKey(arg)) {
            throw IllegalStateException("Arg $arg is missing")
        }

        return intent.extras!!.get(arg) as T
    }

    fun Intent.finishWithResult() {
        setResult(Activity.RESULT_OK, this)
        finish()
    }
}