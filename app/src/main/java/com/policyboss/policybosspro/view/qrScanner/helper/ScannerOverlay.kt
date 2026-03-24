package com.policyboss.policybosspro.view.qrScanner.helper

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


class ScannerOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val dimPaint = Paint().apply {
        color = Color.parseColor("#99000000") // dark overlay
    }

    private val scanRect = RectF()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        val widthRect = w * 0.75f
        val heightRect = h * 0.40f

        val left = (w - widthRect) / 2
        val top = (h - heightRect) / 2

        scanRect.set(left, top, left + widthRect, top + heightRect)
    }

    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        canvas.save()

        // Cut transparent rectangle
        canvas.clipRect(scanRect, Region.Op.DIFFERENCE)

        // Draw dim background
        canvas.drawRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            dimPaint
        )

        canvas.restore()
    }

    fun getScanRect(): RectF {
        return scanRect
    }
}


