package com.codemao.sensors

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.codemao.sensors.SensorsHelper.context

@SuppressLint("ClickableViewAccessibility")
fun View.addClickScale(scale: Float = 0.95f, duration: Long = 150L): View {
    this.setOnTouchListener { _,  event ->
        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                this.animate().scaleX(scale).scaleY(scale).setDuration(duration)
                    .start()
            }
            MotionEvent.ACTION_UP , MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE-> {
                this.animate().scaleX(1f).scaleY(1f).setDuration(duration).start()
            }

            else->{

            }

        }
        false
    }
    return this
}
var viewClickFlag = false
var clickRunnable = Runnable { viewClickFlag = false }
fun View.click(delayMills: Long = 350, action: (view: View) -> Unit): View {
    setOnClickListener {
        if (!viewClickFlag) {
            viewClickFlag = true
            action(it)
        }
        removeCallbacks(clickRunnable)
        postDelayed(clickRunnable, delayMills)
    }
    return this
}

fun drawable(@DrawableRes id: Int) = ContextCompat.getDrawable(context, id)