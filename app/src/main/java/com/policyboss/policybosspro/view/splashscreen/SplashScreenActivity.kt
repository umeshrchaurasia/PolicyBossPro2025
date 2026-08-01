package com.policyboss.policybosspro.view.splashscreen

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
//import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.messaging.FirebaseMessaging
import com.policyboss.policybosspro.BuildConfig
import com.policyboss.policybosspro.analytics.AnalyticsBranchIOHelper
import com.policyboss.policybosspro.analytics.BranchCustomEvents
import com.policyboss.policybosspro.databinding.ActivitySplashScreenBinding
import com.policyboss.policybosspro.facade.DeepLinkEntity
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.utils.FirebasePushNotification.FcmTopicManager
import com.policyboss.policybosspro.utils.showAlert

import com.policyboss.policybosspro.view.home.HomeActivity
import com.policyboss.policybosspro.view.introslider.WelcomeActivity
import com.policyboss.policybosspro.view.login.LoginActivity
import com.policyboss.policybosspro.view.syncContact.ui.WelcomeSyncContactActivityKotlin
import dagger.hilt.android.AndroidEntryPoint
import io.branch.referral.Branch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/*********************** For Deeplink *******************************************

 Mark Must hosted this link :  https://www.policyboss.com/.well-known/assetlinks.json
 in our website for deeplink has to work
 *********************** *********************** *********************** ************/

@AndroidEntryPoint
class  SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    @Inject
    lateinit var prefManager : PolicyBossPrefsManager

    @Inject
    lateinit var fcmTopicManager: FcmTopicManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen()
        }
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // =========================================================
        // 🚨 MOCK DEEP LINK FOR DEBUG TESTING ONLY 🚨
        // =========================================================
//        if (BuildConfig.DEBUG) {
//            // Uncomment the one you want to test:
//
//            //val testUrl ="http://zextratravelassist.interstellar.co.in/static/media/TravelAssist.188539aef4163a318258.webp?_branch_match_id=1493474052034962988&utm_source=Website&utm_campaign=FirstOPen&utm_medium=Push&_branch_referrer=H4sIAAAAAAAAA8soKSkottLXL8jPyUyuTMovLtZLLCjQy8nMy9YPyC8ILcqxrytKTUstKsrMS49PKsovL04tsnXOKMrPTQUADJvNLjwAAAA%3D"
//
//           //  val testUrl = "https://www.policyboss.com/deeplink?product_id=10"
//            //val testUrl =  "https://www.policyboss.com/deeplink?product_id=WB&url=https://www.policyboss.com/about-us"
//           // val testUrl = "https://www.policyboss.com/deeplink?product_id=10"
//          //  val testUrl = "https://www.policyboss.com/deeplink?product_id=DB&url=https://www.policyboss.com/UI22/car-insurance"
//
//            // If the system didn't already pass a deep link, inject ours
//            if (intent.data == null) {
//              //  intent.data = Uri.parse(testUrl)
//                intent.data = testUrl.toUri()
//            }
//        }
        // =========================================================

      // 2. Capture Deep Link on Cold Start
        // We do this immediately so the PrefsManager has the URL before HomeActivity opens
        handleDeepLink(intent)

        //3. Launch coroutine for initialization sequence
        lifecycleScope.launch {
            handleInitialization()
        }




        // ATTENTION: This was auto-generated to handle app links.
