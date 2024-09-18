package com.policyboss.policybosspro.utility

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.policyboss.policybosspro.core.model.DeviceDetailEntity
import java.io.File
import java.io.FileOutputStream

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


    fun loadWebViewUrlInBrowser(context: Context, url: String) {
        Log.d("URL", url)
        val browserIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        context.startActivity(browserIntent)
    }
}

