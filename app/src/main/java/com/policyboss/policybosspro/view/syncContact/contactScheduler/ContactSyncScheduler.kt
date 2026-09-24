package com.policyboss.policybosspro.view.syncContact.contactScheduler

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.Calendar
import java.util.concurrent.TimeUnit


import androidx.work.PeriodicWorkRequest
import com.policyboss.policybosspro.BuildConfig


import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder

object ContactSyncScheduler {

    private const val TAG = "ContactSyncScheduler"

    // ============================================================
    // UNIQUE WORK NAME
    // ============================================================

    const val UNIQUE_WORK_NAME = "weekly_contact_sync_periodic"

    // Old name used by previous implementation.
    // Keep this temporarily so old work is removed.
    private const val OLD_UNIQUE_WORK_NAME =
        "weekly_contact_sync_work"


    // ============================================================
    // 🧪 DEBUG / TEST
    // ============================================================

    /**
     * WorkManager minimum periodic interval is 15 minutes.
     *
     * We intentionally use 20 minutes for testing.
     */
    private const val TEST_INTERVAL_MINUTES = 20L


    // ============================================================
    // 🚀 PRODUCTION
    // ============================================================

    /**
     * Contact sync runs once every 7 days.
     */
    private const val PROD_INTERVAL_DAYS = 7L

    /**
     * Monday 12:00 PM.
     */
    private const val TARGET_DAY = Calendar.MONDAY
    private const val TARGET_HOUR = 12
    private const val TARGET_MINUTE = 0


    // ============================================================
    // PUBLIC API
    // ============================================================

    fun ensureScheduled(context: Context) {

        val appContext = context.applicationContext
        val workManager = WorkManager.getInstance(appContext)

        workManager.cancelUniqueWork(OLD_UNIQUE_WORK_NAME)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request: PeriodicWorkRequest

        if (BuildConfig.DEBUG) {
            request = PeriodicWorkRequestBuilder<ContactSyncWorker>(
                TEST_INTERVAL_MINUTES, TimeUnit.MINUTES
            )
                .setInitialDelay(1, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
                .addTag(UNIQUE_WORK_NAME)
                .build()

            // ✅ FIX: cancel first, then enqueue fresh.
            // UPDATE preserves the ORIGINAL next-run time from whenever this
            // unique name was first scheduled — it will NOT pick up a new
            // interval/initialDelay on its own. For dev iteration we want a
            // hard reset every launch, so cancel explicitly instead of UPDATE.
            workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
            workManager.enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE, // or KEEP if you don't want to reset every launch
                request
            )

            Log.d(TAG, "DEBUG MODE — cancelled + re-enqueued fresh (1 min delay, 20 min repeat)")

        } else {
            val initialDelay = computeDelayToNextMondayNoonMillis()

            request = PeriodicWorkRequestBuilder<ContactSyncWorker>(
                PROD_INTERVAL_DAYS, TimeUnit.DAYS
            )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
                .addTag(UNIQUE_WORK_NAME)
                .build()

            // ✅ Production: UPDATE is correct here — you genuinely don't want
            // every app launch to reset a user's already-scheduled weekly sync.
            workManager.enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )

            Log.d(TAG, "PRODUCTION MODE — interval 7 days, delay ${formatDuration(initialDelay)}")
        }

        Log.d(TAG, "Periodic contact sync enqueued successfully")
    }


    // ============================================================
    // CANCEL
    // ============================================================

    fun cancel(context: Context) {

        WorkManager
            .getInstance(context.applicationContext)
            .cancelUniqueWork(
                UNIQUE_WORK_NAME
            )

        Log.d(
            TAG,
            "Periodic contact sync cancelled"
        )
    }


    // ============================================================
    // MONDAY 12 PM CALCULATION
    // ============================================================

    private fun computeDelayToNextMondayNoonMillis(): Long {

        val now = Calendar.getInstance()

        val target =
            (now.clone() as Calendar).apply {

                set(
                    Calendar.DAY_OF_WEEK,
                    TARGET_DAY
                )

                set(
                    Calendar.HOUR_OF_DAY,
                    TARGET_HOUR
                )

                set(
                    Calendar.MINUTE,
                    TARGET_MINUTE
                )

                set(
                    Calendar.SECOND,
                    0
                )

                set(
                    Calendar.MILLISECOND,
                    0
                )
            }


        /**
         * If Monday 12:00 PM has already passed,
         * target becomes next Monday 12:00 PM.
         */
        if (!target.after(now)) {

            target.add(
                Calendar.DAY_OF_YEAR,
                7
            )
        }


        val delay =
            target.timeInMillis -
                    now.timeInMillis


        Log.d(
            TAG,
            "Current time = ${now.time}"
        )

        Log.d(
            TAG,
            "Next sync target = ${target.time}"
        )

        Log.d(
            TAG,
            "Delay = ${formatDuration(delay)}"
        )


        return delay
    }


    // ============================================================
    // DEBUG LOG HELPER
    // ============================================================

    private fun formatDuration(
        millis: Long
    ): String {

        val totalMinutes =
            TimeUnit.MILLISECONDS
                .toMinutes(millis)

        val days =
            totalMinutes / (24 * 60)

        val hours =
            (totalMinutes % (24 * 60)) / 60

        val minutes =
            totalMinutes % 60

        return "${days}d ${hours}h ${minutes}m"
    }
}