//        val appLinkIntent: Intent = intent
//        val appLinkAction: String? = appLinkIntent.action
//        val appLinkData: Uri? = appLinkIntent.data
    }


    override fun onStart() {
        super.onStart()

        //region Initialize Branch session
        Branch.sessionBuilder(this)
            .withCallback { referringParams, error ->
            if (error == null && referringParams != null) {
                Log.i(Constant.TAG_DEEPLINK, "Branch Config Data: $referringParams")

                val clickedBranchLink = referringParams.optBoolean("+clicked_branch_link", false)
                if (clickedBranchLink) {
                    // Extract custom data defined in your Branch dashboard/links
                    // 1. Extract your custom data from the Branch dashboard payload
                    val productId = referringParams.optString("product_id", "")
                    val customUrl = referringParams.optString("url", "")
                    val title = referringParams.optString("title", "")

                    Log.d(Constant.TAG_DEEPLINK, "Captured customUrl: $customUrl | title: $title  | ProductID: $productId")

                    // Map Branch JSON back into the URI structure HomeActivity already expects
                    // 2. Map it into the format deeplinkHandle() already expects!
                    if (productId.isNotEmpty()) {
                        val entity = DeepLinkEntity(
                            productId = productId,
                            url = customUrl.ifEmpty { null },
                            title = title.ifEmpty { null }
                        )
                        prefManager.setPendingDeepLink(entity)
                    }
                }
            } else {
                Log.e(Constant.TAG_DEEPLINK, "Branch Init Error: ${error?.message}")
            }
        }.withData(this.intent?.data).init()
        //endregion
    }


    /**
     * Triggered when the Activity is already running (e.g., in background)
     * and a new deep link intent brings it to the foreground.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Ensure getIntent() returns this new intent in the future
        setIntent(intent)




        //region Branch Handling
        Branch.sessionBuilder(this).withCallback { referringParams, error ->
            if (error == null && referringParams != null) {
                val clickedBranchLink = referringParams.optBoolean("+clicked_branch_link", false)
                if (clickedBranchLink) {
                    // 1. Extract all custom data just like onStart
                    val productId = referringParams.optString("product_id", "")
                    val customUrl = referringParams.optString("url", "")
                    val title = referringParams.optString("title", "") // Added missing title

                    Log.d(Constant.TAG_DEEPLINK, "Captured customUrl: $customUrl | title: $title  | ProductID: $productId")

                    // 2. Map directly to DeepLinkEntity just like onStart
                    if (productId.isNotEmpty()) {
                        val entity = DeepLinkEntity(
                            productId = productId,
                            url = customUrl.ifEmpty { null },
                            title = title.ifEmpty { null }
                        )

                        // Use the exact same preference method
                        prefManager.setPendingDeepLink(entity)
                    }
                }
            }
        }.reInit()
        //endregion


        // Process the new deep link and trigger the navigation flow again
        handleDeepLink(intent)

        lifecycleScope.launch {
            navigateBasedOnLoginStatus()
        }
    }
    //**********************************************************//



    /**
     * Extracts URI from Intent and saves it globally.
     * The actual cleaning/regex logic belongs inside PolicyBossPrefsManager.
     */
    private fun handleDeepLink(intent: Intent?) {

        val uri = intent?.data ?: return // Exit immediately if no URI exists

        val host = uri.host ?: "Unknown"
        val path = uri.path ?: "Unknown"
        val productId = uri.getQueryParameter("product_id") ?: "N/A"

        Log.d("DeepLink", "Captured URI: $uri | Host: $host | Path: $path | ProductID: $productId")

        // Save to preferences. PrefManager will handle the regex cleaning.
        prefManager.setDeeplink(uri.toString())
    }




    private suspend fun handleInitialization() {
        try {
            // First fetch token
            // Background tasks triggered in parallel (or quickly sequentially)

            getToken()

            // Subscribe to all_users once
        // 1. Everyone gets the ALL_USER topic
            fcmTopicManager.subscribeToAllUsersAsync()

            // 2. ONLY Guests get the GUEST_USERS topic
            if (prefManager.getEmpData() == null) {
                fcmTopicManager.subscribeToGuestAsync()
            }

            // Handle navigation...
            if (prefManager.isFirstTimeLaunch()) {



                navigateToWelcome()
            } else {
                delay(2000.milliseconds)
                navigateBasedOnLoginStatus()
            }
        } catch (e: Exception) {
            Log.e("Initialization", "Error during initialization", e)
            // Handle error case - maybe show error UI or retry

            startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
            finish()
        }
    }

