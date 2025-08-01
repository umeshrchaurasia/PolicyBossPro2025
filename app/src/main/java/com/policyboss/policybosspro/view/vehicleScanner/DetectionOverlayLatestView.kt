package com.policyboss.policybosspro.view.vehicleScanner

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View

class DetectionOverlayLatestView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // --- CONFIGURABLE PROPERTIES ---
    private var guideRectangleHeightRatio: Float = 0.4f // Taller rectangle for better coverage
    private val guideRectangleWidthRatio: Float = 0.9f  // 90% of the screen width

    // Padding to create a larger analysis area than the visual guide
    private val analysisPaddingHorizontal: Float = 0.15f // 15% padding on each side
    private val analysisPaddingVertical: Float = 0.12f     // 12% padding on top/bottom




    // --- PAINT OBJECTS ---
    private val scrimPaint = Paint().apply {
       color = Color.parseColor("#99000000") // Semi-transparent black scrim


    }
    private val eraserPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
    }
    private val borderPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    private val guideRect = RectF()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Calculate the visual guide rectangle's dimensions when the view size is known
        updateGuideRect(w, h)
    }

    private fun updateGuideRect(viewWidth: Int, viewHeight: Int) {
        val rectWidth = viewWidth * guideRectangleWidthRatio
        // Make height relative to the calculated width for a consistent aspect ratio
        val rectHeight = rectWidth * guideRectangleHeightRatio

        val left = (viewWidth - rectWidth) / 2
        val top = (viewHeight - rectHeight) / 2
        val right = left + rectWidth
        val bottom = top + rectHeight

        guideRect.set(left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw the semi-transparent scrim over the entire view
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), scrimPaint)
        // "Cut out" the guide rectangle from the scrim for a professional look
        canvas.drawRoundRect(guideRect, 16f, 16f, eraserPaint)
        // Draw the white border around the guide rectangle
        canvas.drawRoundRect(guideRect, 16f, 16f, borderPaint)
    }

    /**
     * This is the key function. It returns a larger rectangle for analysis
     * than what the user sees, helping to capture edge characters like 'M'.
     */
    fun getDetectionRectInImageCoordinates(imageWidth: Int, imageHeight: Int): Rect {
        // Calculate the padding in pixels based on the view's dimensions
        val horizontalPadding = width * analysisPaddingHorizontal
        val verticalPadding = height * analysisPaddingVertical

        // Create the expanded rectangle for analysis by adding padding to the visual guide
        val analysisRect = RectF(
            guideRect.left - horizontalPadding,
            guideRect.top - verticalPadding,
            guideRect.right + horizontalPadding,
            guideRect.bottom + verticalPadding
        )

        // Ensure the rectangle does not go outside the view's bounds
        analysisRect.intersect(0f, 0f, width.toFloat(), height.toFloat())

        // Calculate scaling factors to map view coordinates to image coordinates
        val scaleX = imageWidth.toFloat() / width
        val scaleY = imageHeight.toFloat() / height

        // Convert the expanded view coordinates to image coordinates
        return Rect(
            (analysisRect.left * scaleX).toInt(),
            (analysisRect.top * scaleY).toInt(),
            (analysisRect.right * scaleX).toInt(),
            (analysisRect.bottom * scaleY).toInt()
        )
    }



}