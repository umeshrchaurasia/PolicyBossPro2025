package com.policyboss.policybosspro.view.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.policyboss.policybosspro.BaseActivity
import com.policyboss.policybosspro.BuildConfig
import com.policyboss.policybosspro.PolicyBossProApplication
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.analytics.WebEngageAnalytics
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.model.homeDashboard.DashboardMultiLangEntity
import com.policyboss.policybosspro.core.model.sysncContact.SyncContactEntity
import com.policyboss.policybosspro.core.viewModel.homeVM.HomeViewModel
import com.policyboss.policybosspro.databinding.ActivityHomeBinding
import com.policyboss.policybosspro.databinding.LayoutMysyncPopupBinding
import com.policyboss.policybosspro.databinding.LayoutSharePopupBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utility.Utility
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.utils.NetworkUtils
import com.policyboss.policybosspro.utils.hideKeyboard
import com.policyboss.policybosspro.utils.showSnackbar
import com.policyboss.policybosspro.view.changePwd.ChangePaswordActivity
import com.policyboss.policybosspro.view.home.adapter.DashboardRowAdapter
import com.policyboss.policybosspro.view.syncContact.ui.WelcomeSyncContactActivityKotlin

import com.policyboss.policybosspro.webview.CommonWebViewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    //region Declare variables
    private lateinit var binding: ActivityHomeBinding
    private lateinit var dashboardAdapter: DashboardRowAdapter
    private lateinit var shareProdDialog: AlertDialog
    private var mySyncPopUpAlert: AlertDialog? = null

    private var isSwipeRefresh = false

    private val viewModel by viewModels<HomeViewModel>()
    @Inject
    lateinit var prefsManager: PolicyBossPrefsManager


    @Inject
   lateinit var myApplication: PolicyBossProApplication


    private lateinit var toggle: ActionBarDrawerToggle

    //endregion


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize views
        // region Set the toolbar as ActionBar
        setSupportActionBar(binding.toolbar)

        // Initialize ActionBarDrawerToggle
        toggle = ActionBarDrawerToggle(
            this, binding.drawer, binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

       // myApplication = PolicyBossProApplication.instance!!
        // Attach the toggle to the drawer
        binding.drawer.addDrawerListener(toggle)
        toggle.syncState()

        binding.swipeRefreshLayout.isEnabled = false
        //endregion


        //region Handle OnBackPressed()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Check if the drawer is open
                if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                    // Close the drawer if it's open
                    binding.drawer.closeDrawer(GravityCompat.START)
                } else {
                    // If drawer is not open, allow the system to handle the back press
                   // isEnabled = false // Disable this callback
                   // onBackPressedDispatcher.onBackPressed() // Call the default back press

                   showAlert(msg="Do you really want to exit?",title = "Exit App",positiveBtn = resources.getString(R.string.yes) , showNegativeButton = true,
                       onPositiveClick = {
                           this@HomeActivity.finish()
                       })
                }
            }
        })
        //endregion

        observeMasterState()

        //Called Master Data ie UserConstant and Dynamic Dashb oard Parallel
        viewModel.getMasterData()


        //region Swipe To Regresh not in used
