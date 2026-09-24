package com.policyboss.policybosspro.view.syncContact.ui




import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.policyboss.demoandroidapp.Utility.ExtensionFun.applySystemBarInsetsPadding
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.analytics.FirebaseAnalyticsHelper
import com.policyboss.policybosspro.analytics.WebEngageAnalytics
import com.policyboss.policybosspro.core.RetroHelper
import com.policyboss.policybosspro.core.requestbuilder.syncContact.SaveCheckboxRequestEntity
import com.policyboss.policybosspro.core.response.horizonResponse.sync_contact_agree
import com.policyboss.policybosspro.databinding.ActivityWelcomeSyncContactKotlinBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.AppPermission.AppPermissionManager
import com.policyboss.policybosspro.utils.AppPermission.PermissionHandler
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.utils.NetworkUtils
import com.policyboss.policybosspro.view.myAccount.MyAccountActivity
import com.policyboss.policybosspro.webview.CommonWebViewActivity
import com.webengage.sdk.android.WebEngage
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.graphics.toColorInt

@AndroidEntryPoint
open class WelcomeSyncContactActivityKotlin : AppCompatActivity() , View.OnClickListener {

    lateinit var binding: ActivityWelcomeSyncContactKotlinBinding

    private lateinit var dialogAnim : Dialog
    val TAG = "HORIZONEMP"

    private lateinit var dialog: Dialog
    private lateinit var onPageChangeListener: ViewPager.OnPageChangeListener

    var current = 0

    // Slide 4 Interactive Components
    var btnchkagree: CheckBox? = null
    var btnchkcommunication_sms: CheckBox? = null
    var btnchktele_call: CheckBox? = null
    var txtprivacy: TextView? = null
    var txtterm: TextView? = null

    var txtsetting: TextView? = null

    // Main layout elements
    lateinit var viewPager: ViewPager
    lateinit var myViewPagerAdapter: MyViewPagerAdapter
    lateinit var dotsLayout: LinearLayout
    lateinit var layouts: IntArray
    lateinit var btnNext: Button
    lateinit var dot1: ImageView
    lateinit var dot2: ImageView
    lateinit var dot3: ImageView


    var isContactSync_msg = 0

    var POSPNO = ""
    var FBAID = ""

    @Inject
    lateinit var prefManager : PolicyBossPrefsManager

    @Inject
    lateinit var firebaseAnalyticsHelper: FirebaseAnalyticsHelper

    private lateinit var permissionHandler: PermissionHandler

    var perms = arrayOf(
        "android.permission.READ_CONTACTS",
        "android.permission.READ_CALL_LOG"
    )
    val READ_CONTACTS_CODE = 101

