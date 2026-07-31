package com.policyboss.policybosspro.analytics


import android.content.Context
import android.util.Log
import com.policyboss.policybosspro.BuildConfig
import com.policyboss.policybosspro.view.salesMaterial.SalesMaterialActivity
import io.branch.referral.Branch
import io.branch.referral.util.BRANCH_STANDARD_EVENT
import io.branch.referral.util.BranchEvent

/**
 * Centralized Utility for handling Branch.io Analytics.
 * Ensures consistent event formatting and global parameters across the app.
 */

object BranchCustomEvents {
    // Onboarding
    const val TUTORIAL_BEGIN = "Tutorial_Begin"

    // Core Actions
    const val RAISE_TICKET_CLICKED = "Raise_Ticket_Clicked"
    const val CONTACT_SYNC_VIEWED = "Contact_Sync_Viewed"

    const val PRODUCT_SHARE= "Product_Share"

    const val SYNC_CONTACTS_VIEWED = "Sync_Contacts_Viewed"
    // Web Views
    const val PAGE_VIEW_WEBVIEW = "Common_WebView"

    // Resources
    const val SALESMATERIAL_VIEWED = "SalesMaterial_Viewed"


    const val SCREEN_VIEW = "Screen_View"
    const val NOTIFICATION_RECEIVE = "Notification_Receive"
    const val NOTIFICATION_CLICK = "Notification_Click"
    const val DEEPLINK_CLICK = "Deeplink_Click"
    const val USER_LOGOUT = "User_Logout"

    const val FIRST_OPEN = "first_open"

}


object AnalyticsBranchIOHelper {

    private const val TAG = "BranchAnalytics"

    // Define standard keys to prevent typos across the app
    private const val KEY_APP_VERSION = "app_version"
    private const val KEY_SCREEN_NAME = "screen_name"
    private const val KEY_SSID = "ssid"

    /**
     * Set the User Identity on Login using SSID.
     * This links all subsequent events to this specific user in the Branch dashboard.
     */
    fun setIdentity(ssid: String?) {
        if (!ssid.isNullOrBlank() && ssid != "0") {
            Branch.getInstance().setIdentity(ssid)
            Log.d(TAG, "Branch Identity set for SSID: $ssid")
        } else {
            Log.w(TAG, "Attempted to set Branch Identity with invalid SSID: $ssid")
        }
    }

    /**
     * Clear Identity on Logout.
     * Prevents the next user on this device from inheriting the previous user's events.
     */
    fun clearIdentity() {
        Branch.getInstance().logout()
        Log.d(TAG, "Branch Identity cleared on logout")
    }

    /**
     * 1. TRACK STANDARD EVENTS (e.g., LOGIN, SHARE, COMPLETE_TUTORIAL)
     */
    fun trackStandardEvent(
        context: Context,
        eventType: BRANCH_STANDARD_EVENT,
        screenName: String? = null,
        alias: String? = null,
        description: String? = null,
        customData: Map<String, String>? = null
    ) {
        val branchEvent = BranchEvent(eventType)
        buildAndLogEvent(context, branchEvent, screenName, alias, description, customData)
    }

    /**
     * 2. TRACK CUSTOM EVENTS (e.g., "TUTORIAL_BEGIN", "Raise_Ticket_Clicked")
     */
    @JvmStatic
    fun trackCustomEvent(
        context: Context,
        eventName: String,
        screenName: String? = null,
        alias: String? = null,
        description: String? = null,
        customData: Map<String, String>? = null
    ) {
        val branchEvent = BranchEvent(eventName)
        buildAndLogEvent(context, branchEvent, screenName, alias, description, customData)
    }

    /**
     * PRIVATE CORE BUILDER
     * Automatically injects global parameters (like App Version) into every event.
     */
    private fun buildAndLogEvent(
        context: Context,
        branchEvent: BranchEvent,
        screenName: String?,
        alias: String?,
        description: String?,
        customData: Map<String, String>?
    ) {
        try {
            // 1. Add Global Properties (Attached to EVERY event)
            branchEvent.addCustomDataProperty(KEY_APP_VERSION, BuildConfig.VERSION_NAME)

            // 2. Add Common Optional Properties
            screenName?.let { branchEvent.addCustomDataProperty(KEY_SCREEN_NAME, it) }
            alias?.let { branchEvent.setCustomerEventAlias(it) }
            description?.let { branchEvent.setDescription(it) }

            // 3. Add Specific Custom Data
            customData?.forEach { (key, value) ->
                branchEvent.addCustomDataProperty(key, value)
            }

            // 4. Log the event
            branchEvent.logEvent(context)

            Log.d(TAG, "Event Logged: ${branchEvent.eventName} | Screen: $screenName | Data: $customData")

        } catch (e: Exception) {
            Log.e(TAG, "Error logging Branch event: ${branchEvent.eventName}", e)
        }
    }
}