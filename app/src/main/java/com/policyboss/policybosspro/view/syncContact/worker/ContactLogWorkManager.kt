package com.policyboss.policybosspro.view.syncContact.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.database.Cursor
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.gson.Gson
import com.policyboss.policybosspro.core.RetroHelper


import com.policyboss.policybosspro.core.requestbuilder.ContactLeadRequestEntity
import com.policyboss.policybosspro.core.requestbuilder.ContactlistEntity
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.view.syncContact.helper.ContactHelper
import com.policyboss.policybosspro.view.syncContact.ui.SyncContactActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter

/**
 * Created by Rahul on 10/06/2022.
 */

class ContactLogWorkManager(
    val context: Context,
    workerParameters: WorkerParameters,

    ) : CoroutineWorker(context, workerParameters) {


    private val TAG = "CALL_LOG_CONTACT"
    private val ProgressStep = 1000
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    override suspend fun doWork(): Result {
        return try {
            Log.d("CallLogWorker", "Run work manager")
            //Do Your task here

            var ContactCount = callContactTask()


            // Log.d("CallLogWorker", callLogList.toString())
            val outPutData: Data = Data.Builder()
                .putString(Constant.KEY_result, "${ContactCount}")
                .build()
            Result.success(outPutData)
        }
        catch (e: Exception) {
            Log.d(TAG, "exception in doWork1 ${e.message}")
            val errorData: Data = Data.Builder()
                .putString(Constant.KEY_error_result, "Data Not Uploaded.Please Try Again.")
                .build()
            Result.failure(errorData)

        }

    }

    private suspend fun callContactTask1() : Int {

        var ContactCount = 0



        //region Comment : Not In Used
        var strbody = Constant.CONTACT_LOG_DataFetching
        var strResultbody = "Successfully fetch the data.."
        setForeground(createForegroundInfo(0, 0, strbody))

        //endregion


        // region getting Input Data
        val fbaid = inputData.getInt(Constant.KEY_fbaid, 0)
        val ssid = inputData.getString(Constant.KEY_ssid)
        val parentid = inputData.getString(Constant.KEY_parentid)
        val deviceID = inputData.getString(Constant.KEY_deviceid) ?: ""
        val appversion = inputData.getString(Constant.KEY_appversion) ?: ""

        var tfbaid = ""
        var tsub_fba_id = ""
        var getAllContactDetails :  MutableList<ContactHelper.ModelContact> = mutableListOf()

        if (parentid.isNullOrEmpty() || parentid.equals("0")) {

            tfbaid = fbaid.toString()
            tsub_fba_id = parentid.toString()

        } else {
            tfbaid = parentid.toString()
            tsub_fba_id = fbaid.toString()
        }

        //endregion
        //  delay(2000)

        withContext(Dispatchers.IO) {

            var url =  "https://horizon.policyboss.com:5443/sync_contacts" + "/contact_entry"


            var contactlist = getContactList()

            // region  05 temp commented for Testing for Increase the Contact List
//            val contactlist: MutableList<ContactlistEntity> = mutableListOf()
//
//            for (i in 1..8) {
//
//             //Assuming getContactList() returns a ContactlistEntity
//                contactlist.addAll(getContactList())
//            }
            //endregion

            ContactCount = contactlist.size
            Log.d(TAG, "Total Contact Size ${contactlist.size}")


            //region ForegroundService and Background

            var remainderProgress = 0
            var maxProgress = 0
            var currentProgress = 0
            var defaultProgress = 1
            maxProgress = contactlist!!.size / ProgressStep

            remainderProgress = contactlist!!.size % ProgressStep
            maxProgress = maxProgress + defaultProgress
            currentProgress = defaultProgress
            if (remainderProgress > 0) {
                maxProgress = maxProgress + 1
            }

            // Log.d(TAG, "maxProgress ${maxProgress}")
            setForeground(createForegroundInfo(maxProgress, currentProgress, strbody))
            val workProgessDefault = workDataOf(
                Constant.CONTACT_LOG_Progress to currentProgress,
                Constant.CONTACT_LOG_MAXProgress to maxProgress,
                // Constant.CONTACT_LOG_Data_SIZE to contactlist.size
            )
            setProgress(workProgessDefault)


            //endregion

            var subcontactlist: List<ContactlistEntity>

            if (contactlist != null && contactlist!!.size > 0) {

                try{

                    getAllContactDetails =  ContactHelper.getContact(context.applicationContext)


//                    var data = Gson().toJson(getAllContactDetails)
//                    Log.d(Constant.TAG_SAVING_CONTACT_LOG,data )
//

                }catch (ex :Exception ){
                    Log.d(Constant.TAG_SAVING_CONTACT_LOG,ex.toString() )
                }


                // 005 temp commented

                for (i in 0..contactlist!!.size - 1 step ProgressStep) {

                    Log.d(TAG, "CallLog for 1 Contact Number of data jumped ${i}")

                    subcontactlist = contactlist!!.filter { it.id > i && it.id <= (ProgressStep + i) }


                    // region calling to server


                    val contactRequestEntity = ContactLeadRequestEntity(
                        fbaid = tfbaid,
                        ssid = ssid!!,
                        sub_fba_id = tsub_fba_id,
                        contactlist = subcontactlist,
                        raw_data = Gson().toJson(getAllContactDetails),
                        device_id = deviceID,
                        app_version = appversion
                    )


                    Log.d(Constant.TAG_SAVING_CONTACT_LOG, Gson().toJson(getAllContactDetails) )


                    val resultResp = RetroHelper.api.saveContactLead(url, contactRequestEntity)

                    if (resultResp?.isSuccessful == true) {


                        Log.d(TAG, "Response Done Contact : ${i}")

                        //region send Notification Progress


                        currentProgress = currentProgress + 1
                        val workProgess = workDataOf(Constant.CONTACT_LOG_Progress to currentProgress,
                            Constant.CONTACT_LOG_MAXProgress to maxProgress)
                        setProgress(workProgess)
                        if (currentProgress < maxProgress) {
                            setForeground(
                                createForegroundInfo(
                                    maxProgress = maxProgress,
                                    progress = currentProgress,
                                    strbody = strbody
                                )
                            )

                        } else {
                            setForeground(
                                createForegroundInfo(
                                    maxProgress = maxProgress,
                                    progress = currentProgress,
                                    strbody = strResultbody
                                )
                            )

                        }
                        //endregion
                        // delay(8000)

                    }
                    else{

                        Log.d(TAG, resultResp.toString())
                    }


                    //endregion




                }




            }

        }

        return ContactCount
    }

    private  fun getContactList1(): MutableList<ContactlistEntity> {

        var contactlist: MutableList<ContactlistEntity> = ArrayList<ContactlistEntity>()
        var templist: MutableList<String> = ArrayList<String>()
        var phones: Cursor? = null


        val PROJECTION = arrayOf(
            ContactsContract.RawContacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Photo.CONTACT_ID,
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.DATA1
        )

        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val filter =
            "" + ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0 and " + ContactsContract.CommonDataKinds.Phone.TYPE + "=" + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
        val order =
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"// LIMIT " + limit + " offset " + lastId + "";

        phones = applicationContext.contentResolver.query(uri, PROJECTION, filter, null, order)




        ///////////////////////////


        val regex = Regex("[^.0-9]")

        phones.let {

            if (phones != null && phones!!.getCount() > 0) {
                try {
                    var i = 1
                    while (phones.moveToNext()) {


                        var name =
                            "" + phones.getString(
                                phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME) ?: 0
                            )
                        var phoneNumber =
                            "" + phones.getString(
                                phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    ?: 0
                            )

                        //  phoneNumber = phoneNumber.trim().replace("[^0-9\\s+]", "");   // remove Special character and Space

                        phoneNumber = regex.replace(phoneNumber, "") // works
                        //.replace("\\s".toRegex(), "")

                        if (phoneNumber.length >= 10) {

                            phoneNumber = phoneNumber.takeLast(10)
                            // check whether the number alreday added to list or not

                            if (!templist!!.contains(phoneNumber)) {
                                templist?.add(phoneNumber)

                                val selectUser = ContactlistEntity(
                                    name = name,
                                    mobileno = phoneNumber,
                                    id = i
                                )
                                // Log.i(TAG, "Key ID: " + i + " Name: " + name + " Mobile: " + phoneNumber + "\n");
                                contactlist.add(selectUser)

                            }


                        }


                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }



        return contactlist


    }


    private suspend fun callContactTask(): Int = withContext(Dispatchers.IO) {
        var totalUploaded = 0
        val strbody = Constant.CONTACT_LOG_DataFetching

        // 1. Fetch data
        val contactlist = getContactList()
        if (contactlist.isEmpty()) return@withContext 0

        // 2. Prepare Meta Data
        val fbaid = inputData.getInt(Constant.KEY_fbaid, 0)
        val ssid = inputData.getString(Constant.KEY_ssid) ?: ""
        val parentid = inputData.getString(Constant.KEY_parentid) ?: ""
        val deviceID = inputData.getString(Constant.KEY_deviceid) ?: ""
        val appversion = inputData.getString(Constant.KEY_appversion) ?: ""

        val tfbaid = if (parentid.isEmpty() || parentid == "0") fbaid.toString() else parentid
        val tsub_fba_id = if (parentid.isEmpty() || parentid == "0") parentid else fbaid.toString()

        // 3. Optimization: Generate RAW JSON ONCE, not inside the loop
        val rawDataJson = try {
            val rawDetails = ContactHelper.getContact(context.applicationContext)
            Gson().toJson(rawDetails)
        } catch (e: Exception) { "" }

        // region For Testing Purpose
//        if (rawDataJson.isNotEmpty()) {
//
//            // Save inside app folder
//           // saveJsonToTxt(applicationContext, rawDataJson.toString())
//
//            withContext(Dispatchers.Main) {
//                Toast.makeText(
//                    applicationContext,
//                    "Contact backup saved in Downloads folder",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
        //endregion

        // 4. Batch Processing (1000 at a time)
        val totalSize = contactlist.size
        val maxProgress = (totalSize / ProgressStep) + 1
        var currentProgress = 1

        for (i in 0 until totalSize step ProgressStep) {
            // Use subList for efficient batching
            val end = minOf(i + ProgressStep, totalSize)
            val subBatch = contactlist.subList(i, end)

           // Log.d(Constant.TAG_SAVING_CONTACT_LOG, Gson().toJson(subBatch) )


            val request = ContactLeadRequestEntity(
                fbaid = tfbaid,
                ssid = ssid,
                sub_fba_id = tsub_fba_id,
                contactlist = subBatch,
                // OPTIMIZATION: Send raw_data ONLY with the first batch to save data
                raw_data = if (i == 0) rawDataJson else "",  // raw_data = rawDataJson, // Reusing the string

                device_id = deviceID,
                app_version = appversion
            )

            val url = "https://horizon.policyboss.com:5443/sync_contacts/contact_entry"
            val response = RetroHelper.api.saveContactLead(url, request)

            if (response?.isSuccessful == true) {
                currentProgress++
                setProgress(workDataOf(
                    Constant.CONTACT_LOG_Progress to currentProgress,
                    Constant.CONTACT_LOG_MAXProgress to maxProgress
                ))
                setForeground(createForegroundInfo(maxProgress, currentProgress, Constant.CONTACT_LOG_DataSending))
            }
        }
        return@withContext totalSize
    }

    private fun getContactList(): MutableList<ContactlistEntity> {
        val contactList = mutableListOf<ContactlistEntity>()
        val tempSet = HashSet<String>() // Fast O(1) duplicate check
        val regex = Regex("[^0-9]")

        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        // FIX: Removed TYPE_MOBILE filter to include ALL saved numbers
        val selection = "${ContactsContract.Contacts.HAS_PHONE_NUMBER} > 0"
        val order = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"

        val cursor = applicationContext.contentResolver.query(uri, projection, selection, null, order)

        cursor?.use { c ->
            var idCounter = 1
            val nameIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            if (nameIndex != -1 && numIndex != -1) {
                while (c.moveToNext()) {
                    try {
                        val name = c.getString(nameIndex) ?: "Unknown"
                        val rawNumber = c.getString(numIndex) ?: ""

                        val cleaned = regex.replace(rawNumber, "")

                        if (cleaned.length >= 10) {
                            val mobile = cleaned.takeLast(10)

                            // HashSet.add returns true only if the number is new
                            if (tempSet.add(mobile)) {
                                contactList.add(
                                    ContactlistEntity(
                                        name = name,
                                        mobileno = mobile,
                                        id = idCounter++
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Skipping corrupted row: ${e.message}")
                    }
                }
            }
        }
        return contactList
    }


    //region Creates notifications for service
    private fun createForegroundInfo(
        maxProgress: Int,
        progress: Int,
        strbody: String
    ): ForegroundInfo {


        val id = "com.utility.PolicyBossPro.notifications556"
        val channelName = "SynContact channel"
        val title = "Sync Contact"
        val cancel = "Cancel"

        val body = strbody
//            .setProgress(0, 0, true)    // for indeterminate progress

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(id, channelName)
        }


        // region Commented :--> For Handling  cancel
//        val intent = WorkManager.getInstance(applicationContext)
//            .createCancelPendingIntent(getId())

        // .setSummaryText(NotifyData.get("body"));

        //  new createBitmapFromURL(NotifyData.get("img_url")).execute();

        //endregion
        /////////////////////////////////////

        val notifyIntent = Intent(applicationContext, SyncContactActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        notifyIntent.putExtra(Constant.NOTIFICATION_EXTRA, true)
        notifyIntent.putExtra(Constant.NOTIFICATION_PROGRESS, progress)
        notifyIntent.putExtra(Constant.NOTIFICATION_MAX, maxProgress)
        notifyIntent.putExtra(Constant.NOTIFICATION_MESSAGE, strbody)

        //region Commented : Adding Pending Intent For Handling Notification Click Action
//        val notifyPendingIntent = PendingIntent.getActivities(
//            applicationContext, 0, arrayOf(notifyIntent), PendingIntent.FLAG_UPDATE_CURRENT
//        )

        //or

//        val notifyPendingIntent: PendingIntent
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            notifyPendingIntent = PendingIntent.getActivity(
//                applicationContext,
//                0, notifyIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
//            )
//        } else {
//            notifyPendingIntent = PendingIntent.getActivity(
//                applicationContext,
//                0, notifyIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT
//            )
//        }
//
        //endregion


        /////////////////////////
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationBuilder.setSmallIcon(com.policyboss.policybosspro.R.drawable.pb_pro_logo)
            notificationBuilder.color = applicationContext.getColor(com.policyboss.policybosspro.R.color.colorPrimary)
        } else {
            notificationBuilder.setSmallIcon(com.policyboss.policybosspro.R.drawable.pb_pro_logo)
        }

        // .addAction(android.R.drawable.ic_delete, cancel, intent)

        notificationBuilder
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(body)
            .setOngoing(false)
            .setProgress(maxProgress, progress, false)

            // .setContentIntent(notifyPendingIntent)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)

            .build()



//        return ForegroundInfo(1,
//            notificationBuilder.build(),
//            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)


        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ForegroundInfo(
                1,
                notificationBuilder.build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )

        } else {

            ForegroundInfo(
                1,
                notificationBuilder.build()
            )

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel1(id: String, channelName: String) {
        notificationManager.createNotificationChannel(
            NotificationChannel(id, channelName, NotificationManager.IMPORTANCE_DEFAULT)

        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(id: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                id,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.lightColor = Color.BLUE
            channel.description = "PoliyBoss Pro"
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            channel.lockscreenVisibility =
                Notification.VISIBILITY_PUBLIC // Notification.VISIBILITY_PRIVATE
            notificationManager.createNotificationChannel(channel)
        }
    }

    //endregion


    private fun saveJsonToTxt(context: Context, json: String) {

        try {

            val fileName = "contact_log_${System.currentTimeMillis()}.txt"

            val file = File(
                context.getExternalFilesDir(null),
                fileName
            )

            val writer = FileWriter(file)
            writer.append(json)
            writer.flush()
            writer.close()

            Log.d("ContactLog", "File saved: ${file.absolutePath}")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private fun saveToDownloads(context: Context, data: String,totalCount: Int) {

        val fileName = "sync_contact_${totalCount}_${System.currentTimeMillis()}.txt"

      //  val fileName = "contact_backup_${System.currentTimeMillis()}.txt"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            // Android 10+
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "text/plain")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {

                resolver.openOutputStream(it)?.use { output ->
                    output.write(data.toByteArray())
                }

                contentValues.clear()
                contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }

        } else {

            // Android 9 and below
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            val file = File(downloadsDir, fileName)

            try {
                val fos = FileOutputStream(file)
                fos.write(data.toByteArray())
                fos.flush()
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveToDownloads1(context: Context, data: String, totalCount: Int) {

        val fileName = "contact_backup_${totalCount}_${System.currentTimeMillis()}.txt"

        // Add total count in first line
        val finalData = "Total Contacts: $totalCount\n\n$data"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            // Android 10+
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "text/plain")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {

                resolver.openOutputStream(it)?.use { output ->
                    output.write(finalData.toByteArray())
                }

                contentValues.clear()
                contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)

                Toast.makeText(
                    context,
                    "File saved in Downloads: $fileName",
                    Toast.LENGTH_LONG
                ).show()
            }

        } else {

            // Android 9 and below
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            val file = File(downloadsDir, fileName)

            try {
                val fos = FileOutputStream(file)
                fos.write(finalData.toByteArray())
                fos.flush()
                fos.close()

                Toast.makeText(
                    context,
                    "File saved in Downloads: $fileName",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}