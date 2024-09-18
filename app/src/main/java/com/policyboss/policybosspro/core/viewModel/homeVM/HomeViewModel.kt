package com.policyboss.policybosspro.core.viewModel.homeVM

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.repository.homeRepository.HomeRepository
import com.policyboss.policybosspro.core.response.master.MasterDataCombine
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

    private val homeRepository: HomeRepository,
    private val prefManager: PolicyBossPrefsManager,
): ViewModel() {


    // StateFlow to manage loader state

    private val _masterState = MutableStateFlow<APIState<MasterDataCombine>>(APIState.Empty())
    val masterState: StateFlow<APIState<MasterDataCombine>> = _masterState



    fun getMasterData() = viewModelScope.launch {



        val body = hashMapOf(
            "app_version" to prefManager.getAppVersion(),
            "device_code" to prefManager.getDeviceID(),
            "ssid" to prefManager.getSSID(),
            "fbaid" to prefManager.getFBAID()
        )

        _masterState.value = APIState.Loading()

        try {
            coroutineScope {
                // Run both API calls concurrently
                val userConstantDeferred = async { homeRepository.getUserConstant(body) }
                val dynamicDashboardDeferred = async { homeRepository.getDynamicDashboardMenu(body) }


                val userConstantResponse = userConstantDeferred.await()
                val dynamicDashboardResponse = dynamicDashboardDeferred.await()

                // Check if both responses are successful
                if (userConstantResponse?.isSuccessful == true &&
                    dynamicDashboardResponse?.isSuccessful == true &&
                    userConstantResponse.body() != null  &&
                    dynamicDashboardResponse.body() != null
                    ) {

                    //for Success stae hold both data // no need actually
                    _masterState.value = APIState.Success(
                        MasterDataCombine(
                            userConstant = userConstantResponse.body(),
                            menuMaster = dynamicDashboardResponse.body()
                        )
                    )
                  // Mark :- storeData in Prference
                    userConstantResponse.body()?.let { prefManager.saveUserConstantResponse(it) }

                    dynamicDashboardResponse.body()?.let {
                        prefManager.storeMenuDashboard(it)
                    }


                } else {
                    _masterState.value = APIState.Failure(errorMessage = Constant.MasterData)
                }
            }
        } catch (e: Exception) {
            Log.e(Constant.TAG, "Error occurred: ${e.message}")
            _masterState.value = APIState.Failure("Error occurred: ${e.message}")

        }

    }

    fun getUserConstant(appVersion: String, deviceCode: String) = viewModelScope.launch {

        var body = HashMap<String, String>()
        body.put("app_version", appVersion)
        body.put("device_code", deviceCode)
        body.put("ssid", prefManager.getSSID())
        body.put("fbaid", prefManager.getFBAID())



        try {
            // Concurrent API calls
            val UserConstatnDeferred = async { homeRepository.getUserConstant(body) }

            val UserConstatnResponse = UserConstatnDeferred.await()

            if (UserConstatnResponse?.isSuccessful() == true) {

                Log.d(Constant.TAG, "User Constant Success: ${UserConstatnResponse.message()}")
            }else{
                Log.d(Constant.TAG, "Error occurred at User Constant : ${UserConstatnResponse?.message()}")
            }

        } catch (e: Exception) {
            Log.e(Constant.TAG, "Error occurred: ${e.message}")
        }

    }

    fun getDynamicDashboardMenu(appVersion: String, deviceCode: String) = viewModelScope.launch {

        var body = HashMap<String, String>()
        body.put("app_version", appVersion)
        body.put("device_code", deviceCode)
        body.put("ssid",prefManager.getSSID())
        body.put("fbaid", prefManager.getFBAID())



        try {
            // Concurrent API calls
            val DynamicDashboardDeferred = async { homeRepository.getDynamicDashboardMenu(body) }

            val DynamicDashboardResponse = DynamicDashboardDeferred.await()

            if (DynamicDashboardResponse?.isSuccessful() == true) {

                Log.d(Constant.TAG, "Dynamic DashboardSuccess: ${DynamicDashboardResponse.message()}")
            }else{
                Log.d(Constant.TAG, "Error occurred Dynamic Dashboard: ${DynamicDashboardResponse?.message()}")
            }

        } catch (e: Exception) {
            Log.e(Constant.TAG, "Error occurred: ${e.message}")
        }

    }

}