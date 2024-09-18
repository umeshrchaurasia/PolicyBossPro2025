package com.policyboss.policybosspro.view.splashscreen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.policyboss.policybosspro.databinding.ActivitySplashScreenBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.view.home.HomeActivity
import com.policyboss.policybosspro.view.introslider.WelcomeActivity
import com.policyboss.policybosspro.view.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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
        if (prefManager.isFirstTimeLaunch()) {
            startActivity(
                Intent(this, WelcomeActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }else{

            Handler(Looper.getMainLooper()).postDelayed({
                this.finish()

                if (prefManager.getEmpData() != null) {
                    startActivity(
                        Intent(this@SplashScreenActivity, HomeActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )

                }else{

                    startActivity(
                        Intent(this@SplashScreenActivity, LoginActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )

                }
            }, 3000)
        }

    }
}