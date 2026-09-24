package com.policyboss.policybosspro.view.syncContact.contactScheduler

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.provider.ContactsContract
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.gson.Gson
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.core.RetroHelper
import com.policyboss.policybosspro.core.requestbuilder.ContactLeadRequestEntity
import com.policyboss.policybosspro.core.requestbuilder.ContactlistEntity
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utility.Utility
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.view.syncContact.contactScheduler.ContactSyncScheduler
import com.policyboss.policybosspro.view.syncContact.helper.ContactHelper
import com.policyboss.policybosspro.view.syncContact.ui.SyncContactActivity
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.work.WorkManager

import androidx.hilt.work.HiltWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

import kotlinx.coroutines.ensureActive
import kotlin.coroutines.cancellation.CancellationException



@HiltWorker
class ContactSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ContactSyncWorkerEntryPoint {
        fun prefManager(): PolicyBossPrefsManager
    }

    private val prefManager: PolicyBossPrefsManager by lazy {
        val entryPoint = EntryPointAccessors.fromApplication(applicationContext, ContactSyncWorkerEntryPoint::class.java)
        entryPoint.prefManager()
    }

    private val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        return try {
            if (!hasContactsPermission()) {
                showPermissionNeededNotification()
                return Result.failure()
            }

            notificationManager.cancel(NOTIF_ID_PERMISSION)
            trySetIntermediateForeground("Preparing contact sync…")

            val totalSynced = syncContacts()

            showResultNotification(
                "Contacts synced",
                "$totalSynced contact(s) synced successfully."
            )

            Result.success(workDataOf(Constant.KEY_result to totalSynced.toString()))

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("ContactSyncWorker", "SYNC FAILED: ${e.message}", e)
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                showResultNotification(
                    "Contact sync failed",
                    "We couldn't sync your contacts this week. It will try again according to schedule."
                )
                Result.failure()
            }
        } finally {
            notificationManager.cancel(NOTIF_ID_PROGRESS)
        }
    }

    private suspend fun trySetIntermediateForeground(msg: String) {
        val info = buildIntermediateForegroundInfo(msg)
        try {
            setForeground(info)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w("ContactSyncWorker", "setForeground not allowed: ${e.message}")
            notificationManager.notify(NOTIF_ID_PROGRESS, info.notification)
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return buildIntermediateForegroundInfo("Waiting to sync contacts…")
    }

    private fun hasContactsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun buildIntermediateForegroundInfo(message: String): ForegroundInfo {
        ensureChannel(CHANNEL_ID_PROGRESS, "Contact sync progress", NotificationManager.IMPORTANCE_LOW)

        val cancelIntent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID_PROGRESS)
            .setContentTitle("Syncing your contacts")
            .setContentText(message)
            .setSmallIcon(R.drawable.pb_pro_logo)
            .setColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(0, 0, true) // Indeterminate progress loader
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Cancel", cancelIntent)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(NOTIF_ID_PROGRESS, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(NOTIF_ID_PROGRESS, notification)
        }
    }


    //note : we get crah bec calling after success trySetIntermediateForeground("Syncing contacts batch...")

    // ✅ No per-chunk foreground/notification update anymore.
    // The single call in doWork() before syncContacts() starts is enough
    // to keep the worker in a valid foreground state for the whole run.

    private suspend fun syncContacts(): Int = withContext(Dispatchers.IO) {
        val contactList = readDeviceContacts()
        if (contactList.isEmpty()) return@withContext 0

        val fbaidStr = prefManager.getFBAID()
        val fbaid = fbaidStr.toIntOrNull() ?: 0
        val parentId = fbaidStr
        val ssid = prefManager.getPOSPNo()
        val deviceId = Utility.getDeviceID(applicationContext)
        val appVersion = prefManager.getAppVersion()
        val tfbaid = if (parentId.isEmpty() || parentId == "0") fbaid.toString() else parentId
        val tsubFbaId = if (parentId.isEmpty() || parentId == "0") parentId else fbaid.toString()

        val rawDataJson: String? = if (contactList.size > 3000) null else generateRawDataSafely()
        val progressStep = 1000
        val totalSize = contactList.size

        for (i in 0 until totalSize step progressStep) {
            ensureActive()

            val end = minOf(i + progressStep, totalSize)
            val request = ContactLeadRequestEntity(
                fbaid = tfbaid, ssid = ssid, sub_fba_id = tsubFbaId,
                contactlist = contactList.subList(i, end),
                raw_data = if (i == 0) rawDataJson ?: "" else "",
                device_id = deviceId, app_version = appVersion
            )

            val response = RetroHelper.api.saveContactLead(CONTACT_SYNC_URL, request)

            if (response?.isSuccessful != true) {
                throw IllegalStateException("API failed with HTTP ${response?.code()}")
            }
            // ✅ No per-chunk foreground/notification update anymore.
            // The single call in doWork() before syncContacts() starts is enough
            // to keep the worker in a valid foreground state for the whole run.
        }
        totalSize
    }

    private fun readDeviceContacts(): MutableList<ContactlistEntity> {
        val contactList = mutableListOf<ContactlistEntity>()
        val seen = HashSet<String>()
        val regex = Regex("[^0-9]")
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)
        val selection = "${ContactsContract.Contacts.HAS_PHONE_NUMBER} > 0"

        applicationContext.contentResolver.query(uri, projection, selection, null, "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC")?.use { cursor ->
            var idCounter = 1
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            if (nameIndex != -1 && numIndex != -1) {
                while (cursor.moveToNext()) {
                    try {
                        val name = cursor.getString(nameIndex) ?: "Unknown"
                        val cleaned = regex.replace(cursor.getString(numIndex) ?: "", "")
                        if (cleaned.length >= 10) {
                            val mobile = cleaned.takeLast(10)
                            if (seen.add(mobile)) contactList.add(ContactlistEntity(name = name, mobileno = mobile, id = idCounter++))
                        }
                    } catch (e: Exception) { Log.e("ContactSyncWorker", "Corrupted row: ${e.message}") }
                }
            }
        }
        return contactList
    }

    private fun generateRawDataSafely(): String? = try {
        val raw = ContactHelper.getDummyRawContacts(applicationContext, multiplier = 10)
        if (raw.size > 3000) null else Gson().toJson(raw).takeIf { it.length <= 1_000_000 }
    } catch (e: Exception) { null }

    private fun showResultNotification(title: String, text: String) {
        ensureChannel(CHANNEL_ID_RESULT, "Sync Results", NotificationManager.IMPORTANCE_DEFAULT)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID_RESULT)
            .setSmallIcon(R.drawable.pb_pro_logo)
            .setContentTitle(title)
            .setColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(NOTIF_ID_RESULT, notification)
    }

    private fun showPermissionNeededNotification() {
        ensureChannel(CHANNEL_ID_REMINDER, "Sync Permission Alerts", NotificationManager.IMPORTANCE_HIGH)

        val openAppIntent = Intent(applicationContext, SyncContactActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_OPENED_FROM_SYNC_REMINDER, true)
        }
        val pendingIntent = PendingIntent.getActivity(applicationContext, REQUEST_CODE_PERMISSION, openAppIntent, pendingIntentFlags())

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID_REMINDER)
            .setSmallIcon(R.drawable.pb_pro_logo)
            .setContentTitle("Contact sync paused")
            .setContentText("Tap to allow contacts access so this week's sync can run.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIF_ID_PERMISSION, notification)
    }

    private fun ensureChannel(id: String, name: String, importance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(id) == null) {
                val channel = NotificationChannel(id, name, importance).apply {
                    enableLights(true)
                    enableVibration(importance >= NotificationManager.IMPORTANCE_DEFAULT)
                    if (importance >= NotificationManager.IMPORTANCE_DEFAULT) lightColor = Color.BLUE
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    private fun pendingIntentFlags(): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT

    companion object {
        private const val CONTACT_SYNC_URL = "https://horizon.policyboss.com:5443/sync_contacts/contact_entry"
        const val CHANNEL_ID_PROGRESS = "weekly_contact_sync_progress"
        const val CHANNEL_ID_REMINDER = "weekly_contact_sync_reminder"
        const val CHANNEL_ID_RESULT = "weekly_contact_sync_result"
        const val NOTIF_ID_PROGRESS = 5601
        const val NOTIF_ID_PERMISSION = 5602
        const val NOTIF_ID_RESULT = 5603
        const val REQUEST_CODE_PERMISSION = 9001
        const val EXTRA_OPENED_FROM_SYNC_REMINDER = "opened_from_sync_reminder"
    }
}