//    private fun handleUserTopicSubscriptions() {
//
//       // isGuestTopicSubscribed()      // false
//      //  isLoggedInTopicSubscribed()   // false
//
//        if (prefManager.getEmpData() != null) {
//
//            // Logged-in user
//            if (!prefManager.isLoggedInTopicSubscribed()) {
//
//                Log.d("FCM", "Switching to logged_in_users")
//
//                fcmTopicManager.switchToLoggedIn()
//
//            } else {
//
//                Log.d("FCM", "Already subscribed to logged_in_users")
//            }
//
//        } else {
//
//            // Guest user
//            if (!prefManager.isGuestTopicSubscribed()) {
//
//                Log.d("FCM", "Switching to guest_users")
//
//                fcmTopicManager.switchToGuest()
//
//            } else {
//
//                Log.d("FCM", "Already subscribed to guest_users")
//            }
//        }
//    }



    private fun navigateToWelcome() {
        startActivity(
            Intent(this, WelcomeActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }

    private fun navigateBasedOnLoginStatus() {
        val targetActivity = if (prefManager.getEmpData() != null && prefManager.isUserLoginSyncContact()) {
            HomeActivity::class.java
        } else if(prefManager.getEmpData() != null && (!prefManager.isUserLoginSyncContact()) ) {
            WelcomeSyncContactActivityKotlin::class.java
        } else {
            LoginActivity::class.java
        }

//        startActivity(
//            Intent(this, targetActivity)
//                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        )

        val nextIntent = Intent(this, targetActivity).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            // ========================================================
            // ADD THIS: Pass the Notification payload to the next screen
            // ========================================================
            if (this@SplashScreenActivity.intent.extras != null) {
                putExtras(this@SplashScreenActivity.intent.extras!!)
            }
        }

        startActivity(nextIntent)
        finish()
    }

    //**********************************************************//



//    private fun getDynamicLinkFromFirebaseOld() {
//        FirebaseDynamicLinks.getInstance()
//            .getDynamicLink(intent)
//            .addOnSuccessListener(this) { pendingDynamicLinkData ->
//                Log.d("dynamic", "We have link")
//                var deepLink: Uri? = null
//
//                // Check if we received a dynamic link from Firebase
//                if (pendingDynamicLinkData != null) {
//                    deepLink = pendingDynamicLinkData.link
//                }
//
//                if (deepLink != null) {
//                    // Dynamic link exists, process it
//                    val deeplinkUrl = deepLink.toString()
//                    Log.i("dynamic url", deeplinkUrl)
//
//                    // Save to preferences or handle the deeplink
//                    prefManager.setDeeplink(deeplinkUrl)
//
//                    // Call method to handle deep link (navigation, etc.)
//
//                } else {
//                    // No dynamic link, check for regular intent URI
//                    val uri = intent.data
//                    if (uri != null) {
//                        val deeplinkUrl = uri.toString()
//                        Log.i("intent url", deeplinkUrl)
//
//                        // Save and handle the normal intent
//                        prefManager.setDeeplink(deeplinkUrl)
//
//                    } else {
//                        // No deep link or URI found, proceed with normal flow
//
//                    }
//                }
//            }
//            .addOnFailureListener(this) { e ->
//                Log.w("HomeActivity", "getDynamicLink:onFailure", e)
//                // Proceed normally if dynamic link fetching failed
//               // handleNoDeepLink()
//            }
//    }


    private fun getToken(){

       // Fetch the FCM token in the background
       lifecycleScope.launch(Dispatchers.IO) {

           // 1. Existing Logic: Fetch and save the unique device token
           FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
               if (task.isSuccessful) {
                   val token = task.result

                   token?.let {
                       // Save the token in SharedPreferences
                       prefManager.setToken(it)
                       Log.d("FCMToken", "Token fetched on app start: $it")
                   }
               } else {
                   Log.e("FCMToken", "Fetching FCM token failed", task.exception)
               }
           }


       }

   }


    private fun subscribeToAllUsers() {

        if (prefManager.isSubscribedToAllUsers()) {
            Log.d("FCM", "Already subscribed to ${Constant.ALL_USER}")
            return
        }

        FirebaseMessaging.getInstance()
            .subscribeToTopic(Constant.ALL_USER)   // Topic Logic : Here subscribeToTopic all_users
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    prefManager.setSubscribedToAllUsers(true)

                    Log.d(
                        "FCM",
                        "Successfully subscribed to ${Constant.ALL_USER}"
                    )

                } else {

                    Log.e(
                        "FCM",
                        "Topic subscription failed",
                        task.exception
                    )
                }
            }
    }

    private fun initAuthReceiver(){

//        val  acesstoken = AccessToken()
//        var oathToken = acesstoken.accessToken
//
//        val tokenGenerator = TokenGenerator()
//        val accessToken = tokenGenerator.getAccessTokenFromServiceAccount()






    }



}