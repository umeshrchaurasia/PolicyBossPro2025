package com.policyboss.policybosspro.view.vehicleScanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.policyboss.policybosspro.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min


import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

import com.policyboss.policybosspro.databinding.ActivityLoginBinding
import com.policyboss.policybosspro.databinding.ActivityVehiclePlateReaderBinding
import com.policyboss.policybosspro.utils.Constant


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext



import kotlin.math.min
import kotlin.math.max



class VehiclePlateReaderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVehiclePlateReaderBinding
    companion object {
        private const val TAG = "VehiclePlateReader"
    }

    // UI components
    private lateinit var cameraPreview: PreviewView
    private lateinit var plateTextView: TextView
    private lateinit var btnDetect: Button
    private lateinit var btnBack: Button
    private lateinit var detectionOverlay: DetectionOverlayLatestView


    // Camera related
    private var camera: Camera? = null
    private lateinit var cameraExecutor: ExecutorService
    private var detectionRectangle = Rect()

    override fun onCreate(savedInstanceState: Bundle?) {

        // 💡 ADD THIS LINE HERE
       // enableEdgeToEdge()
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_vehicle_plate_reader)
        binding = ActivityVehiclePlateReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Add this code to handle the insets
       // handleWindowInsets()

        // Initialize UI components
        cameraPreview = binding.cameraPreview
        plateTextView = binding.plateTextView
        btnDetect =  binding.btnDetect
        btnBack = binding.btnBack
        detectionOverlay = binding.detectionOverlay

       // detectionOverlay.heightRatio = 0.4f

        // Initialize executor for camera operations
        cameraExecutor = Executors.newSingleThreadExecutor()

        btnDetect.setOnClickListener {
            val plateText = plateTextView.text.toString()
            if (plateText.isNotEmpty() && plateText != getString(R.string.plate_scan_no_text)) {
                returnResult(plateText)
            }
        }

        btnBack.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        // Check and request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions.launch(arrayOf(Manifest.permission.CAMERA))
        }


        // You can uncomment this during development to test the OCR correction logic
        // testOCRCorrection()
    }

    private fun handleWindowInsets() {
        // Find the view you want to move (your button container)
        val viewToPad = binding.buttonContainer

        ViewCompat.setOnApplyWindowInsetsListener(viewToPad) { view, windowInsets ->
            // Get the insets for the system bars (status bar and navigation bar)
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply the bottom inset as padding to the bottom of your view
            view.updatePadding(bottom = insets.bottom)

            // Return the insets so other views can use them if needed
            WindowInsetsCompat.CONSUMED
        }
    }




    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = cameraPreview.surfaceProvider
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageForPlateDetection(imageProxy)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
                setupCameraForPlateReading()
            } catch (e: Exception) {
                Log.e(TAG, "Use case binding failed", e)
                showToast("Camera initialization failed: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun setupCameraForPlateReading() {
        camera?.cameraControl?.apply {
            enableTorch(false)
            setLinearZoom(0.0f)
            val meteringPoint = SurfaceOrientedMeteringPointFactory(1.0f, 1.0f).createPoint(0.5f, 0.5f)
            val focusAction = FocusMeteringAction.Builder(meteringPoint)
                .setAutoCancelDuration(2, TimeUnit.SECONDS)
                .build()
            startFocusAndMetering(focusAction)
        }
    }

    @ExperimentalGetImage
    private fun processImageForPlateDetection(imageProxy: androidx.camera.core.ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            detectionRectangle = detectionOverlay.getDetectionRectInImageCoordinates(imageProxy.width, imageProxy.height)

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    processDetectedText(visionText)
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Text recognition failed", e)
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun processDetectedText(visionText: Text) {
        // Use lifecycleScope.launch to safely update the UI from a background thread.
        // This is the modern replacement for runOnUiThread.
        lifecycleScope.launch {
            val detectedText = getLicensePlateText(visionText)

            if (detectedText.isNotEmpty()) {
                Log.d(TAG, "Raw detected text from zone: $detectedText")
                val vehicleNumber = extractVehicleNumber(detectedText)

                if (!vehicleNumber.isNullOrEmpty()) {
                    Log.d(TAG, "Extracted vehicle number: $vehicleNumber")
                    if (isValidVehicleNumber(vehicleNumber)) {
                        val formattedPlate = formatVehicleNumber(vehicleNumber)
                        Log.d(TAG, "Final Valid & Formatted Plate: $formattedPlate")

                        plateTextView.text = formattedPlate
                        plateTextView.visibility = View.VISIBLE
                        plateTextView.setBackgroundResource(R.drawable.plate_background_success)

                        // Delay using coroutines instead of Handler
                        delay(400)
                        returnResult(formattedPlate)

                    } else {
                        Log.d(TAG, "Invalid vehicle number structure: $vehicleNumber")
                        plateTextView.text = vehicleNumber
                        plateTextView.visibility = View.VISIBLE
                        plateTextView.setBackgroundResource(R.drawable.plate_background_neutral)
                    }
                } else {
                    val firstLine = detectedText.split('\n').firstOrNull() ?: ""
                    val displayText = firstLine.take(20)
                    Log.d(TAG, "No valid vehicle number pattern found in: $displayText")
                    plateTextView.text = displayText
                    plateTextView.visibility = View.VISIBLE
                    plateTextView.setBackgroundResource(R.drawable.plate_background_neutral)
                }
            } else {
                plateTextView.text = getString(R.string.plate_scan_no_text)
                plateTextView.visibility = View.VISIBLE
                plateTextView.setBackgroundResource(R.drawable.plate_background_error)
            }
        }
    }

    private fun getLicensePlateText(visionText: Text): String {
        val candidateLines = mutableListOf<Pair<String, Float>>()
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val boundingBox = line.boundingBox ?: continue
                if (isTextInDetectionZone(boundingBox, detectionRectangle)) {
                    val overlapScore = calculateOverlapScore(boundingBox, detectionRectangle)
                    candidateLines.add(Pair(line.text, overlapScore))
                }
            }
        }
        candidateLines.sortByDescending { it.second }
        return candidateLines.take(2).joinToString("\n") { it.first }
    }

    private fun isTextInDetectionZone(textBox: Rect, detectionZone: Rect): Boolean {
        if (detectionZone.contains(textBox)) return true
        val intersection = Rect()
        if (intersection.setIntersect(textBox, detectionZone)) {
            val intersectionArea = intersection.width() * intersection.height()
            val textBoxArea = textBox.width() * textBox.height()
            return intersectionArea > (textBoxArea * 0.3)
        }
        return false
    }

    private fun calculateOverlapScore(textBox: Rect, detectionZone: Rect): Float {
        val intersection = Rect()
        if (!intersection.setIntersect(textBox, detectionZone)) return 0f

        val intersectionArea = (intersection.width() * intersection.height()).toFloat()
        val textBoxArea = (textBox.width() * textBox.height()).toFloat()
        val overlapPercentage = intersectionArea / textBoxArea

        val textCenterX = textBox.centerX()
        val zoneCenterX = detectionZone.centerX()
        val distance = (textCenterX - zoneCenterX).toFloat()
        val maxDistance = (detectionZone.width() / 2.0).toFloat()
        val proximityScore = 1f - (distance / maxDistance)

        return (overlapPercentage * 0.4f) + (proximityScore * 0.6f)
    }

    private fun returnResult(plateText: String) {
        val resultIntent = Intent()
        Log.d(TAG, "Returning result: $plateText")
        resultIntent.putExtra(Constant.KEY_VEHICLE_DETECT_TEXT, plateText.toByteArray(Charsets.UTF_8))
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    // MARK: - Permissions and Dialogs
    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.CAMERA] == true) {
                startCamera()
            } else {
                showPermissionDeniedDialog()
            }
        }

    private fun showPermissionDeniedDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Camera Permission Required")
            .setMessage("This app needs camera access to scan vehicle plates. Please grant permission.")
            .setPositiveButton("OK") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    // MARK: - Vehicle Number Processing Logic

    private val VALID_STATE_CODES = setOf(
        "AP", "AR", "AS", "BR", "CG", "CH", "DD", "DL", "DN", "GA",
        "GJ", "HP", "HR", "JH", "JK", "KA", "KL", "LA", "LD", "MH",
        "ML", "MN", "MP", "MZ", "NL", "OD", "PB", "PY", "RJ", "SK",
        "TN", "TR", "TS", "UK", "UP", "WB"
    )

    /**
     * Extracts the most likely vehicle number from a block of text.
     */
    private fun extractVehicleNumber(text: String): String? {
        Log.d(TAG, "Input for extraction: $text")

        // 1. Clean the text: remove spaces/special chars and uppercase it.
        val cleanText = text.replace("[^A-Za-z0-9]".toRegex(), "").uppercase()
        Log.d(TAG, "Cleaned text: $cleanText")

        // 2. Find all potential matches using a flexible regex.
        val vehicleNumberPattern = Regex("[A-Z0-9]{8,11}")
        val matches = vehicleNumberPattern.findAll(cleanText).map { it.value }.toList()

        // 3. Try to validate and correct each match.
        for (match in matches) {
            val correctedPlate = applyOCRCorrections(match)
            Log.d(TAG, "Attempting correction: $match -> $correctedPlate")
            if (isValidVehicleNumber(correctedPlate)) {
                Log.d(TAG, "Found valid plate after correction: $correctedPlate")
                return correctedPlate
            }
        }

        // 4. Fallback for cases where the first letter of state code is missing (e.g., "H01..." instead of "MH01...").
        if (cleanText.length >= 2) {
            val firstChar = cleanText[0]
            for (stateCode in VALID_STATE_CODES) {
                if (stateCode[1] == firstChar) {
                    val prependedText = stateCode[0] + cleanText
                    val correctedPlate = applyOCRCorrections(prependedText)
                    if (isValidVehicleNumber(correctedPlate)) {
                        Log.d(TAG, "Corrected by prepending state code: $correctedPlate")
                        return correctedPlate
                    }
                }
            }
        }

        Log.d(TAG, "No valid vehicle number found in '$text'")
        return matches.firstOrNull() // Return the best guess if no valid plate is found
    }

    /**
     * This is the core of the intelligent correction logic.
     * It corrects characters based on their expected type (letter/digit) at a specific position.
     * Format: [LL] [DD] [LLL] [DDDD]
     * State RTO Series Number
     */
    private fun applyOCRCorrections(plateText: String): String {
        if (plateText.length < 8) return plateText // Not long enough to be a plate

        val corrected = StringBuilder(plateText)
        Log.d(TAG, "Applying OCR corrections to: $plateText")

        // Positions 0, 1: State Code (must be letters)
        for (i in 0..min(1, corrected.length - 1)) {
            corrected[i] = correctToLetter(corrected[i])
        }

        // Positions 2, 3: RTO Code (must be digits)
        // This is where "MHO1..." becomes "MH01..."
        for (i in 2..min(3, corrected.length - 1)) {
            corrected[i] = correctToDigit(corrected[i])
        }

        // The last 4 characters are the vehicle number (must be digits)
        val numberStartIndex = max(4, corrected.length - 4)
        for (i in numberStartIndex until corrected.length) {
            corrected[i] = correctToDigit(corrected[i])
        }

        // The characters between RTO and Number are the series code (must be letters)
        val seriesStartIndex = 4
        for (i in seriesStartIndex until numberStartIndex) {
            if (i < corrected.length) {
                corrected[i] = correctToLetter(corrected[i])
            }
        }

        val result = corrected.toString()
        Log.d(TAG, "Correction result: $plateText -> $result")
        return result
    }

    private fun correctToLetter(char: Char): Char = when (char) {
        '0' -> 'O'
        else -> char
    }

    private fun correctToDigit(char: Char): Char = when (char) {
        'O' -> '0'
        else -> char
    }

    /**
     * Validates if the text string is a valid Indian vehicle number.
     */
    private fun isValidVehicleNumber(text: String): Boolean {
        // A final cleanup before validation
        val cleanText = text.replace("[^A-Za-z0-9]".toRegex(), "").uppercase()

        if (cleanText.length !in 8..11) {
            Log.d(TAG, "Validation fail: Invalid length (${cleanText.length}) for '$cleanText'")
            return false
        }

        val stateCode = cleanText.substring(0, 2)
        if (!VALID_STATE_CODES.contains(stateCode)) {
            Log.d(TAG, "Validation fail: Invalid state code '$stateCode' for '$cleanText'")
            return false
        }

        // Regex for the final structure: LL DD LLL DDDD (parts are optional)
        val regex = "^[A-Z]{2}\\d{1,2}[A-Z]{1,3}\\d{1,4}$".toRegex()
        val isValid = regex.matches(cleanText)
        if (!isValid) {
            Log.d(TAG, "Validation fail: Regex mismatch for '$cleanText'")
        }
        return isValid
    }

    /**
     * Formats the final, validated number by ensuring it's clean and uppercase.
     */
    private fun formatVehicleNumber(vehicleNumber: String): String {
        return vehicleNumber.replace("[^A-Za-z0-9]".toRegex(), "").uppercase()
    }

    /**
     * A helper function to test the correction logic during development.
     */
    private fun testOCRCorrection() {
        val testCases = mapOf(
            "MHO1EN4382" to "MH01EN4382", // O -> 0 in RTO
            "KAO5AB1234" to "KA05AB1234"  // O -> 0 in RTO
        )

        Log.d(TAG, "=== Starting OCR Correction Test ===")
        testCases.forEach { (input, expected) ->
            val corrected = applyOCRCorrections(input)
            val isValid = isValidVehicleNumber(corrected)
            Log.d(TAG, "Test: '$input' -> '$corrected' (Expected: '$expected'). Correct: ${corrected == expected}, Valid: $isValid")
        }
        Log.d(TAG, "=== Finished OCR Correction Test ===")
    }
}