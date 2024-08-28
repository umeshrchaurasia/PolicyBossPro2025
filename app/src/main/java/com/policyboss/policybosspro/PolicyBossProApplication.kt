package com.policyboss.policybosspro

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Region
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.policyboss.policybosspro.analytics.AnalyticsTrackers
import com.webengage.sdk.android.WebEngage
import com.webengage.sdk.android.WebEngageActivityLifeCycleCallbacks
import com.webengage.sdk.android.WebEngageConfig
import com.webengage.sdk.android.actions.database.ReportingStrategy
import com.webengage.sdk.android.actions.render.InAppNotificationData
import com.webengage.sdk.android.actions.render.PushNotificationData
import com.webengage.sdk.android.callbacks.InAppNotificationCallbacks
import com.webengage.sdk.android.callbacks.LifeCycleCallbacks
import com.webengage.sdk.android.callbacks.PushNotificationCallbacks
import com.xiaomi.mipush.sdk.MiPushClient

import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PolicyBossProApplication : Application() {

    companion object {
        const val TAG: String = "PolicyBossPro"
        @JvmStatic
        var instance: PolicyBossProApplication? = null
            private set
    }

    private var firebaseAnalytics: FirebaseAnalytics? = null


    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)


        instance = this

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val webengageKey = "in~aa13173a"
        val webEngageConfig = WebEngageConfig.Builder()
            .setWebEngageKey(webengageKey)
            .setDebugMode(true) // only in development mode
            .setEventReportingStrategy(ReportingStrategy.FORCE_SYNC)
            .build()
        registerActivityLifecycleCallbacks(WebEngageActivityLifeCycleCallbacks(this, webEngageConfig))

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            try {
                val token = task.result
                WebEngage.get().setRegistrationID(token)
                // prefManager.setToken(token)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        WebEngage.registerPushNotificationCallback(PushNotificationCallbacksImpl())
        WebEngage.registerInAppNotificationCallback(InAppNotificationCallbackImpl())
        WebEngage.registerLifeCycleCallback(LifeCycleCallbacksImpl())

        MiPushClient.setRegion(com.xiaomi.channel.commonutils.android.Region.India) //Set default region to Global or India

        // Register for MI Push
        MiPushClient.registerPush(this, "2882303761521918691", "5682191839691")


    }

    @Synchronized
    fun getGoogleAnalyticsTracker(): Tracker {
        val analyticsTrackers = AnalyticsTrackers.getInstance()
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP)
    }

    fun trackScreenView(screenName: String) {
        val tracker = getGoogleAnalyticsTracker()
        tracker.setScreenName(screenName)
        tracker.send(HitBuilders.ScreenViewBuilder().build())
        GoogleAnalytics.getInstance(this).dispatchLocalHits()
    }

    private inner class PushNotificationCallbacksImpl : PushNotificationCallbacks {
        override fun onPushNotificationReceived(
            context: Context,
            pushNotificationData: PushNotificationData
        ): PushNotificationData {
            return pushNotificationData
        }

        override fun onPushNotificationShown(context: Context, pushNotificationData: PushNotificationData) {}

        override fun onPushNotificationClicked(context: Context, pushNotificationData: PushNotificationData): Boolean {
            return false
        }

        override fun onPushNotificationDismissed(context: Context, pushNotificationData: PushNotificationData) {}

        override fun onPushNotificationActionClicked(
            context: Context,
            pushNotificationData: PushNotificationData,
            actionId: String
        ): Boolean {
            return false
        }
    }

    private inner class InAppNotificationCallbackImpl : InAppNotificationCallbacks {
        override fun onInAppNotificationPrepared(
            context: Context,
            inAppNotificationData: InAppNotificationData
        ): InAppNotificationData? {
            return null
        }

        override fun onInAppNotificationShown(context: Context, inAppNotificationData: InAppNotificationData) {}

        override fun onInAppNotificationClicked(
            context: Context,
            inAppNotificationData: InAppNotificationData,
            actionId: String
        ): Boolean {
            return false
        }

        override fun onInAppNotificationDismissed(context: Context, inAppNotificationData: InAppNotificationData) {}
    }

    private inner class LifeCycleCallbacksImpl : LifeCycleCallbacks {
        override fun onGCMRegistered(context: Context, registrationId: String) {}

        override fun onGCMMessageReceived(context: Context, intent: Intent) {}

        override fun onAppInstalled(context: Context, intent: Intent) {}

        override fun onAppUpgraded(context: Context, oldVersion: Int, newVersion: Int) {}

        override fun onNewSessionStarted() {}
    }


}