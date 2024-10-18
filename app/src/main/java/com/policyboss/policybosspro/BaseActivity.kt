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
import androidx.core.content.FileProvider
import com.policyboss.policybosspro.databinding.LayoutCommonWebviewPopupBinding
import com.policyboss.policybosspro.databinding.ProgressdialogLoadingBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utility.Utility
import com.policyboss.policybosspro.view.others.incomePotential.IncomePotentialActivity

import com.policyboss.policybosspro.webview.CommonWebViewActivity
import java.util.regex.Pattern
import javax.inject.Inject

import com.policyboss.policybosspro.view.syncContact.ui.WelcomeSyncContactActivityKotlin
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

open class BaseActivity() : AppCompatActivity() {

    private lateinit var dialog: Dialog
    private var dialogNoInterNet: AlertDialog? = null

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    //region progress dialog

    open fun displayLoadingWithText(
        text: String? = "Loading...",

        cancelable: Boolean? = false,
    ) { // function -- context(parent (reference))

        var loadingLayout: ProgressdialogLoadingBinding? = null
        try {
            if (!this::dialog.isInitialized) {
                dialog = Dialog(this@BaseActivity)
               dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                if (dialog.window != null) {

                    dialog.window!!.setBackgroundDrawable(ColorDrawable(0))

                }
                loadingLayout = ProgressdialogLoadingBinding.inflate(layoutInflater)
                dialog.setContentView(loadingLayout.root)
                dialog.setCancelable(cancelable ?: false)

            }

            loadingLayout?.txtMessage?.text = text


            //hide keyboard
            //view.context.hideKeyboard(view)

            dialog.let {
                if (!it.isShowing) {
                    it.show()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun hideLoading() {
        try {
            if (this::dialog.isInitialized && dialog.isShowing) {
                dialog.dismiss()
            }
        } catch (e: Exception) {
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

            setPositiveButton("OK") { dialog, whichButton ->
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


        @Inject
        lateinit var prefsManager: PolicyBossPrefsManager

        // region Raise Ticket
        @JavascriptInterface
        fun uploadDoc(randomID: String) {
            (mContext as? CommonWebViewActivity)?.galleryCamPopUp(randomID)
        }

        @JavascriptInterface
        fun uploadDocView(randomID: String) {
            (mContext as? CommonWebViewActivity)?.galleryCamPopUp(randomID)
        }

        @JavascriptInterface
        fun syncContacts() {
            // Sync contacts and navigate to WelcomeSyncContactActivity
            mContext.startActivity(Intent(mContext, WelcomeSyncContactActivityKotlin::class.java))
        }

        @JavascriptInterface
        fun syncSummary() {
            // Fetch UserConstantEntity and navigate to CommonWebViewActivity with lead dashboard URL

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
        fun callPDFCredit(url: String) {
            // Load a PDF in CommonWebViewActivity for free credit report
            val intent = Intent(mContext, CommonWebViewActivity::class.java).apply {
                putExtra("URL", url)
                putExtra("NAME", "FREE CREDIT REPORT")
                putExtra("TITLE", "LIC FREE CREDIT REPORT")
            }
            mContext.startActivity(intent)
        }

        @JavascriptInterface
        fun showCar() {
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
        fun showCV() {
            dismissWebviewDialog()
            val cvUrl = buildMotorUrl(productId = 12)
            openWebView(cvUrl, "Commercial Vehicle Insurance", "Commercial Vehicle Insurance")
        }

        @JavascriptInterface
        fun showHealth() {
            dismissWebviewDialog()
            val healthUrl = buildMotorUrlWithVersion()
            openWebView(healthUrl, "Health Insurance", "Health Insurance")
        }


        @JavascriptInterface
        fun userDefUrl(url: String, title: String) {
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