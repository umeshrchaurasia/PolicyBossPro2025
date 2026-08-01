package com.policyboss.policybosspro.view.introslider

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.policyboss.demoandroidapp.Utility.ExtensionFun.applySystemBarInsetsPadding
import com.policyboss.policybosspro.BuildConfig
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.analytics.AnalyticsBranchIOHelper
import com.policyboss.policybosspro.analytics.BranchCustomEvents
import com.policyboss.policybosspro.databinding.ActivityWelcomeBinding
import com.policyboss.policybosspro.utils.AppPermission.AppPermissionManager
import com.policyboss.policybosspro.utils.AppPermission.PermissionHandler
import com.policyboss.policybosspro.utils.CoroutineHelper
import com.webengage.sdk.android.WebEngage
import io.branch.referral.util.BRANCH_STANDARD_EVENT

class WelcomeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var myViewPagerAdapter: MyViewPagerAdapter
    private lateinit var layouts: IntArray
    private var current = 0

    // 1. Declare Permission Handler and a flag to prevent multiple prompts
    private lateinit var permissionHandler: PermissionHandler
    private var hasAskedNotification = false

    override fun onStart() {
        super.onStart()
        val weAnalytics = WebEngage.get().analytics()
        weAnalytics.screenNavigated("Welcome Screen")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Opt into edge-to-edge drawing
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.applySystemBarInsetsPadding()


        // 2. Initialize your Permission Handler
        permissionHandler = PermissionHandler(this)

        // Inside WelcomeActivity.kt -> onCreate() or pageChangeCallback (position == 0)

        AnalyticsBranchIOHelper.trackCustomEvent(
            context = this@WelcomeActivity,
            eventName = BranchCustomEvents.TUTORIAL_BEGIN,
            screenName = "WelcomeActivity",
            description = "User started the onboarding slider"
            // customData is completely omitted because it's not needed here
        )

        AnalyticsBranchIOHelper.trackCustomEvent(
            context = this@WelcomeActivity,
            eventName = BranchCustomEvents.APP_OPEN, // Keep this generic: "app_open"
            screenName = "WelcomeActivity",
            customData = mapOf(
                "login_status" to "false",
                "user_status" to  "NEW_INSTALL"
            )
        )


        initWidgets()
        setListener()
        CoroutineHelper.saveDeviceDetails(this@WelcomeActivity, "0", "Install")
    }

    private fun setListener() {
        myViewPagerAdapter = MyViewPagerAdapter(layouts)
        binding.viewPager.adapter = myViewPagerAdapter

        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)
        binding.btnNext.setOnClickListener(this)
        binding.btnSkip.setOnClickListener(this)
    }

    private fun initWidgets() {
        layouts = intArrayOf(
            R.layout.welcome_slide1,
            R.layout.welcome_slide2,
            R.layout.welcome_slide3,
            R.layout.welcome_slide4,
            R.layout.welcome_slide5
        )
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_next -> {
                current++
                if (current < layouts.size) {
                    binding.viewPager.currentItem = current
                } else {

                    AnalyticsBranchIOHelper.trackStandardEvent(
                        context = this,
                        eventType = BRANCH_STANDARD_EVENT.COMPLETE_TUTORIAL,
                        screenName = "WelcomeActivity",
                        description = "User completed the onboarding slider"
                    )
                    startActivity(Intent(this, EulaActivity::class.java))
                }
            }
            R.id.btn_skip -> {
                AnalyticsBranchIOHelper.trackStandardEvent(
                    context = this,
                    eventType = BRANCH_STANDARD_EVENT.COMPLETE_TUTORIAL,
                    screenName = "WelcomeActivity",
                    description = "User completed the onboarding slider"
                )
                startActivity(Intent(this, EulaActivity::class.java))
            }
        }
    }

    //  ViewPager2 change listener
    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            current = position
            setSelectedDot(position + 1)

            // 1. Ask for permission ONLY on the first slide (position == 0), and ONLY once
            if (position == 0 && !hasAskedNotification) {
                hasAskedNotification = true
                requestNotificationPermission()
            }
            // 2. Update button states based on which slide we are on
            if (position == layouts.size - 1) {
                binding.btnNext.text = getString(R.string.get_started)
                binding.btnSkip.visibility = View.VISIBLE



            } else {
                binding.btnNext.text = getString(R.string.next)
                binding.btnSkip.visibility = View.VISIBLE
            }
        }
    }

    private fun setSelectedDot(current: Int) {

        // Get the drawables safely using AppCompatResources
        val unselectedDot = AppCompatResources.getDrawable(this, R.drawable.unselected_dot)
        val selectedDot = AppCompatResources.getDrawable(this, R.drawable.selected_dot)

        // Reset all dots to unselected
        binding.dot1.setImageDrawable(unselectedDot)
        binding.dot2.setImageDrawable(unselectedDot)
        binding.dot3.setImageDrawable(unselectedDot)
        binding.dot4.setImageDrawable(unselectedDot)
        binding.dot5.setImageDrawable(unselectedDot)

        // Set the selected dot based on position
        when (current) {
            1 -> binding.dot1.setImageDrawable(selectedDot)
            2 -> binding.dot2.setImageDrawable(selectedDot)
            3 -> binding.dot3.setImageDrawable(selectedDot)
            4 -> binding.dot4.setImageDrawable(selectedDot)
            5 -> binding.dot5.setImageDrawable(selectedDot)
        }
    }

    // ViewPager2 Adapter using RecyclerView.Adapter
    inner class MyViewPagerAdapter(private val layouts: IntArray) :
        RecyclerView.Adapter<MyViewPagerAdapter.ViewHolder>() {

        inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
            fun bind(layoutRes: Int) {
                // No need for additional binding in this case, view is inflated in onCreateViewHolder
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            // Inflate the specific layout for the position
            val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // Not much binding needed for static layouts
            holder.bind(layouts[position])
        }

        override fun getItemCount(): Int {
            return layouts.size
        }

        override fun getItemViewType(position: Int): Int {
            // Return the specific layout for this position
            return layouts[position]
        }
    }

    // 4. Add your Notification Permission logic here
    private fun requestNotificationPermission() {
        permissionHandler.checkAndRequestPermissions(
            AppPermissionManager.PermissionType.POST_NOTIFICATIONS,
            onResult = { granted ->
                if (granted) {
                    // Perfect, they allowed it. Do nothing, let them click "Get Started"
                } else {
                    // As you suggested in your comments, show a polite Snackbar if they deny
                    Snackbar.make(
                        binding.root,
                        "You might miss important policy updates.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            },
            onPermanentlyDenied = { permanentlyDeniedPermissions ->
                // Show an explanation or direct the user to the settings
                showPermissionDeniedDialog(permanentlyDeniedPermissions)
            }
        )
    }

    private fun showPermissionDeniedDialog(permanentlyDeniedPermissions: List<String>) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permission Denied")
            .setMessage("Please enable notification permissions in Settings to receive important policy alerts.")
            .setPositiveButton("Open Settings") { _, _ ->
                permissionHandler.openAppSettings()
            }
            .setNegativeButton("Maybe Later", null) // Use "Maybe Later" instead of "Cancel" for a friendlier UI
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()


    }
}