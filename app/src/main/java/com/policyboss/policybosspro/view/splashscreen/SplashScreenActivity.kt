package com.policyboss.policybosspro.view.splashscreen

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.databinding.ActivitySplashScreenBinding
import com.policyboss.policybosspro.databinding.ActivityWelcomeBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.view.login.LoginActivity
import javax.inject.Inject

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    @Inject
    lateinit var sharePrefManager : PolicyBossPrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Handler(Looper.getMainLooper()).postDelayed({
            this.finish()

            val options = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )

//            if(sharePrefManager.getEnableProPOSPurl()?.length ?: 0 > 0) {
//                startActivity(Intent(this, LoginViaMpinActivity::class.java), options.toBundle())
//
//            }else{
//
//                startActivity(Intent(this, LoginActivity::class.java), options.toBundle())
//            }
        }, 3000)
    }
}