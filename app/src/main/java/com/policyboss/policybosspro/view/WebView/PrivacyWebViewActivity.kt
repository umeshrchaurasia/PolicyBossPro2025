package com.policyboss.policybosspro.view.WebView

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MenuItem
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.policyboss.policybosspro.BaseActivity
import com.policyboss.policybosspro.databinding.ActivityPrivacyWebViewBinding
import com.policyboss.policybosspro.utils.NetworkUtils
import com.webengage.sdk.android.WebEngage

class PrivacyWebViewActivity : BaseActivity() {

    private lateinit var binding: ActivityPrivacyWebViewBinding
    private lateinit var countDownTimer: CountDownTimer
    private var url: String = ""
    private var name: String = ""
    private var title: String = ""
    companion object {
        var isActive: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyInsets()

        url = intent.getStringExtra("URL").orEmpty()
        name = intent.getStringExtra("NAME").orEmpty()
        title = intent.getStringExtra("TITLE").orEmpty()

        setupToolbar()

        if (NetworkUtils.isNetworkAvailable(this@PrivacyWebViewActivity)) {
            setupWebViewURL(binding.webView,url)

        } else {
            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Call finish() to close the activity
                this@PrivacyWebViewActivity.finish()
            }
        })
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = this@PrivacyWebViewActivity.title
        }
    }

    private fun applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            // Push AppBar below status bar
            binding.appbar.setPadding(
                binding.appbar.paddingLeft,
                statusBars.top,
                binding.appbar.paddingRight,
                binding.appbar.paddingBottom
            )

            // Push WebView above navigation bar
            binding.webView.setPadding(
                binding.webView.paddingLeft,
                binding.webView.paddingTop,
                binding.webView.paddingRight,
                navBars.bottom
            )

            insets
        }
    }


    override fun onStart() {
        super.onStart()
        val weAnalytics = WebEngage.get().analytics()
        weAnalytics.screenNavigated("Privacy Screen")
    }

    fun setupWebViewURL(webView: WebView, url: String) {
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

                    // TODO show you progress image
                    if (isActive)
                    {displayLoadingWithText()}
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView, url: String) {
                    // Hide progress indicator when the page finishes loading
                    // TODO: Hide your progress image
                    hideLoading()
                    super.onPageFinished(view, url)
                }

                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    url?.let {
                        if (it.endsWith(".pdf")) {
                            openPdfInViewer(it)
                            return true
                        }
                    }
                    return false
                }
            }

            // Add JavaScript interface for native Android interaction
            addJavascriptInterface(MyJavaScriptInterface(this@PrivacyWebViewActivity), "Android")

            // Load the URL (handle PDF and regular URLs)
            Log.d("URL", url)
            if (url.endsWith(".pdf")) {
                loadUrl("https://docs.google.com/viewer?url=$url")
            } else {
                loadUrl(url)
            }
        }
    }

    private fun openPdfInViewer(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
                type = "application/pdf"
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            binding.webView.loadUrl("https://docs.google.com/viewer?url=$url")
        }
    }

    override fun onResume() {
        super.onResume()
        isActive = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Finish the activity when the Up button is pressed
                this@PrivacyWebViewActivity.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}