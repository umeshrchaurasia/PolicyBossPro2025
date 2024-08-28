package com.policyboss.policybosspro;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import com.policyboss.policybosspro.analytics.AnalyticsTrackers;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.WebEngageActivityLifeCycleCallbacks;
import com.webengage.sdk.android.WebEngageConfig;
import com.webengage.sdk.android.actions.database.ReportingStrategy;
import com.webengage.sdk.android.actions.render.InAppNotificationData;
import com.webengage.sdk.android.actions.render.PushNotificationData;
import com.webengage.sdk.android.callbacks.InAppNotificationCallbacks;
import com.webengage.sdk.android.callbacks.LifeCycleCallbacks;
import com.webengage.sdk.android.callbacks.PushNotificationCallbacks;
import com.xiaomi.channel.commonutils.android.Region;
import com.xiaomi.mipush.sdk.MiPushClient;



public class MyApplication extends Application {

    public static final String TAG = MyApplication.class
            .getSimpleName();
    public static MyApplication mInstance;



    private FirebaseAnalytics mFirebaseAnalytics;

    //PrefManager prefManager;
    @Override
    public void onCreate() {

        FirebaseApp.initializeApp(this);
        super.onCreate();
     //   Fabric.with(this, new Crashlytics());       // temp 05 commented



        //region google analytics Initialization

        mInstance = this;
        // 05 below two line  commented
       // AnalyticsTrackers.initialize(this);
       // AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

       // prefManager = PrefManager.getInstance(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //endregion --  in~~991991c1
        String webengage_key = "in~aa13173a";
        WebEngageConfig webEngageConfig = new WebEngageConfig.Builder()
                .setWebEngageKey(webengage_key)
                .setDebugMode(true) // only in development mode
                .setEventReportingStrategy(ReportingStrategy.FORCE_SYNC)
                .build();
        registerActivityLifecycleCallbacks(new WebEngageActivityLifeCycleCallbacks(this, webEngageConfig));



        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                try {
                    String token = task.getResult();
                    WebEngage.get().setRegistrationID(token);
                   // prefManager.setToken(token);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        WebEngage.registerPushNotificationCallback(new PushNotificationCallbacksImpl());

        WebEngage.registerInAppNotificationCallback(new InAppNotificationCallbackImpl());

        WebEngage.registerLifeCycleCallback(new LifeCycleCallbacksImpl());

        MiPushClient.setRegion(Region.India); //Set default region to Global or India

//Register for MI Push
      //  MiPushClient.registerPush(this, Constants.MI_APP_ID, Constants.MI_APP_KEY);
        MiPushClient.registerPush(this,"2882303761521918691","5682191839691");
    }


    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(this, null)
                                    .getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }






    private class PushNotificationCallbacksImpl implements PushNotificationCallbacks {

        @Override
        public PushNotificationData onPushNotificationReceived(Context context, PushNotificationData pushNotificationData) {



            return pushNotificationData;
        }

        @Override
        public void onPushNotificationShown(Context context, PushNotificationData pushNotificationData) {

        }

        @Override
        public boolean onPushNotificationClicked(Context context, PushNotificationData pushNotificationData) {
            return false;
        }

        @Override
        public void onPushNotificationDismissed(Context context, PushNotificationData pushNotificationData) {

        }

        @Override
        public boolean onPushNotificationActionClicked(Context context, PushNotificationData pushNotificationData, String s) {
            return false;
        }
    }

    private class InAppNotificationCallbackImpl implements InAppNotificationCallbacks {
        @Override
        public InAppNotificationData onInAppNotificationPrepared(Context context, InAppNotificationData inAppNotificationData) {
            return null;
        }

        @Override
        public void onInAppNotificationShown(Context context, InAppNotificationData inAppNotificationData) {

        }

        @Override
        public boolean onInAppNotificationClicked(Context context, InAppNotificationData inAppNotificationData, String s) {
            return false;
        }

        @Override
        public void onInAppNotificationDismissed(Context context, InAppNotificationData inAppNotificationData) {

        }
    }

    private class LifeCycleCallbacksImpl implements LifeCycleCallbacks {
        @Override
        public void onGCMRegistered(Context context, String s) {

        }

        @Override
        public void onGCMMessageReceived(Context context, Intent intent) {

        }

        @Override
        public void onAppInstalled(Context context, Intent intent) {

        }

        @Override
        public void onAppUpgraded(Context context, int i, int i1) {

        }

        @Override
        public void onNewSessionStarted() {

        }
    }
}