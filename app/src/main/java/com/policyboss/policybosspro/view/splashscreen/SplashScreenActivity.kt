package com.policyboss.policybosspro.view.splashscreen

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.messaging.FirebaseMessaging
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
/*********************** For Deeplink *******************************************

 Mark Must hosted this link :  https://www.policyboss.com/.well-known/assetlinks.json
 in our website for deeplink has to work
 *********************** *********************** *********************** ************/

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    @Inject
    lateinit var prefManager : PolicyBossPrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        //setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen()
        }
        setContentView(binding.root)

        // getDynamicLinkFromFirebase()



        // Launch coroutine for initialization sequence
        lifecycleScope.launch {
            handleInitialization()
        }


        //region comment
//        getToken()
//
//        if (prefManager.isFirstTimeLaunch()) {
//            startActivity(
//                Intent(this, WelcomeActivity::class.java)
//                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            )
//        }else{
//
//            Handler(Looper.getMainLooper()).postDelayed({
//                this.finish()
//
//                if (prefManager.getEmpData() != null) {
//                    startActivity(
//                        Intent(this@SplashScreenActivity, HomeActivity::class.java)
//                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                    )
//
//                }else{
//
//                    startActivity(
//                        Intent(this@SplashScreenActivity, LoginActivity::class.java)
//                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                    )
//
//                }
//            }, 3000)
//        }

        //endregion


        // ATTENTION: This was auto-generated to handle app links.
        val appLinkIntent: Intent = intent
        val appLinkAction: String? = appLinkIntent.action
        val appLinkData: Uri? = appLinkIntent.data
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)   //setIntent(intent) ensures getIntent() returns the latest one if needed later.
        handleDeepLink(intent)
    }
    //**********************************************************//

    private fun handleDeepLink1(intent: Intent?) {
        val data = intent?.data
        if (data != null && data.path?.startsWith("/PolicyBossPro.github.io/referral") == true) {
            val code = data.getQueryParameter("code")
            Log.d(Constant.TAG, "Referral code: $code")
            // TODO: Navigate to referral screen or save code

            showAlert("Referral code: $code")
        }
    }


    private fun handleDeepLink(intent: Intent?) {

        //region test deeplink
//        val url = "https://www.policyboss.com/deeplink/health-insurance?product_id=2&title=Health+Insurance"
//
//        val uri = Uri.parse(url)
//        // Pass into your function
//        processDeeplink(uri)
        //endregion

        processDeeplink(intent?.data)
    }

    private fun processDeeplink(uri: Uri?) {
        uri?.let {
            val host = it.host ?: "Unknown"
            val path = it.path ?: "Unknown"

            val productId = it.getQueryParameter("product_id")?.takeIf { id -> id.isNotBlank() } ?: "N/A"
            val title = it.getQueryParameter("title")?.takeIf { t -> t.isNotBlank() } ?: "N/A"

            Log.d("DeepLink", "URI: $uri")
            Log.d("DeepLink", "Host = $host, Path = $path, ID = $productId, Title = $title")

     //Note : Replace /deeplink/ from url bec we set deeplink url like https://www.policyboss.com/deeplink/...

            prefManager.setDeeplink(uri.toString())
            // Navigate or store as needed
        }
    }


    private suspend fun handleInitialization() {
        try {
            // First fetch token

            initAuthReceiver()
            val token = getToken()

             handleDeepLink(intent)


            // Then handle navigation
            if (prefManager.isFirstTimeLaunch()) {
                navigateToWelcome()
            } else {
                delay(2000) // Optional delay if you still want it
                navigateBasedOnLoginStatus()
            }
        } catch (e: Exception) {
            Log.e("Initialization", "Error during initialization", e)
            // Handle error case - maybe show error UI or retry
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

    private fun initAuthReceiver(){

//        val  acesstoken = AccessToken()
//        var oathToken = acesstoken.accessToken
//
//        val tokenGenerator = TokenGenerator()
//        val accessToken = tokenGenerator.getAccessTokenFromServiceAccount()






    }



}