//        binding.swipeRefreshLayout.setColorSchemeResources(
//            R.color.white // Progress spinner (circle) color
//        )
//
//        // Set the background color of the progress circle to blue
//        binding.swipeRefreshLayout.setProgressBackgroundColorSchemeResource(
//            R.color.colorPrimary // Background color of the progress circle
//        )
//        binding.swipeRefreshLayout.setOnRefreshListener {
//            // Trigger API refresh only on user swipe
//            isSwipeRefresh = true
//            vewModel.getMasterData()
//        }

        //endregion

        //region Set up navigation item click listener
        binding.navigationView.setNavigationItemSelectedListener {  menuItem ->

            if (!NetworkUtils.isNetworkAvailable(this@HomeActivity)) {

                this.showSnackbar(binding.root,getString(R.string.noInternet))

                return@setNavigationItemSelectedListener false
            }
            // Toggle checked state of the menu item
            menuItem.isChecked = !menuItem.isChecked



            hideKeyboard(binding.root)


            //region Add Dynmaic Drawer Menu
            if (prefsManager.getMenuDashBoard() != null) {
                prefsManager.getMenuDashBoard()?.MasterData?.Menu?.forEach { menuItemEntity ->
                    var sequence = menuItemEntity.sequence?.toInt()
                    if (sequence != null) {
                        sequence = (sequence * 100) + 1
                    }
                    if (menuItem.itemId == sequence) {

                        val appendMenu = "&ss_id=${prefsManager.getSSID()}" +
                                "&fba_id=${prefsManager.getFBAID()}" +
                                "&sub_fba_id=" +
                                "ip_address=&mac_address=" +
                                "&app_version=policyboss-${BuildConfig.VERSION_NAME}" +
                                "&device_id=${Utility.getDeviceID(this@HomeActivity)}" +
                                "&login_ssid="

                        val menuDetail = "${menuItemEntity.link}$appendMenu"

                        startActivity(
                            Intent(this@HomeActivity, CommonWebViewActivity::class.java).apply {
                                putExtra("URL", menuDetail)
                                putExtra("NAME", menuItemEntity.menuname)
                                putExtra("TITLE", menuItemEntity.menuname)
                            }
                        )
                        return@setNavigationItemSelectedListener true
                    }


                }
            }

            //endregion


            when (menuItem.itemId) {
                R.id.nav_finbox -> {
                    // Handle nav item 1 click
                }
                R.id.nav_AppointmentLetter -> {
                    // Handle nav item 2 click
                }

                R.id.nav_changepassword -> {

                    startActivity(Intent(this@HomeActivity, ChangePaswordActivity::class.java))
                }

                R.id.nav_myaccount -> {
                    //05 temp My Account
                    startActivity(Intent(this@HomeActivity, ChangePaswordActivity::class.java))
                }

                R.id.nav_pospenrollment ->{

                    val intent = Intent(this@HomeActivity, CommonWebViewActivity::class.java).apply {
                        putExtra("URL", prefsManager.getEnableProPOSPurl() +
                                "&app_version=" + prefsManager.getAppVersion() +
                                "&device_code=" + Utility.getDeviceID(this@HomeActivity) +
                                "&ssid=" + prefsManager.getSSID() +
                                "&fbaid=" + prefsManager.getFBAID())
                        putExtra("NAME", "PospEnrollment")
                        putExtra("TITLE", "Posp Enrollment")
                    }
                    startActivity(intent)

                }


                R.id.nav_leaddetail -> {

                    var leaddetail = ""
                    val append_lead = "&ip_address=&mac_address=&app_version=policyboss-" + BuildConfig.VERSION_NAME +
                            "&device_id=" + Utility.getDeviceID(this@HomeActivity) + "&login_ssid="
                    leaddetail = prefsManager.getLeadDashUrl() + append_lead

                    val intent = Intent(this@HomeActivity, CommonWebViewActivity::class.java).apply {
                        putExtra("URL", leaddetail)
                        putExtra("NAME", "Sync Contact DashBoard")
                        putExtra("TITLE", "Sync Contact DashBoard")
                    }
                    startActivity(intent)


                }

                R.id.nav_whatsnew -> {
                    //05 temp My Account
                    startActivity(Intent(this@HomeActivity, ChangePaswordActivity::class.java))
                }

                R.id.nav_raiseTicket -> {
                    val intent = Intent(this@HomeActivity, CommonWebViewActivity::class.java).apply {
                        putExtra("URL", prefsManager.getRaiseTickitUrl() + "&mobile_no=" + prefsManager.getMobileNo() +
                                "&UDID=" + prefsManager.getUserId() + "&app_version=" + prefsManager.getAppVersion() +
                                "&device_code=" + Utility.getDeviceID(this@HomeActivity) + "&ssid=" + prefsManager.getSSID() +
                                "&fbaid=" + prefsManager.getFBAID())
                        putExtra("NAME", "RAISE_TICKET")
                        putExtra("TITLE", "RAISE TICKET")
                    }
                    startActivity(intent)

                }


                // Add more cases as needed
            }
            // Close the drawer after item selection
            binding.drawer.closeDrawer(GravityCompat.START)
            true
        }
        //endregion


    }


    //region SetUpDashboard
    private fun setupDashBoardData() {


        dashboardAdapter = DashboardRowAdapter(
            mContext = this,
            insurancePosition = 0,
            disclosurePosition = 1,
            listIns =  viewModel.getInsurProductLangList(),
            prefsManager = prefsManager,
            onShareClick = ::onShareListener,
            onInfoClick = ::onInfoListener,
            onDashBoardClick = ::onDashBoardListener

            )
        binding.rvHome.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = dashboardAdapter
            setHasFixedSize(true)
            setItemViewCacheSize(20)
        }
    }

    //endregion

    private fun setNavigationMenu(language: String) {

        val menu = binding.navigationView.menu

        // Create a list of MenuItem pairs for better handling
        val menuItems = mapOf(
            "MenuHome" to menu.findItem(R.id.nav_home),
            "Switch Language" to menu.findItem(R.id.nav_language),
            "MenuMyFinbox" to menu.findItem(R.id.nav_finbox),
            "MenuFinperks" to menu.findItem(R.id.nav_finperk),
            "FESTIVE LINKS" to menu.findItem(R.id.nav_festivelink),
            "Finmart Business Contact" to menu.findItem(R.id.nav_insert_contact),
            "MenuMyAccount" to menu.findItem(R.id.nav_myaccount_pro),
            "MenuMyProfile" to menu.findItem(R.id.nav_myaccount),
            "Enrol as POSP" to menu.findItem(R.id.nav_pospenrollment),
            "" to menu.findItem(R.id.nav_addposp),
            "MenuRaiseTicket" to menu.findItem(R.id.nav_raiseTicket),
            "MenuChangePwd" to menu.findItem(R.id.nav_changepassword),

            "MenuMyDocs" to menu.findItem(R.id.nav_Doc),

            "MenuPospAppLtr" to menu.findItem(R.id.nav_AppointmentLetter),
            "MenuPospAppForm" to menu.findItem(R.id.nav_Certificate),
            "MenuMyTranTitle" to menu.findItem(R.id.nav_TRANSACTIONS),
            "MenuInsBus" to menu.findItem(R.id.nav_mybusiness_insurance),
            "MenuMyTransItm" to menu.findItem(R.id.nav_transactionhistory),
            "MenuMyMsgs" to menu.findItem(R.id.nav_MessageCentre),
            "MenuPolicyByCRN" to menu.findItem(R.id.nav_crnpolicy),
            "MenuMyLeads" to menu.findItem(R.id.nav_LEADS),
            "MenuLeadfromCont" to menu.findItem(R.id.nav_contact),
            "MenuMotLeads" to menu.findItem(R.id.nav_generateLead),
            "" to menu.findItem(R.id.nav_scan_vehicle),

            "MenuLeadDash" to menu.findItem(R.id.nav_leaddetail),
            "MenuSms" to menu.findItem(R.id.nav_sendSmsTemplate),

            "MenuMorServ" to menu.findItem(R.id.nav_REQUEST),
            "MenuMorServ" to menu.findItem(R.id.nav_QA),
            "MenuUtil" to menu.findItem(R.id.nav_MYUtilities),
            "Menuwtsnew" to menu.findItem(R.id.nav_whatsnew),
            "" to menu.findItem(R.id.nav_cobrowser),
            "MenuLogOut" to menu.findItem(R.id.nav_logout)
        )

        // Set titles and fonts for menu items based on the selected language
        if (language.isNotEmpty()) {
//            for ((key, item) in menuItems) {
//                val title = db.getLangData(language, key)
//                if (title.isNotEmpty()) {
//                    item.title = title
//                    setLanguageFont(this, language, item)
//                }
//            }
        }
    }


    //region Adapter Callback
    fun onShareListener(entity: DashboardMultiLangEntity){
        viewModel.setCurrentDashboardShareEntity(entity)
        shareProductPopUp(shareEntity = entity)

    }
    fun onInfoListener(entity: DashboardMultiLangEntity){

        openWebViewPopUp(binding.root, entity.info, true, "")
    }
    fun onDashBoardListener(entity: DashboardMultiLangEntity){

        switchDashBoardMenus(dashboardEntity = entity)
    }

    //endregion

    // region Dashboard Menu

    private fun switchDashBoardMenus(dashboardEntity: DashboardMultiLangEntity?) {

        dashboardEntity?.let { entity ->
            try {

                var ipaddress = "0.0.0.0"
                val parent_ssid = ""
                val deviceId = Utility.getDeviceID(this@HomeActivity)
                val appVersion = "policyboss-${BuildConfig.VERSION_NAME}"

                when (entity.productId) {
                    1 -> {
                        // Car
                        var motorUrl = prefsManager.getFourWheelerUrl()

                        //region comment

//                        val append = "&ip_address=$ipaddress&mac_address=$ipaddress" +
//                                "&app_version=policyboss-${BuildConfig.VERSION_NAME}" +
//                                "&device_id=${Utility.getDeviceID(this@HomeActivity)}" +
//                                "&product_id=1&login_ssid=$parent_ssid"
                        // motorUrl += append

                        //                        startActivity(
//                            Intent(this@HomeActivity, CommonWebViewActivity::class.java)
//                                .putExtra("URL", motorUrl)
//                                .putExtra("dashBoardtype", "INSURANCE")
//                                .putExtra("NAME", "Motor Insurance")
//                                .putExtra("TITLE", "Motor Insurance")
//                        )

                        //endregion

                        motorUrl += buildUrlAppend(ipaddress, deviceId, appVersion, entity.productId, parent_ssid)

                        openCommonWebView(
                            motorUrl,
                            "Motor Insurance",
                            "Motor Insurance",
                            Constant.INSURANCE_TYPE
                        )



                        trackMainMenuEvent("Motor Insurance")
//                        myApplication.trackEvent(
//                            Constant.PRIVATE_CAR,
//                            "Clicked",
//                            "Motor insurance tab on home page"
//                        )
                    }

                    23 -> {

                        // Kotak
                        var kotakUrl = prefsManager.getUserConstantResponse()?.MasterData?.EliteKotakUrl ?: ""
                        kotakUrl += buildUrlAppend(ipaddress, deviceId, appVersion, entity.productId, parent_ssid)

                        openCommonWebView(
                            kotakUrl,
                            "Kotak Group health Care",
                            "Kotak Group health Care",
                            Constant.INSURANCE_TYPE
                        )

                        trackMainMenuEvent("Kotak Group health Care")
//                        myApplication.trackEvent(
//                            Constant.PRIVATE_CAR,
//                            "Clicked",
//                            "Kotak Group health Care tab on home page"
//                        )

                    }

                    2 ->  {

                        // health
                        var healthUrl = prefsManager.getHealthurl()
                        healthUrl += buildUrlAppend(ipaddress, deviceId, appVersion, entity.productId, parent_ssid)

                        openCommonWebView(
                            healthUrl,
                            "Health Insurance",
                            "Health Insurance",
                            Constant.INSURANCE_TYPE
                        )

                        trackMainMenuEvent("Health Insurance")
//                        myApplication.trackEvent(
//                            Constant.HEALTH_INS,
//                            "Clicked",
//                            "Health insurance tab on home page"
//                        )

                    }

                    10 -> {

                        //bike
                        var bikeUrl = prefsManager.getTwoWheelerUrl()
                        bikeUrl += buildUrlAppend(ipaddress, deviceId, appVersion, entity.productId, parent_ssid)

                        openCommonWebView(
                            bikeUrl,
                            "Two Wheeler Insurance",
                            "Two Wheeler Insurance",
                            Constant.INSURANCE_TYPE
                        )

                        trackMainMenuEvent("Two Wheeler Insurance")
//                        myApplication.trackEvent(
//                            Constant.TWO_WHEELER,
//                            "Clicked",
//                            "Two Wheeler tab on home page"
//                        )

                    }

                    12 -> {

                        //bike
                        var cvUrl = prefsManager.getCVUrl()
                        cvUrl += buildUrlAppend(ipaddress, deviceId, appVersion, entity.productId, parent_ssid)

                        openCommonWebView(
                            cvUrl,
                            "Commercial Vehicle Insurance",
                            "Commercial Vehicle Insurance",
                            Constant.INSURANCE_TYPE
                        )

                        trackMainMenuEvent("Commercial Vehicle Insurance")
//                        myApplication.trackEvent(
//                            Constant.CV,
//                            "Clicked",
//                            "Health CheckUp tab on home page"
//                        )

                    }

                    5 -> {

                        //INVESTMENT PLANS
                        if(prefsManager.getInvestmentEnabled() == "1" ){

                            var  invUrl = prefsManager.getInvestmentUrl()

                            val append = "&ip_address=$ipaddress" +
                                    appVersion +
                                    "&device_id=${Utility.getDeviceID(this@HomeActivity)}" +
                                    "&login_ssid=$parent_ssid"
                            invUrl = invUrl + append

                            openCommonWebView(
                                invUrl,
                                "INVESTMENT PLANS",
                                "INVESTMENT PLANS",
                                Constant.INSURANCE_TYPE
                            )

                            trackMainMenuEvent("INVESTMENT PLANS")
//                            myApplication.trackEvent(
//                                "INVESTMENT PLANS",
//                                "Clicked",
//                                "INVESTMENT PLANS"
//                            )

                        }


                    }

                    41 -> {
                        //Synch Contact
                        startActivity(
                            Intent(
                                this@HomeActivity,
                                WelcomeSyncContactActivityKotlin::class.java
                            )
                        )
                        trackMainMenuEvent("Sync Contact")
//                        myApplication.trackEvent(
//                            Constant.SyncContacts,
//                            "Clicked",
//                            "Sync Contact"
//                        )
                    }


                    else ->{

                        if (entity.productId < 100 && entity.productId != 41) {
                            entity.IsNewprdClickable?.let { clickable ->
                                if (clickable == "Y") {
                                    // Fetch dynamic product URL
                                    var dynamicUrl = ""
                                    val userConstantsData = prefsManager.getDashboardarray()
                                    for (dashboardItem in userConstantsData) {
                                        if (dashboardItem.ProdId?.toInt() == entity.productId) {
                                            dynamicUrl = dashboardItem.url ?: ""
                                            break
                                        }
                                    }

                                    if (dynamicUrl.isNotEmpty()) {
                                        ipaddress = try {
                                            "" // Add logic for fetching ip address here
                                        } catch (e: Exception) {
                                            "0.0.0.0"
                                        }

                                        trackMainMenuEvent(entity.productName)

                                        val append = "&ip_address=$ipaddress&mac_address=$ipaddress" +
                                                "&app_version=$appVersion&device_id=$deviceId&product_id=${entity.productId}&login_ssid=$parent_ssid"
                                        dynamicUrl += append


                                        openCommonWebView(
                                            dynamicUrl,
                                            entity.productName,
                                            entity.productName,
                                            Constant.INSURANCE_TYPE
                                        )
                                    }
                                }
                            }
                        }
                        else if (entity.productId >= 100) {
                            trackMainMenuEvent(entity.productName)

                            openCommonWebView(
                                entity.link ?: "",
                                entity.productName,
                                entity.productName,
                                Constant.INSURANCE_TYPE
                            )
                        }
                    }


                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun buildUrlAppend(ipaddress: String, deviceId: String, appVersion: String, productId: Int, parentSsid: String): String {
        return "&ip_address=$ipaddress&mac_address=$ipaddress" +
                "&app_version=$appVersion" +
                "&device_id=$deviceId" +
                "&product_id=$productId&login_ssid=$parentSsid"
    }

    // Helper method to open CommonWebViewActivity
    private fun openCommonWebView(url: String, name: String, title: String, dashboardType: String) {
        startActivity(
            Intent(this@HomeActivity, CommonWebViewActivity::class.java).apply {
                putExtra("URL", url)
                putExtra("dashBoardtype", dashboardType)
                putExtra("NAME", name)
                putExtra("TITLE", title)
            }
        )
    }


    //endregion

    // region Share Product and Forece Sync Dialog

    fun shareProductPopUp(shareEntity: DashboardMultiLangEntity) {
        if (this::shareProdDialog.isInitialized && shareProdDialog.isShowing) {
            return
        }

        // Initialize the binding for the custom layout
        val binding = LayoutSharePopupBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(this@HomeActivity)
        builder.setView(binding.root)

        shareProdDialog = builder.create()

        // Set values using view binding
        binding.txtTitle.text = shareEntity.title
        binding.txtMessage.text = shareEntity.popupmsg

        // Set up share button click listener
        binding.btnShare.setOnClickListener {

           viewModel.getProductShareURL(shareEntity.productId.toString(),"0")
            shareProdDialog.dismiss()
        }

        // Set up close button (cross icon) click listener
        binding.ivCross.setOnClickListener {
            shareProdDialog.dismiss()
        }

        // Show the dialog
        shareProdDialog.setCancelable(true)
        shareProdDialog.show()
    }

    fun showMySyncPopUpAlert(syncContactEntity: SyncContactEntity) {
        try {
            if (mySyncPopUpAlert != null && mySyncPopUpAlert?.isShowing == true) {
                return
            }

            val builder = AlertDialog.Builder(this, R.style.CustomDialog)

            // ViewBinding usage
            val binding = LayoutMysyncPopupBinding.inflate(layoutInflater)
            builder.setView(binding.root)
            mySyncPopUpAlert = builder.create()

            // Extract data from syncContactEntity
            val actionNeeded = syncContactEntity.ACTION_NEEDED
            val firstSyncCampaignCreative = syncContactEntity.FIRST_SYNC_CAMPAIGN_CREATIVE
            val reSyncCampaignCreative = syncContactEntity.RE_SYNC_CAMPAIGN_CREATIVE

            var url = ""
            var titleText = ""

            // Set data based on actionNeeded
            if (actionNeeded == "RE_SYNC") {
                titleText = "Update Resync Contacts!!"
                url = "$reSyncCampaignCreative?${(Math.random() * 1000).roundToInt()}"
                binding.btnAllow.text = "Go To Resync Contacts"
            } else {
                titleText = "Update Sync Contacts!!"
                url = "$firstSyncCampaignCreative?${(Math.random() * 1000).roundToInt()}"
                binding.btnAllow.text = "Go To Sync Contacts"
            }

            // Load image using Glide
            Glide.with(this)
                .load(url)
                .into(binding.ivMessage)

            // Set title
            binding.txtTile.text = titleText

            // Handle cross (close) button click
            binding.ivCross.setOnClickListener {
                mySyncPopUpAlert?.dismiss()
            }

            // Handle allow button click
            binding.btnAllow.setOnClickListener {
                mySyncPopUpAlert?.dismiss()
                if (actionNeeded == "RE_SYNC") {
                   // startActivity(Intent(this, SyncContactActivity::class.java))
                } else {
                    startActivity(Intent(this, WelcomeSyncContactActivityKotlin::class.java))
                }
            }

            mySyncPopUpAlert?.apply {
                setCancelable(true)
                setCanceledOnTouchOutside(true)
                show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //endregion

    // region webEnagage Event
    private fun trackMainMenuEvent(strOption: String) {
        // Create event attributes
        val eventAttributes = mutableMapOf<String, Any>()
        eventAttributes["Option Clicked"] = strOption

        // Track the login event using WebEngageHelper
        WebEngageAnalytics.getInstance().trackEvent("Main Menu Clicked", eventAttributes)
    }

    //endregion

    // region ShortcutMenu
    private fun shortcutAppMenu() {
        try {
            if (prefsManager.getEmpData() != null && prefsManager.getUserConstantEntity() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                    val shortcutManager = getSystemService(ShortcutManager::class.java)

                    shortcutManager?.let {


                        val motorUrl = prefsManager.getFourWheelerUrl()
                        val bikeUrl =prefsManager.getTwoWheelerUrl()
                        val healthUrl = prefsManager.getHealthurl()

                        val expressUrl = prefsManager.getDashboardarray().firstOrNull {
                            it.ProdId.toInt() == 35
                        }?.url.orEmpty()

                        var ipAddress = try { "" } catch (e: Exception) { "0.0.0.0" }

                        val parentSsid = ""

                        val appendParams = "&ip_address=$ipAddress&mac_address=$ipAddress&app_version=policyboss-${BuildConfig.VERSION_NAME}&device_id=${Utility.getDeviceID(this@HomeActivity)}&login_ssid=$parentSsid"

                        val motorUrlWithParams = motorUrl + appendParams + "&product_id=1"
                        val bikeUrlWithParams = bikeUrl + appendParams + "&product_id=10"
                        val healthUrlWithParams = healthUrl + appendParams.replace("&mac_address", "") // Remove unused params
                        val expressUrlWithParams = expressUrl + appendParams + "&product_id=35"

                        val intentPrivateCar = createWebIntent(motorUrlWithParams, "Motor Insurance")
                        val intentBike = createWebIntent(bikeUrlWithParams, "Two Wheeler Insurance")
                        val intentHealthIns = createWebIntent(healthUrlWithParams, "Health Insurance")
                        val intentExpressUrl = createExpressIntent(expressUrlWithParams)

                        val shortcuts = listOf(
                            createShortcut("ID1", "Sync Contacts", R.drawable.sync_contact, intentExpressUrl, 0),
                            createShortcut("ID2", "Health Insurance", R.drawable.health_insurance_sm, intentHealthIns, 1),
                            createShortcut("ID3", "Private Car", R.drawable.private_car_sm, intentPrivateCar, 2),
                            createShortcut("ID4", "Two Wheeler", R.drawable.two_wheeler_sm, intentBike, 3)
                        )

                        shortcutManager.dynamicShortcuts = shortcuts
                    }
                }
            }
        } catch (ex: Exception) {
            Log.d("SHORTCUTMENU", ex.toString())
            Log.d(Constant.TAG, ex.toString())
        }
    }


    private fun createWebIntent(url: String, title: String): Intent {
        return Intent(this@HomeActivity, CommonWebViewActivity::class.java).apply {
            putExtra("URL", url)
            putExtra("dashBoardtype", "INSURANCE")
            putExtra("NAME", title)
            putExtra("TITLE", title)
            putExtra("APPMENU", "Y")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            action = Intent.ACTION_VIEW
        }
    }

    private fun createExpressIntent(url: String): Intent {
        return Intent(this@HomeActivity, WelcomeSyncContactActivityKotlin::class.java).apply {
            // putExtra("URL", url)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            action = Intent.ACTION_VIEW
        }
    }

    private fun createShortcut(id: String, label: String, iconRes: Int, intent: Intent, rank: Int): ShortcutInfo {
        return ShortcutInfo.Builder(this, id)
            .setShortLabel(label)
            .setLongLabel(label)
            .setIcon(Icon.createWithResource(this, iconRes))
            .setIntent(intent)
            .setRank(rank)
            .build()
    }
    //endregion


    //region Observer
    private fun observeMasterState() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Collecting masterState
                launch {
                    viewModel.masterState.collectLatest { state ->
                        when (state) {

                            is APIState.Loading -> {

                                //region comment
                                // displayLoadingWithText()
//                            if( isSwipeRefresh){
//                                binding.swipeRefreshLayout.isRefreshing = true
//                            }else{
//                                displayLoadingWithText()
//                            }
                                //endregion
                                displayLoadingWithText()

                            }
                            is APIState.Success -> {

                                hideLoading()

                                setupDashBoardData()

//                                state.data?.horizonDetail?.SYNC_CONTACT?.let { syncContactEntity ->
//
//                                    if( syncContactEntity.ACTION_NEEDED.equals("NO_ACTION")){
//                                        showMySyncPopUpAlert(syncContactEntity)
//                                    }
//
//                                }

                                state.data?.horizonDetail?.SYNC_CONTACT?.takeIf { it.ACTION_NEEDED == "NO_ACTION"}
                                    ?.let { syncContactEntity->

                                        showMySyncPopUpAlert(syncContactEntity)

                                    }
                                // binding.swipeRefreshLayout.isRefreshing = false

                            }
                            is APIState.Empty -> {
                                hideLoading()
                            }
                            is APIState.Failure -> {
                                // binding.swipeRefreshLayout.isRefreshing = false
                                hideLoading()
                            }
                        }
                    }
                }

                launch {
                    viewModel.productShareResponse.collect{  event->

                        event.contentIfNotHandled?.let {

                            when (it) {
                                is APIState.Empty -> {

                                    hideLoading()
                                }

                                is APIState.Failure -> {
                                    hideLoading()
                                    this@HomeActivity.showSnackbar(binding.root, it.errorMessage)
                                }

                                is APIState.Loading -> {

                                    displayLoadingWithText()
                                }

                                is APIState.Success -> {
                                    hideLoading()

                                    if (it.data != null) {
                                        it.data?.let { shareEntity ->
                                            datashareList(
                                                this@HomeActivity,

                                                viewModel.ShareTitle(),
                                                shareEntity.msg,
                                                shareEntity.url
                                            )
                                        }

                                    }

                                }


                            }
                        }

                    }
                }

            }
        }


    }

    //endregion
}