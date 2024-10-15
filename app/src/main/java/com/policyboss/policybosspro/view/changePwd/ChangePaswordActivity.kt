package com.policyboss.policybosspro.view.changePwd

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.policyboss.policybosspro.BaseActivity
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.databinding.ActivityChangePaswordBinding
import com.policyboss.policybosspro.databinding.ActivityHomeBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.webengage.sdk.android.WebEngage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ChangePaswordActivity : BaseActivity(), OnClickListener {

    private lateinit var binding: ActivityChangePaswordBinding

    @Inject
    lateinit var prefsManager: PolicyBossPrefsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePaswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.apply {

            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle("Change Password")
        }



        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Call finish() to close the activity
                this@ChangePaswordActivity.finish()
            }
        })


        binding.btnChangePassword.setOnClickListener(this)

    }

    override fun onStart() {
        super.onStart()
        val weAnalytics = WebEngage.get().analytics()
        weAnalytics.screenNavigated("ChangePassword Screen")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Finish the activity when the Up button is pressed
                this@ChangePaswordActivity.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(view: View?) {
        when (view!!.getId()) {

            binding.btnChangePassword.id -> {
                when {
                    binding.etOldPassword.text.isNullOrEmpty() -> {
                        binding.etOldPassword.error = "Enter old password"
                        binding.etOldPassword.requestFocus() // Set focus to the EditText
                        return
                    }
                    binding.etNewPassword.text.isNullOrEmpty() -> {
                        binding.etNewPassword.error = "Enter New password"
                        binding.etNewPassword.requestFocus() // Set focus to the EditText
                        return
                    }

                    binding.etNewPassword.text!!.length < 6 -> {
                        binding.etNewPassword.error = "Minimum 6 characters required"
                        binding.etNewPassword.requestFocus() // Set focus to the EditText
                        return
                    }

                    binding.etConfirmPassword.text.toString() != binding.etNewPassword.text.toString() -> {
                        binding.etConfirmPassword.error = "Incorrect password."
                        binding.etConfirmPassword.requestFocus() // Set focus to the EditText
                        return
                    }
                }


            }
        }
    }
}