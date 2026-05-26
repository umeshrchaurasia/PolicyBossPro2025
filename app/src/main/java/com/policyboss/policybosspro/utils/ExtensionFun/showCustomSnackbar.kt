package com.policyboss.policybosspro.utils.ExtensionFun

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.policyboss.policybosspro.R

fun Context.showCustomSnackbar(
    view: View,
    message: String,
    isSuccess: Boolean = false
) {

    val snackbar = Snackbar.make(
        view,
        message,
        Snackbar.LENGTH_LONG
    )

    val backgroundColor = if (isSuccess) {
        ContextCompat.getColor(this, R.color.green)
    } else {
        ContextCompat.getColor(this, R.color.red_custom)
    }

    snackbar.setBackgroundTint(backgroundColor)

    snackbar.setTextColor(
        ContextCompat.getColor(this, R.color.white)
    )

    snackbar.show()
}