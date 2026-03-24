package com.policyboss.policybosspro.view.qrScanner.helper


import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.atomic.AtomicBoolean

/**
 * QRAnalyzer processes camera frames and detects QR codes using ML Kit.
 *
 * Important responsibilities:
 * 1. Prevent multiple frames processing simultaneously
 * 2. Throttle scanning for performance
 * 3. Pause scanning when a QR is detected
 */


class QRAnalyzer(
    private val previewView: PreviewView,
    private val scanRect: RectF, // from overlay
    private val onDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )

    private val isProcessing = AtomicBoolean(false)
    private val isPaused = AtomicBoolean(false)

    fun pause() = isPaused.set(true)
    fun resume() = isPaused.set(false)
    fun shutdown() = scanner.close()

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {

        if (isPaused.get() || !isProcessing.compareAndSet(false, true)) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image ?: run {
            isProcessing.set(false)
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        scanner.process(image)
            .addOnSuccessListener { barcodes ->

                val barcode = barcodes.firstOrNull()
                val boundingBox = barcode?.boundingBox
                val rawValue = barcode?.rawValue

                if (boundingBox != null && !rawValue.isNullOrBlank()) {

                    // ✅ STEP 1: Map QR box to screen
                    val mappedRect = calculateRectOnScreen(boundingBox, imageProxy)

                    // ✅ STEP 2: Create SAFE scan area (🔥 THIS IS YOUR CODE)
                    val safeRect = RectF(scanRect).apply {
                        inset(30f, 30f) // reduce edges
                    }

                    // ✅ STEP 3: Final validation (GPay-like)
                    if (isValidQrPosition(safeRect, mappedRect)) {
                        pause()
                        onDetected(rawValue)
                    }
                }
            }
            .addOnFailureListener {
                Log.e("QRAnalyzer", "QR scan failed")
            }
            .addOnCompleteListener {
                isProcessing.set(false)
                imageProxy.close()
            }
    }

    /**
     * ✅ FINAL VALIDATION (CENTER + % AREA)
     */
    private fun isValidQrPosition(scanRect: RectF, qrRect: RectF): Boolean {

        // 1️⃣ Center must be inside
        val isCenterInside = scanRect.contains(
            qrRect.centerX(),
            qrRect.centerY()
        )

        if (!isCenterInside) return false

        // 2️⃣ Area intersection
        val intersection = RectF()
        val intersects = intersection.setIntersect(scanRect, qrRect)

        if (!intersects) return false

        val intersectionArea = intersection.width() * intersection.height()
        val qrArea = qrRect.width() * qrRect.height()

        if (qrArea == 0f) return false

        val percentageInside = intersectionArea / qrArea

        // 🔥 BEST VALUE
        return percentageInside >= 0.55f
    }

    /**
     * ✅ Mapping image → screen (correct handling)
     */
    private fun calculateRectOnScreen(
        boundingBox: Rect,
        imageProxy: ImageProxy
    ): RectF {

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val isRotated = rotationDegrees == 90 || rotationDegrees == 270

        val srcW = if (isRotated) imageProxy.height else imageProxy.width
        val srcH = if (isRotated) imageProxy.width else imageProxy.height

        val dstW = previewView.width
        val dstH = previewView.height

        val scaleX = dstW.toFloat() / srcW.toFloat()
        val scaleY = dstH.toFloat() / srcH.toFloat()

        return RectF(
            boundingBox.left * scaleX,
            boundingBox.top * scaleY,
            boundingBox.right * scaleX,
            boundingBox.bottom * scaleY
        )
    }
}

//class QRAnalyzer(
//    private val previewView: PreviewView,
//    private val scanRect: RectF,          // ✅ NEW (pass from overlay)
//    private val onDetected: (String) -> Unit
//) : ImageAnalysis.Analyzer {
//
//    private val options = BarcodeScannerOptions.Builder()
//        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
//        .build()
//
//    private val scanner = BarcodeScanning.getClient(options)
//
//    private val isProcessing = AtomicBoolean(false)
//    private val isPaused = AtomicBoolean(false)
//
//    private var lastScanMs = 0L
//
//    fun pause() = isPaused.set(true)
//    fun resume() = isPaused.set(false)
//    fun shutdown() = scanner.close()
//
//    @OptIn(ExperimentalGetImage::class)
//    override fun analyze(imageProxy: ImageProxy) {
//
//        if (isPaused.get() || !isProcessing.compareAndSet(false, true)) {
//            imageProxy.close()
//            return
//        }
//
//        val now = System.currentTimeMillis()
//        if (now - lastScanMs < 400) {
//            isProcessing.set(false)
//            imageProxy.close()
//            return
//        }
//        lastScanMs = now
//
//        val mediaImage = imageProxy.image ?: run {
//            isProcessing.set(false)
//            imageProxy.close()
//            return
//        }
//
//        val image = InputImage.fromMediaImage(
//            mediaImage,
//            imageProxy.imageInfo.rotationDegrees
//        )
//
//        scanner.process(image)
//            .addOnSuccessListener { barcodes ->
//
//                val barcode = barcodes.firstOrNull()
//                val value = barcode?.rawValue?.takeIf { it.isNotBlank() }
//                val boundingBox = barcode?.boundingBox
//
//                if (value != null && boundingBox != null) {
//
//                    // 🔥 IMPORTANT CHANGE
//                    val mappedRect = mapToPreview(boundingBox, imageProxy)
//
//                    if (scanRect.contains(mappedRect)) {
//                        pause()
//                        onDetected(value)
//                    }
//                }
//            }
//            .addOnFailureListener {
//                Log.e("QRAnalyzer", "QR scan failed")
//            }
//            .addOnCompleteListener {
//                isProcessing.set(false)
//                imageProxy.close()
//            }
//    }
//
//    /**
//     * 🔥 CORE LOGIC: Convert Image → Preview coordinates
//     */
//    private fun mapToPreview(rect: Rect, imageProxy: ImageProxy): RectF {
//
//        val imageWidth = imageProxy.width.toFloat()
//        val imageHeight = imageProxy.height.toFloat()
//
//        val previewWidth = previewView.width.toFloat()
//        val previewHeight = previewView.height.toFloat()
//
//        val scaleX = previewWidth / imageHeight   // ⚠️ rotated
//        val scaleY = previewHeight / imageWidth
//
//        return RectF(
//            rect.left * scaleX,
//            rect.top * scaleY,
//            rect.right * scaleX,
//            rect.bottom * scaleY
//        )
//    }
//}

