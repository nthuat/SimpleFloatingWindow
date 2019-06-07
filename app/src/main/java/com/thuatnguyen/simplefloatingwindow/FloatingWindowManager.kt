package com.thuatnguyen.simplefloatingwindow

import android.app.Activity
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.os.Build
import android.support.v7.widget.AppCompatTextView
import android.view.*
import android.widget.Button
import kotlin.math.abs

class FloatingWindowManager {
    private var dismissButton: Button?
    private var mActivity: Activity? = null
    private var floatingWindowView: View
    private var isShowing = false
    private var windowManager: WindowManager? = null
    private var floatWindowLayoutParams: WindowManager.LayoutParams? = null
    private var floatLastX: Int = 0
    private var floatLastY: Int = 0
    private var floatFirstX: Int = 0
    private var floatFirstY: Int = 0

    constructor(activity: Activity) {
        mActivity = activity
        val inflater = LayoutInflater.from(activity)
        floatingWindowView = inflater.inflate(R.layout.floating_window_layout, null)
        var textView = floatingWindowView.findViewById<AppCompatTextView>(R.id.textView)
        textView.setText("Hello guys! Let's bring me to everywhere you can!")
        dismissButton = floatingWindowView.findViewById(R.id.dismissButton)
        dismissButton?.setOnClickListener { hideFloatingWindow() }

        floatWindowLayoutParams = WindowManager.LayoutParams()
        floatWindowLayoutParams?.format = PixelFormat.TRANSLUCENT
        floatWindowLayoutParams?.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        floatWindowLayoutParams?.type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_TOAST
        floatWindowLayoutParams?.gravity = Gravity.CENTER
        floatWindowLayoutParams?.width = WindowManager.LayoutParams.WRAP_CONTENT
        floatWindowLayoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT

        floatingWindowView.setOnTouchListener { v: View?, event: MotionEvent? ->
            val prm = floatWindowLayoutParams
            val totalDeltaX = floatLastX - floatFirstX
            val totalDeltaY = floatLastY - floatFirstY
            when (event!!.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    floatLastX = event.rawX.toInt()
                    floatLastY = event.rawY.toInt()
                    floatFirstX = floatLastX
                    floatFirstY = floatLastY
                }
                MotionEvent.ACTION_MOVE -> {
                    var deltaX = event.rawX.toInt() - floatLastX
                    var deltaY = event.rawY.toInt() - floatLastY
                    floatLastX = event.rawX.toInt()
                    floatLastY = event.rawY.toInt()
                    if (abs(totalDeltaX) >= 10 || abs(totalDeltaY) >= 10) {
                        if (event.pointerCount == 1) {
                            prm!!.x += deltaX
                            prm!!.y += deltaY
                            windowManager?.updateViewLayout(floatingWindowView, prm)
                            true
                        }
                    }
                }

            }
            false
        }
    }

    fun showFloatingWindow() {
        if (!isShowing) {
            isShowing = true
            windowManager = mActivity?.getSystemService(WINDOW_SERVICE) as WindowManager?
            windowManager?.addView(floatingWindowView, floatWindowLayoutParams)
            mActivity?.finish()
        }
    }

    fun hideFloatingWindow() {
        if (isShowing) {
            isShowing = false;
            windowManager?.removeViewImmediate(floatingWindowView)
        }
    }
}