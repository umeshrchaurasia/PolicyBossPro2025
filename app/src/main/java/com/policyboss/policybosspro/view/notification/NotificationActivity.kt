package com.policyboss.policybosspro.view.notification

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.policyboss.demoandroidapp.Utility.ExtensionFun.applySystemBarInsetsPadding
import com.policyboss.policybosspro.BaseActivity
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.response.notification.NotificationEntity
import com.policyboss.policybosspro.core.viewModel.NotificationVM.NotifyViewModel
import com.policyboss.policybosspro.databinding.ActivityNotificationBinding
import com.policyboss.policybosspro.databinding.ContentNotificationBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.utils.showSnackbar
import com.policyboss.policybosspro.webview.CommonWebViewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActivity : BaseActivity() {

    private lateinit var binding: ActivityNotificationBinding

    // Initialize contentBinding for the included layout
    private lateinit var includedBinding: ContentNotificationBinding // For the included layout

    @Inject
    lateinit var prefsManager: PolicyBossPrefsManager

    private val viewModel by viewModels<NotifyViewModel>()

   // private var NotificationLst: List<NotificationEntity> = listOf()
    private var NotificationLst: MutableList<NotificationEntity> = mutableListOf()


    private lateinit var notificationAdapter: NotificationAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityNotificationBinding.inflate(layoutInflater)
        //region Toolbar Set
        setContentView(binding.root)

       // binding.root.applySystemBarInsetsPadding()
        applyInsets()

        setSupportActionBar(binding.toolbar)

        supportActionBar!!.apply {

            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(Constant.NotifyTitle)
        }

        includedBinding = ContentNotificationBinding.bind(binding.includeNotification.root)


        //endregion

        init()

        prefsManager.setNotificationCounter(0)

        apiCall()

        //region backPress
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Call finish() to close the activity

                val resultIntent = Intent().apply {
                    putExtra("COUNT", 0)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                this@NotificationActivity.finish()
            }
        })

        //endregion
    }

    private fun applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Push AppBar below status bar
            binding.appbar.setPadding(
                binding.appbar.paddingLeft,
                systemBars.top,
                binding.appbar.paddingRight,
                binding.appbar.paddingBottom
            )

            // Push content above nav bar
            includedBinding.root.setPadding(
                includedBinding.root.paddingLeft,
                includedBinding.root.paddingTop,
                includedBinding.root.paddingRight,
                systemBars.bottom
            )

            // Always return insets to let children consume if needed
            WindowInsetsCompat.CONSUMED
        }
    }


    private fun init() {

        NotificationLst = ArrayList()

        // Initialize the adapter with an empty list
        notificationAdapter = NotificationAdapter(
            mContext = this,
            notificationList = NotificationLst, // Initialize with an empty list
            onItemClicked = ::onNotificationListener
        )

        // Use 'with' to reduce the repeated 'includedBinding'
        with(includedBinding) {


            // Set up RecyclerView for product
            rvNotify.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@NotificationActivity)
                setItemViewCacheSize(20)

                adapter = notificationAdapter // Set the adapter here
            }
        }


    }
    private fun apiCall(){


        //Mark :-- call Api for Sales Product Detail Page
        viewModel.geNotificationData()

        //Mark :- Observing Api, get Api Response
        observeResponse()
    }


    private fun updateNotifyAdapter(NotificationLst: List<NotificationEntity>) {


        notificationAdapter.updateNotifyList(NotificationLst)

    }

    fun onNotificationListener(notifyEntity : NotificationEntity){

        if (notifyEntity.notifyFlag != null && notifyEntity.web_url != null) {
            navigateViaNotification(
                notifyEntity.notifyFlag?:"",
                notifyEntity.web_url ?:"",
                notifyEntity.web_title ?:""
            )
        }
    }


    private fun navigateViaNotification(prdID: String, WebURL: String, Title: String) {
        if (WebURL.trim().isEmpty() || Title.trim().isEmpty()) {
            return
        }

        var ipaddress = "0.0.0.0"
        try {
            ipaddress = "" // Insert logic to retrieve the actual IP address if needed
        } catch (io: Exception) {
            ipaddress = "0.0.0.0"
        }

        // Constructing the URL parameters
        val append = "&ss_id=${prefsManager.getSSID()}&fba_id=${prefsManager.getFBAID()}&sub_fba_id=" +
                "&ip_address=$ipaddress&mac_address=$ipaddress" +
                "&app_version=${prefsManager.getAppVersion()}" +
                "&device_id=${prefsManager.getDeviceID()}" +
                "&product_id=$prdID&login_ssid="

        // Append the constructed parameters to WebURL
        val finalWebURL = WebURL + append

        finish()
        startActivity(Intent(this, CommonWebViewActivity::class.java).apply {
            putExtra("URL", finalWebURL)
            putExtra("NAME", Title)
            putExtra("TITLE", Title)
        })
    }


    private fun observeResponse() {



        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {

                    viewModel.NotificationDtlResponse.collect { event ->

                        event.contentIfNotHandled?.let {

                            when (it) {
                                is APIState.Empty -> {
                                    hideLoading()
                                }

                                is APIState.Failure -> {
                                    hideLoading()



                                    showSnackbar(binding.root,"No Notification  Data Available")

                                    Log.d(Constant.TAG, it.errorMessage.toString())
                                }

                                is APIState.Loading -> {
                                    displayLoadingWithText()
                                }

                                is APIState.Success -> {

                                    hideLoading()

                                    it.data?.MasterData?.let {

                                        //setupSalesMaterialAdapter(lstSalesProdEntity)

                                        NotificationLst  = it.toMutableList()

                                        updateNotifyAdapter(NotificationLst)
                                    }
                                }
                            }


                        }


                    }
                }


            }
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Finish the activity when the Up button is pressed
                val resultIntent = Intent().apply {
                    putExtra("COUNT", 0)
                }
                setResult(Activity.RESULT_OK, resultIntent)

                this@NotificationActivity.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}