package com.policyboss.policybosspro.view.knowledgeGuru

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.policyboss.demoandroidapp.Utility.ExtensionFun.applySystemBarInsetsPadding
import com.policyboss.policybosspro.BaseActivity
import com.policyboss.policybosspro.analytics.WebEngageAnalytics
import com.policyboss.policybosspro.databinding.ActivityKnowledgeGuruBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.webengage.sdk.android.WebEngage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class KnowledgeGuruActivity : BaseActivity(), OnClickListener {

    private lateinit var binding: ActivityKnowledgeGuruBinding

    @Inject
    lateinit var prefsManager: PolicyBossPrefsManager
    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        // Opt into edge-to-edge drawing
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityKnowledgeGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyInsets()

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.apply {

            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle("Knowledge Guru")
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Call finish() to close the activity
                this@KnowledgeGuruActivity.finish()
            }
        })


        binding.includeKnowledgeGuru.loan.visibility = View.GONE
        binding.includeKnowledgeGuru.other.visibility = View.GONE
        setListener()

    }


    private fun applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            // ✅ Push AppBar below status bar
            binding.appbar.setPadding(
                binding.appbar.paddingLeft,
                statusBars.top,
                binding.appbar.paddingRight,
                binding.appbar.paddingBottom
            )

            // ✅ Push included content above nav bar
            binding.includeKnowledgeGuru.root.setPadding(
                binding.includeKnowledgeGuru.root.paddingLeft,
                binding.includeKnowledgeGuru.root.paddingTop,
                binding.includeKnowledgeGuru.root.paddingRight,
                navBars.bottom
            )

            insets
        }
    }


    private fun setListener() {

        binding.includeKnowledgeGuru.insurance.setOnClickListener(this)

    }


    override fun onStart() {
        super.onStart()
        val weAnalytics = WebEngage.get().analytics()
        weAnalytics.screenNavigated("KnowledgeGuru Screen")
    }

    private fun trackMainMenuEvent() {
        // Create event attributes
        val eventAttributes = mutableMapOf<String, Any>()
        eventAttributes["Page Viewed"] = "Personal Accident FAQ's"

        // Track the login event using WebEngageHelper
        WebEngageAnalytics.getInstance().trackEvent("Insurance Repository Viewed", eventAttributes)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Finish the activity when the Up button is pressed
                this@KnowledgeGuruActivity.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(view: View?) {
        when (view!!.getId()) {

            binding.includeKnowledgeGuru.insurance.id -> {


                trackMainMenuEvent()

                val knowledgeUrl = prefsManager.getUserConstantEntity()?.insurancerepositorylink
                val knowledgeGuruUrl = "$knowledgeUrl?app_version=${prefsManager.getAppVersion()}&device_code=${prefsManager.getDeviceID()}&ssid=&fbaid="

                startActivity(Intent(this, KnowledgeGuruWebviewActivity::class.java).apply {
                    putExtra("URL", knowledgeGuruUrl)
                    putExtra("NAME", "INSURANCE REPOSITORY")
                    putExtra("TITLE", "INSURANCE REPOSITORY")
                })
            }
        }
    }

}