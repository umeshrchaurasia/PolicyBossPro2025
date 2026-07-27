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
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.messaging.FirebaseMessaging
import com.policyboss.policybosspro.BuildConfig
import com.policyboss.policybosspro.databinding.ActivitySplashScreenBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.utils.showAlert

import com.policyboss.policybosspro.view.home.HomeActivity
import com.policyboss.policybosspro.view.introslider.WelcomeActivity
import com.policyboss.policybosspro.view.login.LoginActivity
import com.policyboss.policybosspro.view.syncContact.ui.WelcomeSyncContactActivityKotlin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
//             val testUrl = "https://www.policyboss.com/deeplink?product_id=10"
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


    /**
     * Triggered when the Activity is already running (e.g., in background)
     * and a new deep link intent brings it to the foreground.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Ensure getIntent() returns this new intent in the future
        setIntent(intent)

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
            initAuthReceiver()
            getToken()

            subscribeToAllUsers()

           //  handleDeepLink(intent)


            // Then handle navigation
            if (prefManager.isFirstTimeLaunch()) {
                navigateToWelcome()
            } else {
                delay(2000.milliseconds) // Optional delay if you still want it
                navigateBasedOnLoginStatus()
            }
        } catch (e: Exception) {
            Log.e("Initialization", "Error during initialization", e)
            // Handle error case - maybe show error UI or retry

            startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
            finish()
        }
    }

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

        startActivity(
            Intent(this, targetActivity)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }

    //**********************************************************//



    private fun getDynamicLinkFromFirebaseOld() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                Log.d("dynamic", "We have link")
                var deepLink: Uri? = null

                // Check if we received a dynamic link from Firebase
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }

                if (deepLink != null) {
                    // Dynamic link exists, process it
                    val deeplinkUrl = deepLink.toString()
                    Log.i("dynamic url", deeplinkUrl)

                    // Save to preferences or handle the deeplink
                    prefManager.setDeeplink(deeplinkUrl)

                    // Call method to handle deep link (navigation, etc.)

                } else {
                    // No dynamic link, check for regular intent URI
                    val uri = intent.data
                    if (uri != null) {
                        val deeplinkUrl = uri.toString()
                        Log.i("intent url", deeplinkUrl)

                        // Save and handle the normal intent
                        prefManager.setDeeplink(deeplinkUrl)

                    } else {
                        // No deep link or URI found, proceed with normal flow

                    }
                }
            }
            .addOnFailureListener(this) { e ->
                Log.w("HomeActivity", "getDynamicLink:onFailure", e)
                // Proceed normally if dynamic link fetching failed
               // handleNoDeepLink()
            }
    }


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