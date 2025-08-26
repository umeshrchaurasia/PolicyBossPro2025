package com.policyboss.policybosspro.view.introslider

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.analytics.WebEngageAnalytics
import com.policyboss.policybosspro.databinding.ActivityEulaBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.view.login.LoginActivity
import com.policyboss.policybosspro.webview.MyWebViewClient
import com.webengage.sdk.android.WebEngage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EulaActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var binding: ActivityEulaBinding

    @Inject
    lateinit var prefManager: PolicyBossPrefsManager

    override fun onStart() {
        super.onStart()
        val weAnalytics = WebEngage.get().analytics()
        weAnalytics.screenNavigated("Eula Screen")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEulaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        applyInsets()


        setListener()
        setupWebView()
    }

    private fun setListener() {
        binding.includedEula.btnAgree.setOnClickListener(this)
        binding.includedEula.btnDisAgree.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnAgree -> {

                prefManager.setFirstTimeLaunch(false)
                startActivity(Intent(this, LoginActivity::class.java))
                trackEvent("I Agree")
            }
            R.id.btnDisAgree -> {
                trackEvent("I Disagree")
                finish()
            }
        }
    }

    private fun applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            // push whole included layout inside safe area
            binding.includedEula.root.setPadding(
                binding.includedEula.root.paddingLeft,
                statusBars.top,  // respect status bar
                binding.includedEula.root.paddingRight,
                navBars.bottom   // respect bottom gesture/nav bar
            )

            insets
        }
    }


    private fun setupWebView() {
        val settings = binding.includedEula.webView.settings
        settings.javaScriptEnabled = true
        settings.builtInZoomControls = true
        settings.useWideViewPort = false

        settings.loadsImagesAutomatically = true
        settings.lightTouchEnabled = true
        settings.domStorageEnabled = true
        settings.loadWithOverviewMode = true
        settings.supportMultipleWindows()

        // Custom WebViewClient
        val webViewClient = MyWebViewClient(this)
        binding.includedEula.webView.webViewClient = webViewClient

        // Load the EULA page from assets
        binding.includedEula.webView.loadUrl("file:///android_asset/eula.html")
    }


    private fun trackEvent(status: String) {
        val eventAttributes = mapOf("Status" to status)

        // Track the agreement event using WebEngageAnalytics
        WebEngageAnalytics.getInstance().trackEvent("Agreement Acknowledgement", eventAttributes)
    }
}