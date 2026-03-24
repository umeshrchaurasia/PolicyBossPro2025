package com.policyboss.policybosspro.utils.ExtensionFun

import android.view.View


fun Int.dp(view: View): Int {
    return (this * view.resources.displayMetrics.density).toInt()
}