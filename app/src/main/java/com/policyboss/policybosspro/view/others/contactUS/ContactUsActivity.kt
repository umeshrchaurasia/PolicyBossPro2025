package com.policyboss.policybosspro.view.others.contactUS

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.policyboss.policybosspro.BaseActivity
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.response.contactUs.ContactUsEntity
import com.policyboss.policybosspro.core.response.salesMaterial.CompanyEntity
import com.policyboss.policybosspro.core.response.salesMaterial.SalesMateriaProdEntity
import com.policyboss.policybosspro.core.viewModel.contactusVM.ContactUsViewModel
import com.policyboss.policybosspro.core.viewModel.salesMaterialVM.SalesMaterialViewNodel
import com.policyboss.policybosspro.databinding.ActivityContactUsBinding
import com.policyboss.policybosspro.databinding.ActivitySalesMaterialBinding
import com.policyboss.policybosspro.databinding.ContentContactUsBinding
import com.policyboss.policybosspro.databinding.ContentNotificationBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.view.salesMaterial.SalesDetailActivity
import com.policyboss.policybosspro.view.salesMaterial.adapter.SalesMaterialAdapter
import com.policyboss.policybosspro.webview.CommonWebViewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ContactUsActivity : BaseActivity() {

    private lateinit var binding: ActivityContactUsBinding

    // Initialize contentBinding for the included layout
    private lateinit var includedBinding: ContentContactUsBinding// For the included layout

    private lateinit var salesMaterialAdapter: SalesMaterialAdapter



    @Inject
    lateinit var prefsManager: PolicyBossPrefsManager


    private val viewModel by viewModels<ContactUsViewModel>()

    private lateinit var contactUsAdapter: ContactUsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.apply {

            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(Constant.ContactUsTitle)
        }
        includedBinding = ContentContactUsBinding.bind(binding.includeContactUs.root)

        applyInsets() // ✅ handle status + nav bar insets

        viewModel.getContactList()

        observeResponse()
    }

    private fun applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Push AppBar below status bar
            binding.appbar.setPadding(
                binding.appbar.paddingLeft,
                systemBars.top,
                binding.appbar.paddingRight,
                binding.appbar.paddingBottom
            )

            // Push content above nav bar
            includedBinding.root.setPadding(
                includedBinding.root.paddingLeft,
                includedBinding.root.paddingTop,
                includedBinding.root.paddingRight,
                systemBars.bottom
            )

            insets
        }
    }

    private fun setupContctUSAdapter( contactUslst: List<ContactUsEntity>) {


        contactUsAdapter = ContactUsAdapter(
            context = this,
            whatsNewEntities =  contactUslst,
            onSupportNoItemClick = ::onSupportListener,
            onEmailItemClick = ::EmailListener,
            onWebsiteClick = ::onWebsiteListener

            )
        includedBinding.rvContactUs.apply {
            layoutManager = LinearLayoutManager(this@ContactUsActivity)
            adapter = contactUsAdapter
            setHasFixedSize(true)
            setItemViewCacheSize(20)
        }
    }

    fun onSupportListener(entity: ContactUsEntity){

      dialNumber(mobNumber =  entity.PhoneNo)

    }
    fun EmailListener(entity: ContactUsEntity){


        composeEmail(
            address = entity.Email,
            subject = ""
        )
    }
    fun onWebsiteListener(entity: ContactUsEntity){

        val intent = Intent(this, CommonWebViewActivity::class.java).apply {
            putExtra("URL", "https://${entity.Website}")
            putExtra("NAME", "PolicyBoss Pro")
            putExtra("TITLE", entity.Header)
        }
        startActivity(intent)
    }

    private fun observeResponse() {

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {

                    viewModel.ContactUSResponse.collect{ event->

                        event.contentIfNotHandled?.let {

                            when(it){
                                is APIState.Empty -> {
                                    hideLoading()
                                }
                                is APIState.Failure -> {
                                    hideLoading()


                                    Log.d(Constant.TAG,it.errorMessage.toString())
                                }
                                is APIState.Loading -> {
                                    displayLoadingWithText()
                                }
                                is APIState.Success -> {

                                    hideLoading()

                                    it.data?.MasterData?.let { lstContactUs->

                                        setupContctUSAdapter(lstContactUs)
                                    }
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
                this@ContactUsActivity.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}