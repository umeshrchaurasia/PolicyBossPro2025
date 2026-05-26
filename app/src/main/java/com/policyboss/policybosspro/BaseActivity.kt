package com.policyboss.policybosspro

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.policyboss.policybosspro.databinding.LayoutCommonWebviewPopupBinding
import com.policyboss.policybosspro.databinding.NetworkErrorLayoutBinding
import com.policyboss.policybosspro.databinding.ProgressdialogLoadingBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utility.Utility
import com.policyboss.policybosspro.utils.networkManager.ConnectivityObserver

import com.policyboss.policybosspro.utils.networkManager.NetworkConnectivityObserver
import com.policyboss.policybosspro.view.noNetwork.NoInternetDialogFragment

import com.policyboss.policybosspro.view.others.incomePotential.IncomePotentialActivity

import com.policyboss.policybosspro.webview.CommonWebViewActivity
import java.util.regex.Pattern

import com.policyboss.policybosspro.view.syncContact.ui.WelcomeSyncContactActivityKotlin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {


    private lateinit var connectivityObserver: ConnectivityObserver
    private var progressDialog: Dialog? = null
    private var progressBinding: ProgressdialogLoadingBinding? = null

    lateinit var binding: VB

    private lateinit var webviewDialog: Dialog
    private lateinit var webviewDialogMarketing: Dialog



   // private var netWorkErrorLayoutBinding: NetworkErrorLayoutBinding? = null

   // private lateinit var connectivityObserver: ConnectivityObserver
    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    abstract fun getViewBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_base)

        // 1. Initialize binding FIRST
        binding = getViewBinding() // Initialize the generic binding

        // 2. Set the content view
        setContentView(binding.root)

        //hide keyboard whenever activity launched.
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

      // 4. Initialize Network Dialog Manager
        // NOTE: You are using ConnectivityObserver here, which is different from
        // the NetworkDialogManager we built earlier.
        // If you are using the NetworkMonitor singleton we created, use NetworkDialogManager.
        connectivityObserver = NetworkConnectivityObserver(applicationContext)

        observeNetwork()
    }


    private fun observeNetwork() {

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                connectivityObserver.observe()
                    .collectLatest { status ->

                        when (status) {

                            ConnectivityObserver.Status.Available -> {
                                hideNoInternetDialog()
                            }

                            ConnectivityObserver.Status.Lost,
                            ConnectivityObserver.Status.Unavailable -> {
                                showNoInternetDialog()
                            }

                            ConnectivityObserver.Status.Losing -> {
                                Unit
                            }
                        }
                    }
            }
        }
    }

    //region progress dialog

    open fun displayLoadingWithText(
        text: String = "Loading...",
        cancelable: Boolean = false,
    ) {

        try {

            // Initialize dialog once
            if (progressDialog == null) {

                progressDialog = Dialog(this).apply {

                    requestWindowFeature(
                        Window.FEATURE_NO_TITLE
                    )

                    window?.setBackgroundDrawable(
                        ColorDrawable(Color.TRANSPARENT)
                    )

                    progressBinding =
                        ProgressdialogLoadingBinding.inflate(
                            layoutInflater
                        )

                    setContentView(progressBinding!!.root)
                }
            }

            // Update loading text
            progressBinding?.txtMessage?.text = text

            progressDialog?.setCancelable(cancelable)

            // Show dialog safely
            if (
                progressDialog?.isShowing == false &&
                !isFinishing &&
                !isDestroyed
            ) {

                progressDialog?.show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun hideLoading() {

        try {
            if (progressDialog?.isShowing == true) {
                progressDialog?.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    //endregion

    //region ShowAlert Custom AlertDialog
    open fun showAlert(
        msg: String,
        title: String? = null,
        positiveBtn: String? = null,
        negativeBtn: String? = null,
        showNegativeButton: Boolean = false,
        onPositiveClick: (() -> Unit)? = null,

        ) {

        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogTheme)

        alertDialog.apply {


            if (title != null) {
                setTitle(title)
            }
            setMessage(msg)
            setCancelable(false)

            setPositiveButton(positiveBtn ?: "OK") { dialog, whichButton ->
                onPositiveClick?.invoke()
                dialog.dismiss()
            }


            // Set buttons only if corresponding callback is provided
            if (showNegativeButton) {
                setNegativeButton(negativeBtn ?: "Cancel") { dialog, whichButton ->
                    //onNegativeClick.invoke()
                    dialog.dismiss()
                }
            }

        }.create().show()
    }

    //endregion




    private fun showNoInternetDialog() {


        val tag = NoInternetDialogFragment.TAG

        val fragment =
            supportFragmentManager.findFragmentByTag(tag)

        if (
            fragment == null &&
            !supportFragmentManager.isStateSaved
        ) {

            NoInternetDialogFragment()
                .show(
                    supportFragmentManager,
                    tag
                )
        }
    }

    private fun hideNoInternetDialog() {

        val fragment =
            supportFragmentManager.findFragmentByTag(
                NoInternetDialogFragment.TAG
            ) as? DialogFragment

        fragment?.dismissAllowingStateLoss()

    }

    override fun onDestroy() {
        super.onDestroy()
        hideNoInternetDialog()
        hideLoading() // Ensure dialog is dismissed when activity dies
        progressDialog = null
        progressBinding = null
    }

    //region Features: Send Sms,Send Mail, Dialer
    fun sendSms(mobNumber: String) {
        try {
            val formattedNumber = mobNumber
                .replace("\\s".toRegex(), "")
                .replace("\\+".toRegex(), "")
                .replace("-".toRegex(), "")
                .replace(",".toRegex(), "")

            val smsIntent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", formattedNumber, null))
            startActivity(smsIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Invalid Number", Toast.LENGTH_SHORT).show()
        }
    }

    fun composeEmail(address: String, subject: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        startActivity(Intent.createChooser(emailIntent, "Email via..."))
    }

    fun dialNumber(mobNumber: String) {
        try {
            val formattedNumber = mobNumber
                .replace("\\s".toRegex(), "")
                .replace("\\+".toRegex(), "")
                .replace("-".toRegex(), "")
                .replace(",".toRegex(), "")

            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$formattedNumber")
            }
            startActivity(callIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Invalid Number", Toast.LENGTH_SHORT).show()
        }
    }

    //endregion




    //region Share Data as Text
    fun datashareList(context: Context, prdSubject: String, bodyMsg: String, link: String) {
        val deeplink = "$bodyMsg\n$link"
        val finalSubject = if (prdSubject.isEmpty()) "PolicyBoss Pro" else prdSubject
        val prdDetail = deeplink

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, prdDetail)
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, finalSubject)
            putExtra(Intent.EXTRA_TEXT, prdDetail)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Via"))
    }
    //endregion

    //region Share Image
    fun datashareList(context: Context, bitmap: Bitmap?, prdSubject: String, prdDetail: String) {
        var fos: OutputStream? = null
        var screenshotUri: Uri? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Scoped storage approach for Android Q and above
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, Utility.getNewFileName("Finmart_product"))
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Utility.getImageDirectoryPath())
                }

                screenshotUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = resolver.openOutputStream(screenshotUri!!)
            } else {
                // Legacy approach for older versions (below Q)
                val imagesDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + File.separator + "PolicyBossPro")
                if (!imagesDir.exists()) {
                    imagesDir.mkdir()
                }

                val file = File(imagesDir, "PolicyBossPro_product.jpg")
                fos = FileOutputStream(file)

                screenshotUri = FileProvider.getUriForFile(
                    context,
                    context.getString(R.string.file_provider_authority),
                    file
                )
            }

            // Save bitmap to the output stream
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 90, fos ?: return)
            fos?.close()

            // Share the saved image
            openNativeShare(context, screenshotUri, prdSubject, prdDetail)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // Always close the output stream
            fos?.close()
        }
    }

    private fun openNativeShare(context: Context, screenshotUri: Uri?, prdSubject: String, prdDetail: String) {
        screenshotUri?.let {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_SUBJECT, prdSubject)
                putExtra(Intent.EXTRA_TEXT, prdDetail)
                putExtra(Intent.EXTRA_STREAM, screenshotUri)
                type = "image/*"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Via"))
        }
    }

    //endregion


    //region Method for marketing web view pop-up
    fun openWebViewPopUp(view: View, url: String, isCancelable: Boolean, strHdr: String) {
        try {
            // Initialize dialog if not already
            webviewDialog = Dialog(this@BaseActivity).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                val binding = LayoutCommonWebviewPopupBinding.inflate(layoutInflater)
                setContentView(binding.root)

                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                // Set title visibility and content
                if (strHdr.trim().isEmpty()) {
                    binding.txtTitle.visibility = View.GONE
                } else {
                    binding.txtTitle.text = strHdr.uppercase()
                    binding.txtTitle.visibility = View.VISIBLE
                }

                // Configure the web view
                setupWebView(binding.webView, url)

                // Set the close button listener
                binding.ivCross.setOnClickListener {
                    dismiss()
                }

                setCancelable(isCancelable)
                setCanceledOnTouchOutside(isCancelable)

                configureDialogWindow(this)

                show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openWebViewPopUp_marketing(view: View, url: String, isCancelable: Boolean, strHdr: String) {
        try {
            // Ensure dialog is only shown if not already visible
            if (::webviewDialogMarketing.isInitialized && webviewDialogMarketing.isShowing) return

            // Initialize dialog if not already
            webviewDialogMarketing = Dialog(this@BaseActivity).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                val binding = LayoutCommonWebviewPopupBinding.inflate(layoutInflater)
                setContentView(binding.root)

                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                // Set title visibility and content
                if (strHdr.trim().isEmpty()) {
                    binding.txtTitle.visibility = View.GONE
                } else {
                    binding.txtTitle.text = strHdr.uppercase()
                    binding.txtTitle.visibility = View.VISIBLE
                }

                // Configure the web view
                setupWebView(binding.webView, url)

                // Set the close button listener
                binding.ivCross.setOnClickListener {
                    dismiss()
                }

                setCancelable(isCancelable)
                setCanceledOnTouchOutside(isCancelable)

                configureDialogWindow(this)

                show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //endregion

    // Helper function to configure dialog window properties

    //region WebView handling
    private fun configureDialogWindow(dialog: Dialog) {
        dialog.window?.let { window ->
            window.attributes = window.attributes.apply {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
            }
            window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            )
        }
    }


     fun setupWebView(webView: WebView, url: String) {
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                builtInZoomControls = true
                useWideViewPort = false
               // supportMultipleWindows() = false
                setSupportMultipleWindows(false)
                loadsImagesAutomatically = true
                lightTouchEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
            }

            // Setup WebViewClient
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    // Show progress indicator when the page starts loading
                    // TODO: Show your progress image
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView, url: String) {
                    // Hide progress indicator when the page finishes loading
                    // TODO: Hide your progress image
                    super.onPageFinished(view, url)
                }

                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    val urlToLoad = request.url.toString()
                    if (urlToLoad.endsWith(".pdf")) {
                        // Handle PDF files with Google Docs Viewer
                        loadUrl("https://docs.google.com/viewer?url=$urlToLoad")
                        return true
                    }
                    return false
                }
            }

            // Add JavaScript interface for native Android interaction
            addJavascriptInterface(MyJavaScriptInterface(this@BaseActivity), "Android")

            // Load the URL (handle PDF and regular URLs)
            Log.d("URL", url)
            if (url.endsWith(".pdf")) {
                loadUrl("https://docs.google.com/viewer?url=$url")
            } else {
                loadUrl(url)
            }
        }
    }


    inner class MyJavaScriptInterface(private val mContext: Context) {



       // lateinit var prefsManager: PolicyBossPrefsManager

        var prefsManager  = PolicyBossPrefsManager( this@BaseActivity)

        // region Raise Ticket
        @JavascriptInterface
        fun Upload_doc(randomID: String) {
            (mContext as? CommonWebViewActivity)?.galleryCamPopUp(randomID)
        }

        @JavascriptInterface
        fun Upload_doc_view(randomID: String) {
            (mContext as? CommonWebViewActivity)?.galleryCamPopUp(randomID)
        }

        @JavascriptInterface
        fun synccontacts() {
            // Sync contacts and navigate to WelcomeSyncContactActivity
            mContext.startActivity(Intent(mContext, WelcomeSyncContactActivityKotlin::class.java))
        }

        @JavascriptInterface
        fun syncsummary() {
            // Fetch UserConstantEntity and navigate to CommonWebViewActivity with lead dashboard URL
          //  prefsManager = PolicyBossPrefsManager( this@BaseActivity)
            val intent = Intent(mContext, CommonWebViewActivity::class.java).apply {
                putExtra("URL", prefsManager.getLeadDashUrl())
                putExtra("NAME", "Sync Contact Dashboard")
                putExtra("TITLE", "Sync Contact Dashboard")
            }
            mContext.startActivity(intent)
        }

        // endregion

        @JavascriptInterface
        fun incomePotential() {
            // Navigate to IncomePotentialActivity
            mContext.startActivity(Intent(mContext, IncomePotentialActivity::class.java))
        }

        @JavascriptInterface
        fun incomeCalculator() {
            // Navigate to IncomePotentialActivity
            mContext.startActivity(Intent(mContext, IncomePotentialActivity::class.java))
        }

        @JavascriptInterface
        fun processComplete() {
            // Placeholder for process complete action
        }

        @JavascriptInterface
        fun callPDF(url: String) {
            // Load a PDF in CommonWebViewActivity
            val intent = Intent(mContext, CommonWebViewActivity::class.java).apply {
                putExtra("URL", url)
                putExtra("NAME", "LIC Business")
                putExtra("TITLE", "LIC Business")
            }
            mContext.startActivity(intent)
        }

        @JavascriptInterface
        fun callPDFCREDIT(url: String) {
            // Load a PDF in CommonWebViewActivity for free credit report
            val intent = Intent(mContext, CommonWebViewActivity::class.java).apply {
                putExtra("URL", url)
                putExtra("NAME", "FREE CREDIT REPORT")
                putExtra("TITLE", "LIC FREE CREDIT REPORT")
            }
            mContext.startActivity(intent)
        }

        @JavascriptInterface
        fun showcar() {
            dismissWebviewDialog()
            val motorUrl = buildMotorUrl(productId = 1)
            openWebView(motorUrl, "Motor Insurance", "Motor Insurance")
        }

        @JavascriptInterface
        fun showTwoWheeler() {
            dismissWebviewDialog()
            val motorUrl = buildMotorUrl(productId = 10)
            openWebView(motorUrl, "Two Wheeler Insurance", "Two Wheeler Insurance")
        }

        @JavascriptInterface
        fun showtw() {
            dismissWebviewDialog()
            val motorUrl = buildMotorUrl(productId = 10)
            openWebView(motorUrl, "Two Wheeler Insurance", "Two Wheeler Insurance")
        }



        @JavascriptInterface
        fun showcv() {
            dismissWebviewDialog()
            val cvUrl = buildMotorUrl(productId = 12)
            openWebView(cvUrl, "Commercial Vehicle Insurance", "Commercial Vehicle Insurance")
        }

        @JavascriptInterface
        fun showhealth() {
            dismissWebviewDialog()
            val healthUrl = buildMotorUrlWithVersion()
            openWebView(healthUrl, "Health Insurance", "Health Insurance")
        }


        @JavascriptInterface
        fun userdefurl(url: String, title: String) {
            dismissWebviewDialog()
            openWebView(url, title, title)
        }

        private fun dismissWebviewDialog() {
            webviewDialog?.takeIf { it.isShowing }?.dismiss()
        }

        private fun buildMotorUrl(productId: Int): String {
            val motorUrl =  prefsManager.getFourWheelerUrl()
            val ipaddress = "0.0.0.0"
            // val parentSsid = (mContext as? BaseActivity)?.loadMap()?.get("Parent_POSPNo") ?: ""
            val parentSsid =  ""
            return "$motorUrl&ip_address=$ipaddress&mac_address=$ipaddress" +
                    "&app_version=policyboss-${BuildConfig.VERSION_NAME}" +
                    "&device_id=${Utility.getDeviceID(mContext)}&product_id=$productId&login_ssid=$parentSsid"
        }

        private fun buildMotorUrlWithVersion(): String {
            val healthUrl = prefsManager.getHealthurl()
            val ipaddress = "0.0.0.0"
            //val parentSsid = (mContext as? BaseActivity)?.loadMap()?.get("Parent_POSPNo") ?: ""
            val parentSsid = ""
            return "$healthUrl&ip_address=$ipaddress" +
                    "&app_version=policyboss-${Utility.getVersionName(mContext)}" +
                    "&device_id=${Utility.getDeviceID(mContext)}&login_ssid=$parentSsid"
        }

        private fun openWebView(url: String, name: String, title: String) {
            val intent = Intent(mContext, CommonWebViewActivity::class.java).apply {
                putExtra("URL", url)
                putExtra("NAME", name)
                putExtra("TITLE", title)
            }
            mContext.startActivity(intent)
        }
    }

    //endregion

}