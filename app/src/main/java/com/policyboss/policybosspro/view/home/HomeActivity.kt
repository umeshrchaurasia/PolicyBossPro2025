package com.policyboss.policybosspro.view.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.policyboss.policybosspro.BaseActivity
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.viewModel.homeVM.HomeViewModel
import com.policyboss.policybosspro.core.viewModel.loginVM.LoginViewModel
import com.policyboss.policybosspro.databinding.ActivityHomeBinding
import com.policyboss.policybosspro.databinding.ActivityLoginBinding
import com.policyboss.policybosspro.utils.showAlert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val vewModel by viewModels<HomeViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeMasterState()

        //Called Master Data ie UserConstant and Dynamic Dashb oard Parallel
        vewModel.getMasterData()

    }


    private fun observeMasterState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {

                vewModel.masterState.collectLatest { state ->
                    when (state) {

                        is APIState.Loading -> {
                            displayLoadingWithText()
                        }
                        is APIState.Success -> {
                            hideLoading()
                            showAlert("Master data done")
                        }
                        is APIState.Empty -> {
                            hideLoading()
                        }
                        is APIState.Failure -> {

                            hideLoading()
                        }
                    }
                }
            }
        }
    }
}