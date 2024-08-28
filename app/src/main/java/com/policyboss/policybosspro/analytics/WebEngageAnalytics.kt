package com.policyboss.policybosspro.analytics

import com.webengage.sdk.android.Analytics
import com.webengage.sdk.android.WebEngage
import com.webengage.sdk.android.utils.WebEngageConstant

class WebEngageAnalytics private constructor() {

    private val weConstant: WebEngageConstant? = null

    companion object {
        @Volatile
        private var instance: WebEngageAnalytics? = null

        private val weAnalytics: Analytics = WebEngage.get().analytics()

        // Singleton instance of WebEngageAnalytics
        @Synchronized
        fun getInstance(): WebEngageAnalytics {
            return instance ?: synchronized(this) {
                instance ?: WebEngageAnalytics().also { instance = it }
            }
        }
    }

    // Method to track events
    fun trackEvent(eventName: String, eventAttributes: Map<String, Any>) {
        weAnalytics.track(eventName, eventAttributes)
    }
}
