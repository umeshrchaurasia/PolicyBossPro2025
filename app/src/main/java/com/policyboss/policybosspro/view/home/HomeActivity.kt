package com.policyboss.policybosspro.view.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.policyboss.policybosspro.BaseActivity
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.model.homeDashboard.DashboardMultiLangEntity
import com.policyboss.policybosspro.core.model.sysncContact.SyncContactEntity
import com.policyboss.policybosspro.core.viewModel.homeVM.HomeViewModel
import com.policyboss.policybosspro.databinding.ActivityHomeBinding
import com.policyboss.policybosspro.databinding.LayoutMysyncPopupBinding
import com.policyboss.policybosspro.databinding.LayoutSharePopupBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.showSnackbar
import com.policyboss.policybosspro.view.home.adapter.DashboardRowAdapter
import com.policyboss.policybosspro.view.syncContact.welcome.WelcomeSyncContactActivityKotlin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var dashboardAdapter: DashboardRowAdapter
    private lateinit var shareProdDialog: AlertDialog
    private var mySyncPopUpAlert: AlertDialog? = null

    private var isSwipeRefresh = false

    private val viewModel by viewModels<HomeViewModel>()
    @Inject
    lateinit var prefsManager: PolicyBossPrefsManager

    private lateinit var toggle: ActionBarDrawerToggle



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
        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_finbox -> {
                    // Handle nav item 1 click
                }
                R.id.nav_AppointmentLetter -> {
                    // Handle nav item 2 click
                }
                // Add more cases as needed
            }
            // Close the drawer after item selection
            binding.drawer.closeDrawer(GravityCompat.START)
            true
        }
        //endregion


    }

    private fun setupDashBoardData() {


        dashboardAdapter = DashboardRowAdapter(
            mContext = this,
            insurancePosition = 0,
            disclosurePosition = 1,
            listIns =  viewModel.getInsurProductLangList(),
            prefsManager = prefsManager,
            onShareClick = ::onShareListener,
            onInfoClick = ::onInfoListener

            )
        binding.rvHome.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = dashboardAdapter
            setHasFixedSize(true)
            setItemViewCacheSize(20)
        }
    }

    fun onShareListener(entity: DashboardMultiLangEntity){
        viewModel.setCurrentDashboardShareEntity(entity)
        shareProductPopUp(shareEntity = entity)

    }
    fun onInfoListener(entity: DashboardMultiLangEntity){

    }

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

                                state.data?.horizonDetail?.SYNC_CONTACT?.let { syncContactEntity ->


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
}