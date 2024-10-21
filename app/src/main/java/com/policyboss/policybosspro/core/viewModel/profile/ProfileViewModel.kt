package com.policyboss.policybosspro.core.viewModel.profile


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.Event
import com.policyboss.policybosspro.core.repository.appRepository.AppRepository
import com.policyboss.policybosspro.core.response.doc.DocumentResponse
import com.policyboss.policybosspro.core.response.profile.MyAcctDtlResponse
import com.policyboss.policybosspro.core.response.salesMaterial.SalesMaterialResponse
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(

    @ApplicationContext val context: Context,
    private val appRepository: AppRepository,
    private val prefManager: PolicyBossPrefsManager,
): ViewModel() {

    //region Decleration of SalesMaterial State
    private val profileDtlStateFlow : MutableStateFlow<Event<APIState<MyAcctDtlResponse>>> = MutableStateFlow(
        Event(APIState.Empty())
    )
    val ProfileDtlResponse: StateFlow<Event<APIState<MyAcctDtlResponse>>>
        get() = profileDtlStateFlow



//    private val salesMaterialDtlStateFlow : MutableStateFlow<Event<APIState<SalesMaterialProductDetailsResponse>>> = MutableStateFlow(
//        Event(APIState.Empty())
//    )
//    val SalesMaterialDtlResponse: StateFlow<Event<APIState<SalesMaterialProductDetailsResponse>>>
//        get() = salesMaterialDtlStateFlow


    private val _uploadDocumentStateFlow = MutableStateFlow<Event<APIState<DocumentResponse>>>(Event(APIState.Empty()))
    val uploadDocumentResponse: StateFlow<Event<APIState<DocumentResponse>>>
        get() = _uploadDocumentStateFlow

    fun getProfileDetails() = viewModelScope.launch {


        var body = HashMap<String,String>()

        body.put("FBAID",prefManager.getFBAID())


        profileDtlStateFlow.value =  Event(APIState.Loading())


        appRepository.getProfileDetail(body)
            .catch {
                profileDtlStateFlow.value = Event(APIState.Failure(it.message ?: Constant.Fail))

            }.collect{ data ->

                if(data.isSuccessful){

                    if(data.body()?.StatusNo?:1 == 0){

                        profileDtlStateFlow.value = Event( APIState.Success(data = data.body()))
                    }else{
                        profileDtlStateFlow.value = Event(APIState.Failure(errorMessage = data.body()?.Message ?: Constant.ErrorMessage))
                    }

                }else{
                    profileDtlStateFlow.value = Event(APIState.Failure(errorMessage = Constant.SeverUnavaiable))
                }

            }


    }



    fun uploadDocument(document: MultipartBody.Part, body: HashMap<String, String>) {

        viewModelScope.launch {
            appRepository.uploadDocument(document, body)
                .onStart { _uploadDocumentStateFlow.value = Event(APIState.Loading()) }
                .catch { exception -> _uploadDocumentStateFlow.value = Event(APIState.Failure(exception.message ?: Constant.ErrorMessage)) }
                .collect { response ->
                    if (response.isSuccessful && response.body()?.StatusNo == 0) {
                        _uploadDocumentStateFlow.value = Event(APIState.Success(response.body()!!))
                    } else {
                        _uploadDocumentStateFlow.value = Event(APIState.Failure(response.body()?.Message ?: "Upload failed"))
                    }
                }
        }
    }





}