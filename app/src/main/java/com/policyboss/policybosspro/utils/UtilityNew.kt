package com.policyboss.policybosspro.utility

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.SmsManager
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.policyboss.policybosspro.BuildConfig
import com.policyboss.policybosspro.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


object UtilityNew {

    //val dialog = Dialog( MyApplication.getInstance.applicationContext,R.style.Dialog)

    //  val   formatter  =  SimpleDateFormat("dd-MMM-yyyy HH:mm:ss, Locale.US")

    fun getFormatter() : DateFormat {


        return SimpleDateFormat("dd-MMM-yyyy HH:mm:ss, Locale.US")

    }
    fun isOverlayPermissionExist(context: Context) : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(context)) {

                return true
            }else{

                return false
            }
        }else{

            return true
        }

    }

    fun getDate(lnDate: Date) : String{

        try {
            // var  formatter  =  SimpleDateFormat("dd-MMM-yyyy HH:mm:ss")
            val   formatter  =  SimpleDateFormat("dd-MMM-yyyy HH:mm:ss")
            return  formatter.format(lnDate)

        }catch (ex : Exception){
            return ""
        }


    }

    fun compareDate(strCalculateTime : Long)  : Int{

        try {
            if(strCalculateTime.equals(0)){
                return 1
            }else{


                var currerntdate = Calendar.getInstance().timeInMillis


                return  currerntdate.compareTo(strCalculateTime)


            }


        }catch (ex : Exception){

            return 1
        }
    }
    fun compareDate1(strCalculateTime : String)  : Int{

        try {
            if(strCalculateTime.isEmpty()){
                return 1
            }else{

                var  formatter  =  SimpleDateFormat("dd-MMM-yyyy HH:mm:ss")
                var currerntdate = Calendar.getInstance().time

                var calTime = formatter.parse(strCalculateTime)

               // Log.d(Constant.TAG,"Compare Diff ${currerntdate.compareTo(calTime)}")
                return currerntdate.compareTo(calTime)


            }


        }catch (ex : Exception){

            return 1
        }
    }

    fun getCurrentTime() : String{

        try {
            var  formatter  =  SimpleDateFormat("dd-MMM-yyyy HH:mm:ss")
            return formatter.format(Calendar.getInstance().time)

        }catch (ex : Exception){
            return ""
        }


    }


    fun isBackgroundPermissionExist(context: Context) : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val packageName = context.packageName
            val pm = context.getSystemService(AppCompatActivity.POWER_SERVICE) as PowerManager
            if (pm.isIgnoringBatteryOptimizations(packageName)) {

                return true
            }else{

                return false
            }
        }else{

            return true
        }

    }





    open fun showAlert(context: Context, title : String = "PolicyBossProCaller", msg : String,

                       action: (strType: String,dialog : DialogInterface) -> Unit) {
        val alertDialog = AlertDialog.Builder(context)


        alertDialog.apply {
            setIcon(R.drawable.logo_policyboss)
            setTitle(title)
            setMessage(msg)
            setCancelable(false)
            setPositiveButton("OK") {dialog, whichButton ->

                //dialog.dismiss()
                action("Y",dialog)

            }

            setNegativeButton("Cancel") { dialog, whichButton ->
                // dialog.dismiss()
                action("N", dialog)
            }
//        setNeutralButton("Neutral") { _, _ ->
//            toast("clicked neutral button")
//        }
        }.create().show()
    }

    fun settingDialog(context: Context, action: (strType: String, dialog: DialogInterface) -> Unit){

        val dialogBuilder = AlertDialog.Builder(context)

        // set message of alert dialog
        dialogBuilder.setMessage(context.getString(R.string.permission_required))
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("OPEN SETTING", DialogInterface.OnClickListener { dialog, id ->


                action("Y" , dialog)

            }).setNegativeButton("CANCEL", DialogInterface.OnClickListener{ dialog, id ->

                action("N" , dialog)

            })



        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("PolicyBoss Pro")
        // show alert dialog
        alert.show()
    }



    open fun showlogout(context: Context, title : String = "PolicyBossProCaller", msg : String,

                        action: (strType: String,dialog : DialogInterface) -> Unit) {
        val alertDialog = AlertDialog.Builder(context)


        alertDialog.apply {
            setIcon(R.drawable.logo_policyboss1)
            setTitle(title)
            setMessage(msg)
            setCancelable(false)
            setPositiveButton("Logout") {dialog, whichButton ->

                //dialog.dismiss()
                action("Y",dialog)

            }

            setNegativeButton("Cancel") { dialog, whichButton ->
                // dialog.dismiss()
                action("N", dialog)
            }
//        setNeutralButton("Neutral") { _, _ ->
//            toast("clicked neutral button")
//        }
        }.create().show()
    }


    open fun noInternetDialog(context: Context) {
        AlertDialog.Builder(context)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("No Internet")
            .setMessage("Please check your internet connection.")
            .setPositiveButton("CLOSE") { dialog, i ->
                dialog.dismiss()



            }.show()
    }

    fun uriFromFile(context: Context, file: File): Uri {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
        }
        else
        {
            return Uri.fromFile(file)
        }
    }


    fun createImageUri(context: Context) : Uri {

        val image = File(context.filesDir,"camera_photo.png")

        return FileProvider.getUriForFile(context.applicationContext,
            "com.example.policybosscaller.fileprovider",
            image
        )

    }






    fun openSetting(context: Context){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }

    open fun createImageFile(name: String,context: Context): File? {
        // Create an image file name
        val temp: File
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = getAppSpecificAlbumStorageDir(
            context.applicationContext,
            Environment.DIRECTORY_PICTURES,
            "PolicyBossProElite"
        )
        try {
            temp = File.createTempFile(
                name + timeStamp,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
            )
            Log.d("IMAGE_PATH", "File Name" + temp.name + "File Path" + temp.absolutePath)
            //  String  currentPhotoPath = temp.getAbsolutePath();
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return temp
    }

    open fun getAppSpecificAlbumStorageDir(
        context: Context,
        albumName: String?,
        subAlbumName: String?
    ): File {
        // Get the pictures directory that's inside the app-specific directory on
        // external storage.
        val file = File(context.getExternalFilesDir(albumName), subAlbumName)
        if (file.mkdirs()) {
            Log.e("fssfsf", "Directory not created")
        }
        return file
    }

    // URI TO Bitmap
    fun getBitmapFromContentResolver(selectedFileUri: Uri?, context: Context): Bitmap? {
        return try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(
                selectedFileUri!!, "r"
            )
            val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor!!.close()
            image
        } catch (e: IOException) {
            e.printStackTrace()

            null
        }
    }




    open  fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

