package com.policyboss.policybosspro.view.vehicleScanner.old

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class BorderOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val borderPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    private val borderRect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Set the rectangle to the full size of this view
        borderRect.set(0f, 0f, width.toFloat(), height.toFloat())
        // Draw the border with rounded corners to match the CardView
        canvas.drawRoundRect(borderRect, 16f, 16f, borderPaint)
    }
}