    override fun onStart() {
        super.onStart()
        val weAnalytics = WebEngage.get().analytics()
        weAnalytics.screenNavigated("Welcome Sync Contact Screen")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityWelcomeSyncContactKotlinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarInsetsPadding()

        permissionHandler = PermissionHandler(this)
        dialogAnim = Dialog(this)

        POSPNO = prefManager.getPOSPNo()
        FBAID = prefManager.getFBAID()
        viewPager = binding.viewPager

        init_widgets()
        setListener()

        myViewPagerAdapter = MyViewPagerAdapter(this@WelcomeSyncContactActivityKotlin)
        viewPager.adapter = myViewPagerAdapter

        val bundle = Bundle().apply {
            putString("ss_id", POSPNO)
        }
        firebaseAnalyticsHelper.trackScreenView(this@WelcomeSyncContactActivityKotlin.javaClass.simpleName, "sync_contacts_viewed", bundle)

        showAnimDialog("Please Wait...")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                getHorizonDetails()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    cancelAnimDialog()
                }
            }
        }


        onPageChangeListener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                current = position
                setSelectedDot(position + 1)

                if (position == 3) {
                    // Slide 4: Control enabled state strictly based on checkbox
                    binding.lyFooter.setBackgroundColor("#EBFCFF".toColorInt())
                    dotsLayout.visibility = View.GONE
                    txtsetting?.visibility = View.VISIBLE

                    btnNext.text = "GET STARTED"
                    val isChecked = btnchkagree?.isChecked == true
                    btnNext.isEnabled = isChecked
                    btnNext.alpha = if (isChecked) 1.0f else 0.4f
                    btnNext.tag = 0

                    // Auto-scroll to bottom of Slide 4 after a short delay to ensure view is fully inflated
                    viewPager.postDelayed({
                        // Find the NestedScrollView using its ID directly from the active view hierarchy
                        val scrollView = viewPager.findViewById<androidx.core.widget.NestedScrollView>(R.id.slide4ScrollView)
                        scrollView?.fullScroll(View.FOCUS_DOWN)
                    }, 150)

                } else {
                    // Slides 1, 2, 3: Button must always be ENABLED
                    binding.lyFooter.setBackgroundColor(Color.WHITE)
                    dotsLayout.visibility = View.VISIBLE
                    txtsetting?.visibility = View.GONE

                    btnNext.text = "NEXT"
                    btnNext.isEnabled = true
                    btnNext.alpha = 1.0f
                    btnNext.tag = 1
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        }

        viewPage2Listener()
    }

    private fun init_widgets() {
        dot1 = binding.dot1
        dot2 = binding.dot2
        dot3 = binding.dot3

        layouts = intArrayOf(
            R.layout.sync_welcome_slide1,
            R.layout.sync_welcome_slide2,
            R.layout.sync_welcome_slide3,
            R.layout.sync_welcome_slide4
        )

        dotsLayout = binding.layoutDots
        btnNext = binding.btnNext


        btnNext.tag = 1
        txtsetting?.visibility = View.GONE
    }

    private fun setListener() {
        btnNext.setOnClickListener(this)
        txtsetting?.setOnClickListener(this)
    }

    private fun viewPage2Listener() {
        viewPager.addOnPageChangeListener(onPageChangeListener)
    }

    private fun setSelectedDot(currentDot: Int) {
        dot1.setImageDrawable(ContextCompat.getDrawable(this@WelcomeSyncContactActivityKotlin, R.drawable.unselected_dot))
        dot2.setImageDrawable(ContextCompat.getDrawable(this@WelcomeSyncContactActivityKotlin, R.drawable.unselected_dot))
        dot3.setImageDrawable(ContextCompat.getDrawable(this@WelcomeSyncContactActivityKotlin, R.drawable.unselected_dot))

        when (currentDot) {
            1 -> dot1.setImageDrawable(ContextCompat.getDrawable(this@WelcomeSyncContactActivityKotlin, R.drawable.indicator_active))
            2 -> dot2.setImageDrawable(ContextCompat.getDrawable(this@WelcomeSyncContactActivityKotlin, R.drawable.indicator_active))
            3, 4 -> dot3.setImageDrawable(ContextCompat.getDrawable(this@WelcomeSyncContactActivityKotlin, R.drawable.indicator_active))
        }
    }

    private suspend fun getHorizonDetails() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val url = "https://horizon.policyboss.com:5443/sync_contact/get_sync_contact_agreements?ss_id=" + POSPNO +
                        "&device_code=" + prefManager.getDeviceID() + "&app_version=" + prefManager.getAppVersion() + "&fbaid=" + FBAID

                val resultRespAsync = async { RetroHelper.api.getSyncHorizonDetails(url) }
                val resultResp = resultRespAsync.await()

                if (resultResp.isSuccessful) {
                    cancelAnimDialog()
                    val responseBody = resultResp.body()

                    if (responseBody?.Status?.equals("Success", ignoreCase = true) == true) {
                        if (responseBody?.Msg is List<*>) {
                            val msgList = (responseBody.Msg as? List<*>)?.mapNotNull { item ->
                                when (item) {
                                    is Map<*, *> -> {
                                        try {
                                            sync_contact_agree(
                                                ss_id = (item["ss_id"] as? Number)?.toInt() ?: 0,
                                                is_sms = item["is_sms"] as? String ?: "",
                                                is_call = item["is_call"] as? String ?: "",
                                                fba_id = (item["fba_id"] as? Number)?.toInt() ?: 0,
                                            )
                                        } catch (e: Exception) {
                                            null
                                        }
                                    }
                                    else -> null
                                }
                            } ?: emptyList()

                            isContactSync_msg = msgList.size

                            if (msgList.isNotEmpty()) {
                                withContext(Dispatchers.Main) {
                                    val lastItem = msgList.lastOrNull()
                                    val is_call = lastItem?.is_call ?: ""
                                    val is_sms = lastItem?.is_sms ?: ""

                                    btnchktele_call?.isChecked = (is_call == "yes")
                                    btnchkcommunication_sms?.isChecked = (is_sms == "yes")

                                    if (!checkPermission()) {
                                        requestPermission()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        cancelAnimDialog()
                    }
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        val read_contact = ActivityCompat.checkSelfPermission(this@WelcomeSyncContactActivityKotlin, perms[0])
        val read_call_log = ActivityCompat.checkSelfPermission(this@WelcomeSyncContactActivityKotlin, perms[1])
        return (read_contact == PackageManager.PERMISSION_GRANTED) && (read_call_log == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this@WelcomeSyncContactActivityKotlin, perms, READ_CONTACTS_CODE)
    }

    fun showAnimDialog(msg: String? = "") {
        try {
            if (!this::dialog.isInitialized) {
                dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.progressdialog2_loading)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setCancelable(false)
            }
            val txtMessage = dialog.findViewById<TextView>(R.id.txtMessage)
            txtMessage.text = msg

            dialog.let {
                if (!it.isShowing) {
                    it.show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelAnimDialog() {
        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.dismiss()
        }
    }

    suspend fun savecheckboxdetails() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val url = "https://horizon.policyboss.com:5443/postservicecall/sync_contacts/online_agreement"

                val smschk = if (btnchkcommunication_sms?.isChecked == true) "yes" else "no"
                val telechk = if (btnchktele_call?.isChecked == true) "yes" else "no"

                val saveCheckboxRequestEntity = SaveCheckboxRequestEntity(
                    fba_id = Integer.parseInt(FBAID),
                    is_sms = smschk,
                    is_call = telechk,
                    online_agreement = "online_agreement",
                    ss_id = Integer.parseInt(POSPNO),
                    app_version = prefManager.getAppVersion(),
                    device_code = prefManager.getDeviceID()
                )

                val resultRespAsync = async { RetroHelper.api.savecheckboxdetails(url, saveCheckboxRequestEntity) }
                resultRespAsync.await()
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            btnNext.id -> {
                if (NetworkUtils.isNetworkAvailable(this@WelcomeSyncContactActivityKotlin)) {
                    current += 1
                    if (current < layouts.size) {
                        viewPager.currentItem = current
                    } else {
                        requestContactPermissionsAndProceed()
                    }
                } else {
                    Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_SHORT).show()
                }
            }

            txtprivacy?.id -> {
                trackSyncContactEvent("Read Privacy Policy for Sync Contacts")
                startActivity(
                    Intent(this, CommonWebViewActivity::class.java)
                        .putExtra(
                            "URL",
                            "https://www.policyboss.com//privacy-policy-policyboss-pro?app_version=" + prefManager.getAppVersion() + "&device_code=" + prefManager.getDeviceID() + "&ssid=" + POSPNO + "&fbaid=" + FBAID
                        )
                        .putExtra("NAME", "privacy-policy")
                        .putExtra("TITLE", "privacy-policy")
                )
            }

            txtterm?.id -> {
                trackSyncContactEvent("T&C Viewed for Sync Contacts")
                startActivity(
                    Intent(this, CommonWebViewActivity::class.java)
                        .putExtra(
                            "URL",
                            "https://www.policyboss.com/terms-condition?app_version=" + prefManager.getAppVersion() + "&device_code=" + prefManager.getDeviceID() + "&ssid=" + POSPNO + "&fbaid=" + FBAID
                        )
                        .putExtra("NAME", "Terms & Conditions")
                        .putExtra("TITLE", "Terms & Conditions")
                )
            }

            txtsetting?.id -> {
                trackSyncContactEvent("Sync Contacts Setting")
                startActivity(Intent(this, MyAccountActivity::class.java))
            }
        }
    }

    private fun requestContactPermissionsAndProceed() {
        permissionHandler.checkAndRequestPermissions(
            AppPermissionManager.PermissionType.CONTACTS_AND_CALL_LOG,
            onResult = { isGranted ->
                if (isGranted) {
                    proceedToSync()
                } else {
                    Snackbar.make(binding.root, "Permissions are required to sync contacts.", Snackbar.LENGTH_LONG).show()
                }
            },
            onPermanentlyDenied = { permanentlyDeniedList ->
                permissionHandler.showPermissionDeniedDialog(
                    permanentlyDeniedList,
                    "Contacts and Call Log permissions are required to sync your leads. Please enable them in Settings."
                )
            }
        )
    }

    private fun proceedToSync() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                savecheckboxdetails()
            } catch (e: Exception) {
                Log.e(TAG, "Save error: ${e.message}")
            }
        }

        trackSyncContactEvent("Get Started on Sync Contacts")
        startActivity(Intent(this, SyncContactActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::onPageChangeListener.isInitialized) {
            viewPager.removeOnPageChangeListener(onPageChangeListener)
        }
    }

    inner class MyViewPagerAdapter(private val context: Context) : PagerAdapter() {

        private val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = layoutInflater.inflate(layouts[position], container, false)

            if (position == 3) {
                btnchkagree = view.findViewById(R.id.chkagree)
                btnchkcommunication_sms = view.findViewById(R.id.chkcommunication_sms)
                btnchktele_call = view.findViewById(R.id.chktele_call)
                txtterm = view.findViewById(R.id.txtterm)
                txtprivacy = view.findViewById(R.id.txtprivacy)
                txtsetting = view.findViewById(R.id.txtsetting)
                val tvClickHere = view.findViewById<TextView>(R.id.tvClickHere)

                txtterm?.setOnClickListener(this@WelcomeSyncContactActivityKotlin)
                txtprivacy?.setOnClickListener(this@WelcomeSyncContactActivityKotlin)
                txtsetting?.setOnClickListener(this@WelcomeSyncContactActivityKotlin)

                tvClickHere?.setOnClickListener {
                    trackSyncContactEvent("Sync Contacts Details Viewed")
                }

                // ONLY set the listener to update button state dynamically when checked/unchecked
                btnchkagree?.setOnCheckedChangeListener { _, isChecked ->
                    // Guard it with viewPager.currentItem to make sure it only affects btnNext if user is actually viewing slide 4
                    if (viewPager.currentItem == 3) {
                        btnNext.isEnabled = isChecked
                        btnNext.alpha = if (isChecked) 1.0f else 0.4f
                    }
                }
            }

            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return layouts.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            val view = obj as View
            container.removeView(view)
        }
    }

    private fun trackSyncContactEvent(strEvent: String) {
        val eventAttributes: Map<String, Any> = HashMap()
        WebEngageAnalytics.getInstance().trackEvent(strEvent, eventAttributes)
    }
}