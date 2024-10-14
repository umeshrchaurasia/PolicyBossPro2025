package com.policyboss.policybosspro.view.appCode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.viewModel.homeVM.HomeViewModel
import com.policyboss.policybosspro.databinding.ActivityAppCodeBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utility.Utility
import com.webengage.sdk.android.WebEngage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppCodeActivity : AppCompatActivity() {

    lateinit var binding : ActivityAppCodeBinding

    private val viewModel by viewModels<HomeViewModel>()

    @Inject
    lateinit var prefManager: PolicyBossPrefsManager


    override fun onStart() {
        super.onStart()
        val weAnalytics = WebEngage.get().analytics()
        weAnalytics.screenNavigated("AppCode Screen")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAppCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        init()
        setListner()

        if (prefManager.getEmpData() != null) {
            if (prefManager.getSSID() != "0") {
                // calling API
                viewModel.getAuthToken(ss_id =prefManager.getSSID() , deviceID = Utility.getDeviceID(this@AppCodeActivity)
                    ,app_version=prefManager.getAppVersion(),fbaid=prefManager.getFBAID())
            }
        }


        // displaying the response which we get from above API
        observe()
    }

    private fun init(){


        binding.txtOauthData.text = ""
        //  loginResponseEntity = DBPersistanceController(this).getUserData()

    }

    private fun setListner(){

        binding.imgClose.setOnClickListener {

            this@AppCodeActivity.finish()

        }
    }




    private fun observe(){


        lifecycleScope.launch{

            repeatOnLifecycle(Lifecycle.State.STARTED){
                //collect date from flow  Variable
                viewModel.OauthStateFlow.collect{

                    when(it){

                        is APIState.Loading ->{


                            showAnimDialog()
                        }
                        is APIState.Success ->{

                            cancelAnimDialog()

                            if(it != null){
                                it.data?.let{
                                    binding.txtOauthData.visibility = View.VISIBLE
                                    binding.txtOauthData.text = it.Token?: ""

                                }
                            }


                        }

                        is APIState.Failure -> {
                            cancelAnimDialog()
                            binding.txtOauthData.text = ""
                            binding.txtError.text = it.errorMessage
                        }

                        is APIState.Empty ->{
                            cancelAnimDialog()
                        }
                    }


                }
            }
        }
    }

    fun showAnimDialog(){

        binding.imgLoader.visibility = View.VISIBLE
//        Glide.with(this@OauthTokenActivity).load<Any>(R.drawable.loading_gif)
//            .asGif()
//            .crossFade()
//            .into(binding.imgLoader)

    }

    fun cancelAnimDialog(){


        binding.imgLoader.visibility = View.GONE
    }
}