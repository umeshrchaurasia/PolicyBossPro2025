package com.policyboss.policybosspro.utils.FirebasePushNotification

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.core.model.notification.NotifyEntity
import com.policyboss.policybosspro.core.repository.notificationRepository.INotificationRepository
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.view.home.HomeActivity
import com.webengage.sdk.android.WebEngage
import dagger.hilt.android.AndroidEntryPoint

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        const val CHANNEL_ID = "com.policyboss.policybosspro.NotifyID"
        const val CHANNEL_NAME = "POLICYBOSSPRO CHANNEL"
    }

    private var bitmapImage: Bitmap? = null
    private lateinit var type: String
    private lateinit var webURL: String
    private lateinit var webTitle: String
    private lateinit var messageId: String
    private lateinit var notifyEntity: NotifyEntity

    @Inject
    lateinit var prefManager: PolicyBossPrefsManager
    @Inject
    lateinit var notificationRepository: INotificationRepository


    private var notificationManager: NotificationManager? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            if (data["source"] == "webengage") {
                Log.d("webengage_data", data.toString())
                WebEngage.get().`receive`(data)
            } else {
                sendNotification(remoteMessage, data)
            }
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage, data: Map<String, String>) {
        notifyEntity = NotifyEntity()

        val notificationId = Random.nextInt(1000)

        if (data.isEmpty()) {
            Log.d(TAG, "Message Data Body Empty")
            return
        }

        Log.d(TAG, data["notifyFlag"].toString())

        type = data["notifyFlag"] ?: return

        webURL = data["web_url"] ?: ""
        webTitle = data["web_title"] ?: ""
        messageId = data["message_id"] ?: "0"



        notifyEntity.apply {
            notifyFlag = type
            title = data["title"]
            body = data["body"]
            message_id = messageId
            web_url = webURL
            web_title = webTitle
        }

        val notifyEntity = NotifyEntity(
            title = "Title here",
            body = "Body content here",
            notifyFlag = "L",
            web_url = "https://example.com",
            web_title = "Web Title",
            message_id = "123"
        )

        //Mark : Emit the notification data via NotificationRepository

        notificationRepository.sendNotification(

            notifyEntity = notifyEntity
        )

        val imgUrl = data["img_url"]
        bitmapImage = imgUrl?.let { getBitmapFromUrl(it) }

        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra(Constant.PUSH_NOTIFY, notifyEntity)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            Random.nextInt(1000),
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val pendingIntent1 = PendingIntent.getActivity(
            this,
            Random.nextInt(1000),
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        createNotificationChannel()

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setStyle(if (bitmapImage != null) {
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmapImage)
                    .setBigContentTitle(data["title"])
                    .setSummaryText(data["body"])
            } else {
                NotificationCompat.BigTextStyle()
                    .bigText(data["body"])
                    .setBigContentTitle(data["title"])
            })
            setSmallIcon(R.drawable.ic_pb_notify)
            color = ContextCompat.getColor(applicationContext, R.color.colorPrimary)
            setLargeIcon(bitmapImage ?: BitmapFactory.decodeResource(resources, R.mipmap.ic_policyboss))
            setContentTitle(data["title"])
            setContentText(data["body"])
            setAutoCancel(true)
            setSound(defaultSoundUri)
            setTicker("PolicyBoss Pro")
            setDefaults(NotificationCompat.DEFAULT_ALL)
            priority = NotificationCompat.PRIORITY_HIGH
            setWhen(System.currentTimeMillis())
            setChannelId(CHANNEL_ID)
            setContentIntent(pendingIntent)
        }

        getNotificationManager().notify(notificationId, notificationBuilder.build())
        updateNotificationCounter()
    }

    private fun updateNotificationCounter() {
        val notifyCounter = prefManager.getNotificationCounter()
        prefManager.setNotificationCounter( notifyCounter + 1)


    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                enableVibration(true)
                lightColor = Color.BLUE
                description = "PolicyBoss Pro"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            getNotificationManager().createNotificationChannel(channel)
        }
    }

    private fun getNotificationManager(): NotificationManager {
        if (notificationManager == null) {
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return notificationManager as NotificationManager
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        WebEngage.get().setRegistrationID(token)
    }

    private fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        return try {
            if (imageUrl.isBlank()) return null
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}