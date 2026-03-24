package com.policyboss.policybosspro.view.qrScanner


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.databinding.ActivityScannerBinding
import com.policyboss.policybosspro.utils.AppPermission.AppPermissionManager
import com.policyboss.policybosspro.utils.AppPermission.PermissionHandler
import com.policyboss.policybosspro.utils.ExtensionFun.dp
import com.policyboss.policybosspro.utils.showToast
import com.policyboss.policybosspro.view.qrScanner.dialog.InvalidQrBottomSheet
import com.policyboss.policybosspro.view.qrScanner.dialog.LoginConfirmBottomSheet
import com.policyboss.policybosspro.view.qrScanner.helper.QRAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/*
        Key Concepts You Should Remember :---
>> CameraX Analyzer Thread
         ExecutorService
        Handles continuous frame processing.

>>UI Thread
        lifecycleScope.launch
        Handles UI safely.

>>Thread Safety
        AtomicBoolean
        Prevents multiple scans.

>>Backpressure Strategy
        STRATEGY_KEEP_ONLY_LATEST
         If processing slow:

        Frame1
        Frame2
        Frame3

Only latest frame processed.

/////////////////////////////

our code originally:

private var isQrHandled = false

This is NOT thread safe.

Because:

Camera Thread → reading variable
UI Thread → writing variable

Two threads accessing same variable → race condition.

Example Problem

Frame1 detects QR

Thread1 → isQrHandled = false
Thread2 → isQrHandled = false

Both threads think it's false.

Result:

BottomSheet opened twice
AtomicBoolean solves this
private val isQrHandled = AtomicBoolean(false)

Atomic means thread-safe operation.

The important method
isQrHandled.compareAndSet(false, true)

This means:

IF value == false
THEN set to true
ELSE do nothing

And it happens atomically (single CPU operation).

Example Flow

Frame1:

compareAndSet(false,true)
SUCCESS

Frame2:

compareAndSet(false,true)
FAIL

So only first frame triggers action.

3️⃣ Full Flow of Your Scanner (Deep Architecture)
Camera Hardware
      ↓
CameraX
      ↓
Preview → UI
      ↓
ImageAnalysis
      ↓
Executor Thread (cameraExecutor)
      ↓
QRAnalyzer
      ↓
Coroutine (lifecycleScope)
      ↓
Main Thread UI
      ↓
BottomSheet

 */



import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScannerBinding

    private lateinit var cameraExecutor: ExecutorService

    private var imageAnalysis: ImageAnalysis? = null

    private var qrAnalyzer: QRAnalyzer? = null

    // Prevent duplicate QR processing
    private val isQrHandled = AtomicBoolean(false)

    // ✅ Prevent duplicate BottomSheet (race condition fix)
    private val isSheetShowing = AtomicBoolean(false)

    private val  invalidTag = InvalidQrBottomSheet::class.java.simpleName
    private val  confirmTag = LoginConfirmBottomSheet::class.java.simpleName

    private lateinit var permissionHandler: PermissionHandler

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->

            if (granted) startCamera()
            else {
                showToast("Camera permission required")
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupEdgeToEdge()

        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyStatusBarInsets()

        cameraExecutor = Executors.newSingleThreadExecutor()

        permissionHandler = PermissionHandler(this)

        checkCameraPermission()

        binding.btnClose.setOnClickListener{

            this.finish()
        }
    }

    // ✅ 1. Edge-to-edge setup
    private fun setupEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
    }


    // ✅ 2. Insets handling (BEST PRACTICE)
    private fun applyStatusBarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.topBar) { view, insets ->

            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())

            view.updatePadding(
                top = statusBarInsets.top + 16.dp(binding.root)
            )

            insets
        }
    }

    /** Check camera permission */
    private fun checkCameraPermission() {

//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.CAMERA
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            startCamera()
//        } else {
//            permissionLauncher.launch(Manifest.permission.CAMERA)
//        }


        permissionHandler.checkAndRequestPermissions(
            type = AppPermissionManager.PermissionType.CAMERA,
            onResult = { granted ->
                if (granted)
                    startCamera()
            },
            onPermanentlyDenied = {
                permissionHandler.showPermissionDeniedDialog(it)
            }

        )
    }

    /** Initialize CameraX */
    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.previewView.surfaceProvider
            }

