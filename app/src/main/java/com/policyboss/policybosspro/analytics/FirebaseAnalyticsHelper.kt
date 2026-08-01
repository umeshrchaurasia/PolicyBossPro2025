package com.policyboss.policybosspro.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsHelper @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {

    /**
     * Log a custom or standard Firebase Event
     */
    fun trackEvent(eventName: String, params: Bundle? = null) {
        firebaseAnalytics.logEvent(eventName, params)
    }

    /**
     * Track Screen Views across the app
     */
    fun trackScreenView(screenName: String, className: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, className)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    /**
     * Set User ID upon successful Login
     */
    fun setUserId(userId: String) {
        if (userId.isNotBlank() && userId != "0") {
            firebaseAnalytics.setUserId(userId)
        }
    }

    /**
     * Set User Properties (e.g., User Type, FBA ID)
     */
    fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
    }

    /**
     * Clear User ID upon Logout
     */
    fun clearUserId() {
        firebaseAnalytics.setUserId(null)
    }
}