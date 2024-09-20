package com.policyboss.policybosspro.utility

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.policyboss.policybosspro.core.model.DeviceDetailEntity
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utility {


      const val ErrorMessage : String = "Data Not Found.Please try Again!!"

        @JvmStatic
        fun getFilePath(context: Context, contentUri: Uri): String? {
            try {
                val filePathColumn = arrayOf(
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.TITLE,
                    MediaStore.Files.FileColumns.SIZE,
                    MediaStore.Files.FileColumns.DATE_ADDED,
                    MediaStore.Files.FileColumns.DISPLAY_NAME,
                )

                val returnCursor = contentUri.let { context.contentResolver.query(it, filePathColumn, null, null, null) }

                if (returnCursor != null) {

                    returnCursor.moveToFirst()
                    val nameIndex = returnCursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                    val name = returnCursor.getString(nameIndex)
                    val file = File(context.cacheDir, name)
                    val inputStream = context.contentResolver.openInputStream(contentUri)
                    val outputStream = FileOutputStream(file)
                    var read: Int
                    val maxBufferSize = 1 * 1024 * 1024
                    val bytesAvailable = inputStream!!.available()

                    val bufferSize = Math.min(bytesAvailable, maxBufferSize)
                    val buffers = ByteArray(bufferSize)

                    while (inputStream.read(buffers).also { read = it } != -1) {
                        outputStream.write(buffers, 0, read)
                    }

                    inputStream.close()
                    outputStream.close()
                    return file.absolutePath
                }
                else
                {
                    Log.d("","returnCursor is null")
                    return null
                }
            }
            catch (e: Exception) {
                Log.d("","exception caught at getFilePath(): $e")
                return null
            }
        }

       @JvmStatic
       fun isFileLessThan5MB(file: File): Boolean {
        val maxFileSize = 5 * 1024 * 1024
        val l = file.length()
        val fileSize = l.toString()
        val finalFileSize = fileSize.toInt()
        return finalFileSize >= maxFileSize
    }


    @JvmStatic
    fun getDeviceDetail(context: Context) : DeviceDetailEntity {

      return  DeviceDetailEntity(

            Model = Build.MODEL ?: "",
            ID = Build.ID?: "",

            SDK =  ""+ Build.VERSION.SDK_INT,
            Manufacture = Build.MANUFACTURER?: "",
            Brand = Build.BRAND?: "",
            VersionCode = Build.VERSION.RELEASE?: ""
        )
    }


    @JvmStatic
    @SuppressLint("HardwareIds")
    fun getDeviceID(context: Context): String {
        try {
            return  Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }catch (ex: Exception){
            return ""
        }

    }

    @JvmStatic
    @SuppressLint("HardwareIds")
    fun getDeviceName(): String {
        try {
            return  "${Build.BRAND}-${Build.MODEL}"
        }catch (ex: Exception){
            return ""
        }

    }
    @SuppressLint("HardwareIds")
    fun getOS(): String {
        try {
            return  "Android:${Build.VERSION.RELEASE}"
        }catch (ex: Exception){
            return ""
        }

    }


    @JvmStatic
    fun copyTextToClipboard(str : String,context: Context ) {
        val textToCopy = str

        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)


        Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_LONG).show()
    }


    @JvmStatic
    fun loadWebViewUrlInBrowser(context: Context, url: String) {
        Log.d("URL", url)
        val browserIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        context.startActivity(browserIntent)
    }


    @JvmStatic
    fun getVersionName(context: Context): String {
        return try {
            val pinfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pinfo.versionName ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }

    @JvmStatic
    fun getVersionCode(context: Context): Long {
        return try {
            val pinfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                pinfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                pinfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            0L
        }
    }

    @JvmStatic
    fun createDirIfNotExists(): File? {
        val file = File(Environment.getExternalStorageDirectory(), "/PolicyBossPro")
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog", "Problem creating Image folder")
                return null
            }
        }
        return file
    }

    @JvmStatic
    fun createShareDirIfNotExists(): File? {
        val file = File(Environment.getExternalStorageDirectory(), "/PolicyBossPro/QUOTES")
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog", "Problem creating Quotes folder")
                return null
            }
        }
        return file
    }

    @JvmStatic
    fun getNewFileName(name: String): String? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return "$name$timeStamp.jpg"
    }

    @JvmStatic
    fun getPdfFileName(name: String): String? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return "$name$timeStamp.pdf"
    }

    @JvmStatic
    fun getImageDirectoryPath(): String? {
        return Environment.DIRECTORY_PICTURES + File.separator + "PolicyBossPro"
    }


    @JvmStatic
    fun getCurrentMobileDateTime(): String {
        val sdf = SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault())
        return sdf.format(Date())
    }

    @JvmStatic
    fun getMultipartImage(file: File): MultipartBody.Part {
//        val imgBody = RequestBody.create(MediaType.parse("image/*"), file)
//        return MultipartBody.Part.createFormData("DocFile", file.name, imgBody)

        val mediaType = "image/*".toMediaTypeOrNull() // Use the new `toMediaTypeOrNull()` method
        val imgBody = file.asRequestBody(mediaType)  // Use `asRequestBody()` extension function
        return MultipartBody.Part.createFormData("DocFile", file.name, imgBody)

    }

    @JvmStatic
    fun getMultipartImage(file: File, serverKey: String): MultipartBody.Part {
        val mediaType = "image/*".toMediaTypeOrNull()
        val imgBody = file.asRequestBody(mediaType)
        return MultipartBody.Part.createFormData(serverKey, file.name, imgBody)
    }

    @JvmStatic
    fun getMultipartPdf(file: File, fileName: String, serverKey: String): MultipartBody.Part {
//        val pdfBody = RequestBody.create(MediaType.parse("file/*"), file)
//        return MultipartBody.Part.createFormData(serverKey, fileName, pdfBody)

        val mediaType = "application/pdf".toMediaType()  // Use correct media type for PDFs
        val pdfBody = file.asRequestBody(mediaType)      // Create RequestBody for PDF file
        return MultipartBody.Part.createFormData(serverKey, fileName, pdfBody)
    }

    @JvmStatic
    fun getBodyCommon(context: Context, id: String, crn: String, fileType: String, insurerId: String): HashMap<String, String> {
        return hashMapOf(
            "crn" to crn,
            "document_id" to id,
            "insurer_id" to insurerId,
            "document_type" to fileType
        )
    }

}

