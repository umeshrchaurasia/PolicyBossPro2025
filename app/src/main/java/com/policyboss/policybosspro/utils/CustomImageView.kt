package com.policyboss.policybosspro.utils

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.policyboss.policybosspro.R // Adjust this import based on your package structure

/**
 * CustomImageView is a custom view that extends AppCompatImageView.
 * This view maintains a specific aspect ratio (1.5).
 */
class CustomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {

    private var aspectRatio: Float = 1.5f // Default aspect ratio

    init {
        attrs?.let {
            val typedArray: TypedArray = context.obtainStyledAttributes(it, R.styleable.CustomImageView)
            aspectRatio = typedArray.getFloat(R.styleable.CustomImageView_aspectRatio, 1.5f)
            typedArray.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Custom drawing logic can be added here if necessary
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = measuredWidth
        val height = (width * aspectRatio).toInt() // Maintain aspect ratio
        setMeasuredDimension(width, height)
    }
}