//    private fun bitmapToBase64(bitmap: Bitmap): String? {
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
//        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
//        return Base64.encodeToString(byteArray, Base64.DEFAULT)
//    }



    fun loadWebViewUrlInBrowser(context: Context, url: String?) {
        Log.d("URL", url!!)
        val browserIntent = Intent(Intent.ACTION_VIEW)
        if (Uri.parse(url) != null) {
            browserIntent.data = Uri.parse(url)
        }
        context.startActivity(browserIntent)
    }


    open fun smsHandling(){
        val phoneNumber = "9224624999" // replace with the phone number you want to send the SMS to

        val message = "Hello, this is a test message!"

//        val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            applicationContext.getSystemService(SmsManager::class.java)
//        } else {
//            SmsManager.getDefault()
//        }
        try {
            val smsManager: SmsManager = SmsManager.getDefault() as SmsManager
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)


        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    /****************************************************************
    //Note : Download any Type of File and Images Using URL
     ****************************************************************/
    fun downloadFileFromUri(context: Context, url: String, mimeType: String, filename: String?): Uri? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val resolver = context.applicationContext.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            return if (uri != null) {
                URL(url).openStream().use { input ->
                    resolver.openOutputStream(uri).use { output ->
                        input.copyTo(output!!, DEFAULT_BUFFER_SIZE)
                    }

                }
                uri
            } else {
                null
            }

        } else {

            val target = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                filename
            )
            URL(url).openStream().use { input ->
                FileOutputStream(target).use { output ->
                    input.copyTo(output)
                }
            }

            return target.toUri()
        }
    }



}