//            qrAnalyzer = QRAnalyzer { result ->
//                handleScanResult(result)
//            }

            //getting rectangle area from Scanner Overlay View
            val scanRect = binding.overlay.getScanRect() // 👈 from your overlay

            qrAnalyzer = QRAnalyzer(
                previewView = binding.previewView,
                scanRect = scanRect) { result ->
                handleScanResult(result)
            }


            imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis?.setAnalyzer(cameraExecutor, qrAnalyzer!!)

            try {

                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )

            } catch (e: Exception) {
                Log.e("ScannerActivity", "Camera binding failed", e)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    /** Called when QR detected */
    private fun handleScanResult(result: String) {

        if (result.isBlank()) return

        if (!isQrHandled.compareAndSet(false, true)) return

        qrAnalyzer?.pause()

        lifecycleScope.launch {

            if (result.contains("policyboss", ignoreCase = true)) {
                showValidQrSheet(result)
            } else {
                showInvalidQrSheet()
            }
        }
    }

    /** Fake API call */
    private fun demoApiCall() {

        lifecycleScope.launch {

            delay(4000)

            getConfirmSheet()?.dismiss()

            val intent = Intent().apply {
                putExtra("login_msg", "Success Data..")
            }

            setResult(RESULT_OK, intent)

            finish()
        }
    }

    /** Show valid QR confirmation sheet */
    /**
     * SUCCESS PATH: Confirm QR and notify HomeActivity
     */
    private fun showValidQrSheet(result: String) {

        // 1️⃣ UI-level protection (race condition)
        //2️⃣ FragmentManager protection

        if (isSheetShowing.get() || getConfirmSheet() != null) return

         isSheetShowing.set(true)

        val sheet = LoginConfirmBottomSheet.newInstance(
            location = "Mumbai, IN",
            ip = "49.248.9.46",
            device = result
        ).apply {

            // FIX: Make non-cancelable via touch/backpress
            isCancelable = false

            onConfirmClick = {

                demoApiCall()
            }
        }



        sheet.onCancelClick = {
           // isQrHandled.set(false)
           // qrAnalyzer?.resume()

            isSheetShowing.set(false)

            val intent = Intent().apply {
                putExtra("login_msg", "Login cancelled")
            }

            setResult(RESULT_CANCELED, intent)

            sheet.dismissAllowingStateLoss()

            finish() // ✅ EXIT scanner
        }

        sheet.show(
            supportFragmentManager,
            confirmTag
        )
    }

    /** Show invalid QR sheet */
    private fun showInvalidQrSheet() {

        // 1️⃣ Logic-level protection (fast QR spam)
        if (isSheetShowing.get()) return

        // 2️⃣ Lifecycle-level protection (rotation / restore)
        if (supportFragmentManager.findFragmentByTag(invalidTag) != null)
            return

        isSheetShowing.set(true)

//        val sheet = InvalidQrBottomSheet.newInstance()
//
//        sheet.onRetryClick = {
//            resetScannerState()
//            sheet.dismiss()
//        }

        val sheet = InvalidQrBottomSheet.newInstance().apply {

            isCancelable = false
            onRetryClick = {
                dismiss()
                resetScannerState()
//                lifecycleScope.launch {
//                    delay(600) // user moves camera
//                    resetScannerState()
//                }

            }

        }

        sheet.show(
            supportFragmentManager,
            invalidTag
        )
    }

    /** Get confirm sheet safely from FragmentManager */
    private fun getConfirmSheet(): LoginConfirmBottomSheet? {

        return supportFragmentManager
            .findFragmentByTag(confirmTag) as? LoginConfirmBottomSheet
    }

    private fun resetScannerState() {
        // 🔥 Important: clear analyzer buffer effect
        // 1️⃣ Resume camera first
        qrAnalyzer?.resume()
        isQrHandled.set(false)
        isSheetShowing.set(false)

        // 2️⃣ Small delay to let new frames come
//        lifecycleScope.launch {
//            delay(200)
//
//            // 3️⃣ Then unlock scanning
//            isQrHandled.set(false)
//            isSheetShowing.set(false)
//        }
    }

    override fun onResume() {
        super.onResume()

        if (::permissionHandler.isInitialized) {
            checkCameraPermission()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        qrAnalyzer?.shutdown()

        cameraExecutor.shutdown()
    }
}

