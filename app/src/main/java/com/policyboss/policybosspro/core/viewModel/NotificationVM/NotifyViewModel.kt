package com.policyboss.policybosspro.core.viewModel.NotificationVM


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.Event
import com.policyboss.policybosspro.core.repository.appRepository.AppRepository
import com.policyboss.policybosspro.core.response.doc.DocumentResponse
import com.policyboss.policybosspro.core.response.notification.NotificationResponse
import com.policyboss.policybosspro.core.response.profile.MyAcctDtlResponse
import com.policyboss.policybosspro.core.response.salesMaterial.SalesMaterialResponse
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class NotifyViewModel @Inject constructor(

    private val appRepository: AppRepository,
    private val prefManager: PolicyBossPrefsManager,
): ViewModel() {

   // Notification Counter
    private val _notificationCounter = MutableStateFlow(prefManager.getNotificationCounter())
    val notificationCounter: StateFlow<Int> = _notificationCounter


    //region Decleration ofNotification Data
    private val notificationDtlStateFlow : MutableStateFlow<Event<APIState<NotificationResponse>>> = MutableStateFlow(
        Event(APIState.Empty())
    )
    val NotificationDtlResponse: StateFlow<Event<APIState<NotificationResponse>>>
        get() = notificationDtlStateFlow



    fun geNotificationData() = viewModelScope.launch {



        var body = HashMap<String, String>()
        body.put("FBAID", prefManager.getFBAID())
        body.put("ssid",prefManager.getSSID())
        body.put("app_version", prefManager.getAppVersion())
        body.put("device_code", prefManager.getDeviceID())



        notificationDtlStateFlow.value =  Event(APIState.Loading())


        appRepository.getNotificationData(body)
            .catch {
                notificationDtlStateFlow.value = Event(APIState.Failure(it.message ?: Constant.Fail))

            }.collect{ data ->

                if(data.isSuccessful){

                    if(data.body()?.StatusNo?:1 == 0){

                        notificationDtlStateFlow.value = Event( APIState.Success(data = data.body()))
                    }else{
                        notificationDtlStateFlow.value = Event(APIState.Failure(errorMessage = data.body()?.Message ?: Constant.ErrorMessage))
                    }

                }else{
                    notificationDtlStateFlow.value = Event(APIState.Failure(errorMessage = Constant.SeverUnavaiable))
                }

            }


    }




}