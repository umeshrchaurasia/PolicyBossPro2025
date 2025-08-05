package com.policyboss.policybosspro.view.knowledgeGuru

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowCompat
import com.policyboss.demoandroidapp.Utility.ExtensionFun.applySystemBarInsetsPadding
import com.policyboss.policybosspro.BaseActivity
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.databinding.ActivityKnowledgeGuruBinding
import com.policyboss.policybosspro.databinding.ActivityKnowledgeGuruWebviewBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.utils.NetworkUtils
import com.policyboss.policybosspro.utils.showAlert
import com.policyboss.policybosspro.view.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class KnowledgeGuruWebviewActivity : BaseActivity() {

    private lateinit var binding: ActivityKnowledgeGuruWebviewBinding
    private lateinit var backPressedCallback: OnBackPressedCallback

    @Inject
    lateinit var prefsManager: PolicyBossPrefsManager

    lateinit var url : String
    lateinit var name : String
    lateinit var title : String


    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Opt into edge-to-edge drawing
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityKnowledgeGuruWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.applySystemBarInsetsPadding()

        setSupportActionBar(binding.toolbar)

        // Extract URL, NAME, TITLE from intent
         url = intent.getStringExtra("URL").toString()
         name = intent.getStringExtra("NAME").toString()
         title = intent.getStringExtra("TITLE").toString()

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            this.title = title
        }

        // Handle back button
        binding.includeKnowledgeGuruWebview.btnBack.setOnClickListener {
            finish()
        }

        // Floating action button click listener
        binding.fab.setOnClickListener {
            url?.let { downloadPdf(it, name ?: "") }
        }

        // Check network connection
        if (NetworkUtils.isNetworkAvailable(this@KnowledgeGuruWebviewActivity)) {
            settingWebview()
            startCountDownTimer()
        } else {

            showAlert("Check your internet connection")
        }


        setupBackNavigation()
    }



    //region WebView Handling
    private fun settingWebview() {
        binding.includeKnowledgeGuruWebview.webView.apply {
            settings.apply {
                javaScriptEnabled = true
                builtInZoomControls = true
                displayZoomControls = false // Hide the zoom controls
                useWideViewPort = true // Enable responsive layouts
                loadWithOverviewMode = true
                domStorageEnabled = true
                // Disable features that might pose security risks
                allowFileAccess = false
                allowContentAccess = false
                allowFileAccessFromFileURLs = false
                allowUniversalAccessFromFileURLs = false
                // Enable caching
                cacheMode = WebSettings.LOAD_DEFAULT
            }

            // Enable hardware acceleration
            setLayerType(View.LAYER_TYPE_HARDWARE, null)

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    displayLoadingWithText()
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView, url: String) {
                    hideLoading()

                    super.onPageFinished(view, url)
                }

                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    return handleUrl(url)
                }


                // For newer Android versions
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    return handleUrl(request.url.toString())
                }
            }

            // Handle SSL errors
            webChromeClient = object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    // Update activity title with web page title {No need}
                  //  supportActionBar?.title = title
                }
            }
        }

        // Log the URL (consider removing in production)
        Log.d("URL", url)

        // Load the URL
        binding.includeKnowledgeGuruWebview.webView.loadUrl(url)
    }

    private fun handleUrl(url: String): Boolean {
        return when {
            url.endsWith(".pdf") -> {
                try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(Uri.parse(url), "application/pdf")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    // Fallback to Google Docs viewer
                    binding.includeKnowledgeGuruWebview.webView.loadUrl("https://docs.google.com/viewer?url=$url")
                }
                true
            }
            url.startsWith("tel:") -> {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(url)))
                true
            }
            url.startsWith("mailto:") -> {
                startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse(url)))
                true
            }
            else -> false
        }
    }

    private fun downloadPdf(url: String, name: String) {

        showAlert("\"Download started..")

        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$name.pdf")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setMimeType(MimeTypeMap.getFileExtensionFromUrl(url))
        }

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

     //endregion

    private fun startCountDownTimer() {
        countDownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // You can update UI here if needed
            }

            override fun onFinish() {
                try {
                   hideLoading()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    private fun setupBackNavigation() {
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.includeKnowledgeGuruWebview.webView.canGoBack()) {
                    binding.includeKnowledgeGuruWebview.webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_home -> {
                val intent = Intent(this, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("MarkTYPE", "FROM_HOME")
                }
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        backPressedCallback.remove()
        countDownTimer?.cancel()
    }


}