package com.policyboss.policybosspro.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.policyboss.policybosspro.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsHelper @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {

    init {
        // This attaches "client_version" to EVERY event automatically
        val defaultParams = Bundle().apply {
            putString("client_version", BuildConfig.VERSION_NAME)
        }
        firebaseAnalytics.setDefaultEventParameters(defaultParams)
    }
    /**
     * Log a custom or standard Firebase Event
     */
    fun trackEvent(eventName: String, params: Bundle? = null) {
        firebaseAnalytics.logEvent(eventName, params)
    }

    /**
     * Track Screen Views across the app
     */
//    fun trackScreenView(screenName: String, className: String) {
//        val bundle = Bundle().apply {
//            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
//            putString(FirebaseAnalytics.Param.SCREEN_CLASS, className)
//        }
//        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
//    }
    fun trackScreenView(screenName: String, className: String, extraParams: Bundle? = null) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, className)

            // If extra parameters were passed in, add them to the screen view bundle
            extraParams?.let { putAll(it) }
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