package com.policyboss.policybosspro.view.home

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.policyboss.policybosspro.BaseActivity
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.model.homeDashboard.DashboardMultiLangEntity
import com.policyboss.policybosspro.core.viewModel.homeVM.HomeViewModel
import com.policyboss.policybosspro.databinding.ActivityHomeBinding
import com.policyboss.policybosspro.databinding.LayoutSharePopupBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.showAlert
import com.policyboss.policybosspro.view.home.adapter.DashboardRowAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var dashboardAdapter: DashboardRowAdapter
    private lateinit var shareProdDialog: AlertDialog

    private val vewModel by viewModels<HomeViewModel>()
    @Inject
    lateinit var prefsManager: PolicyBossPrefsManager

    private lateinit var toggle: ActionBarDrawerToggle



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize views
        // Set the toolbar as ActionBar
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

        // Add a back press dispatcher callback to handle back presses
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
        vewModel.getMasterData()


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
            listIns =  vewModel.getInsurProductLangList(),
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
            //05 temp
          //  shareDashbordProduct(shareEntity)
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

    private fun observeMasterState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {

                vewModel.masterState.collectLatest { state ->
                    when (state) {

                        is APIState.Loading -> {
                            displayLoadingWithText()
                        }
                        is APIState.Success -> {
                            hideLoading()

                            setupDashBoardData()
                        }
                        is APIState.Empty -> {
                            hideLoading()
                        }
                        is APIState.Failure -> {

                            hideLoading()
                        }
                    }
                }
            }
        }
    }
}