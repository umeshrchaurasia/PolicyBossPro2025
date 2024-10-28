package com.policyboss.policybosspro.core.viewModel.contactusVM


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.Event
import com.policyboss.policybosspro.core.repository.appRepository.AppRepository
import com.policyboss.policybosspro.core.response.contactUs.ContactUsResponse
import com.policyboss.policybosspro.core.response.notification.NotificationResponse
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactUsViewModel @Inject constructor(

    private val appRepository: AppRepository,
    private val prefManager: PolicyBossPrefsManager,
): ViewModel() {



    //region Decleration ofNotification Data
    private val contactUsStateFlow : MutableStateFlow<Event<APIState<ContactUsResponse>>> = MutableStateFlow(
        Event(APIState.Empty())
    )
    val ContactUSResponse: StateFlow<Event<APIState<ContactUsResponse>>>
        get() = contactUsStateFlow



    fun getContactList() = viewModelScope.launch {

        var body = HashMap<String, String>()
        body.put("fbaid", prefManager.getFBAID())
        body.put("ssid",prefManager.getSSID())
        body.put("app_version", prefManager.getAppVersion())
        body.put("device_code", prefManager.getDeviceID())



        contactUsStateFlow.value =  Event(APIState.Loading())


        appRepository.getContactUs(body)
            .catch {
                contactUsStateFlow.value = Event(APIState.Failure(it.message ?: Constant.Fail))

            }.collect{ data ->

                if(data.isSuccessful){

                    if(data.body()?.StatusNo?:1 == 0){

                        contactUsStateFlow.value = Event( APIState.Success(data = data.body()))
                    }else{
                        contactUsStateFlow.value = Event(APIState.Failure(errorMessage = data.body()?.Message ?: Constant.ErrorMessage))
                    }

                }else{
                    contactUsStateFlow.value = Event(APIState.Failure(errorMessage = Constant.SeverUnavaiable))
                }

            }


    }




}