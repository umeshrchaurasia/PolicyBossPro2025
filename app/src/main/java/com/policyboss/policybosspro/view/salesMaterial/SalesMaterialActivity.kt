package com.policyboss.policybosspro.view.salesMaterial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.policyboss.policybosspro.BaseActivity
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.response.salesMaterial.CompanyEntity
import com.policyboss.policybosspro.core.viewModel.homeVM.HomeViewModel

import com.policyboss.policybosspro.databinding.ActivitySalesMaterialBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.utils.showSnackbar
import com.webengage.sdk.android.WebEngage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SalesMaterialActivity : BaseActivity() {

    private lateinit var binding: ActivitySalesMaterialBinding

    @Inject
    lateinit var prefsManager: PolicyBossPrefsManager

    lateinit var companyLst: ArrayList<CompanyEntity>

    private val viewModel by viewModels<HomeViewModel>()


    override fun onStart() {
        super.onStart()
        val weAnalytics = WebEngage.get().analytics()
        weAnalytics.screenNavigated("SalesMaterial Screen")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.apply {

            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle("Sales Material")
        }

        initialize()

        viewModel.getSalesProducts()
//
//
       observeResponse()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Call finish() to close the activity
                this@SalesMaterialActivity.finish()
            }
        })
    }

   private fun initialize(){

       companyLst = ArrayList()

//       binding.rvSalesMaterial.apply {
//           layoutManager = LinearLayoutManager(this@SalesMaterialActivity)
//           adapter = dashboardAdapter
//           setHasFixedSize(true)
//           setItemViewCacheSize(20)
//       }
   }

    private fun observeResponse() {

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {

                   viewModel.SalesMaterialResponse.collect{ event->

                       event.contentIfNotHandled?.let {

                           when(it){
                               is APIState.Empty -> {
                                   hideLoading()
                               }
                               is APIState.Failure -> {
                                   hideLoading()
                                   this@SalesMaterialActivity.showSnackbar(binding.root, it.errorMessage)
                                   Log.d(Constant.TAG,it.errorMessage.toString())
                               }
                               is APIState.Loading -> {
                                   displayLoadingWithText()
                               }
                               is APIState.Success -> {

                                   hideLoading()

                                   showAlert("Success")
                               }
                           }


                       }


                   }
                }
            }
        }
    }


            override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Finish the activity when the Up button is pressed
                this@SalesMaterialActivity.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }





}