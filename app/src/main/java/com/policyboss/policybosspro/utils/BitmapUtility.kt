package com.policyboss.policybosspro.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URL

//****************************************************************************//

// Mark : Customized Bitmap {Convert Url to Customized Bitmap}
//given : Image Url, Image height ,textSize,textMargin etc
// data : Posp Detail {name , mob, design etc}
//****************************************************************************//
object BitmapUtility {

    suspend fun downloadBitmapFromUrl(url: URL): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection()
                connection.connect()
                val inputStream = connection.getInputStream()
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Creates a bitmap with the pospPhoto on the left and text (name, designation, mobile, email) on the right.
     */


    fun createBitmap(
        pospPhoto: Bitmap,
        pospName: String,
        pospDesg: String,
        pospMob: String,
        pospEmail: String,
        textSize: Float = 25F,
        height: Int = 200,
        width: Int = 2000,
        textMargin: Int = 10,
        startHeight:Float
    ): Bitmap {
        // Scale the pospPhoto bitmap
        val scaledPospPhoto = Bitmap.createScaledBitmap(pospPhoto, height - 20, height - 20, false)

        // Create a bitmap for the text
        val textBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(textBitmap)
        canvas.drawColor(Color.WHITE)

        // Paint for normal text
        val paint = Paint().apply {
            color = Color.BLACK
            this.textSize = textSize
        }

        // Paint for bold text
        val paintBold = Paint().apply {
            color = Color.BLACK
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            this.textSize = textSize + 1
        }

        // Draw the text on the canvas
        var currentHeight = startHeight + 10
        canvas.drawText(pospName, height + 20f, currentHeight + textMargin, paintBold)
        canvas.drawText(pospDesg, height + 20f, currentHeight + 1 * textSize + 2 * textMargin, paint)
        canvas.drawText(pospMob, height + 20f, currentHeight + 2 * textSize + 3 * textMargin, paint)
        canvas.drawText(pospEmail, height + 20f, currentHeight + 3 * textSize + 4 * textMargin, paint)

        // Combine the text and the photo into a single bitmap
        val combinedBitmap = Bitmap.createBitmap(2000, height, Bitmap.Config.ARGB_8888)
        val comboCanvas = Canvas(combinedBitmap)

        // Draw the text and pospPhoto on the new bitmap
        comboCanvas.drawBitmap(textBitmap, 0f, 0f, null)
        comboCanvas.drawBitmap(scaledPospPhoto, 0f, 15f, null)

        return combinedBitmap
    }

    /**
     * Combines two bitmaps vertically. If the second bitmap is null, it returns the first bitmap.
     */
    fun combineImages(first: Bitmap, second: Bitmap?): Bitmap {
        val width: Int
        val height: Int
        val combinedBitmap: Bitmap

        if (second == null) {
            // If only one image is present, just return the first image
            width = first.width
            height = first.height
            combinedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            Canvas(combinedBitmap).drawBitmap(first, 0f, 0f, null)
        } else {
            // If both images are present, stack them vertically
            width = first.width
            height = first.height + second.height
            combinedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            val comboCanvas = Canvas(combinedBitmap)
            comboCanvas.drawBitmap(first, 0f, 0f, null)
            comboCanvas.drawBitmap(second, 0f, first.height.toFloat(), null)
        }

        // Optionally save the combined image to the given location (if `loc` is provided)
        // Save combined image to the Downloads folder
      //  saveImageToDownloads(context, combinedBitmap)

        return combinedBitmap
    }

    fun saveImageToDownloads(context: Context, bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.png"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 (API 29) and above, use MediaStore
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                var outputStream: OutputStream? = null
                try {
                    // Open output stream to write the bitmap
                    outputStream = resolver.openOutputStream(it)
                    outputStream?.let { stream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    }
                } catch (e: IOException) {
                    Log.e("BitmapUtility", "Error saving image to Downloads", e)
                } finally {
                    outputStream?.close()

                    // Mark the image as not pending anymore
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                }
            } ?: run {
                Log.e("BitmapUtility", "Failed to create new MediaStore record")
            }

        } else {
            // For Android 9 (API 28) and below, save directly to the Downloads folder
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, filename)

            var outputStream: OutputStream? = null
            try {
                // Save the bitmap to the file in the Downloads folder
                outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream as FileOutputStream)
                Log.d("BitmapUtility", "Image saved to Downloads: ${file.absolutePath}")
            } catch (e: IOException) {
                Log.e("BitmapUtility", "Error saving image to Downloads", e)
            } finally {
                outputStream?.close()
            }
        }
    }


    /**
     * Decodes a byte array into a Bitmap.
     */
    fun decodeBitmap(byteArray: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun bitmapToByteArray(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = 100): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(format, quality, stream)
        return stream.toByteArray()
    }

}
