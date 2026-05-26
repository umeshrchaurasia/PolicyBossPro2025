package com.policyboss.policybosspro.core.viewModel.homeVM

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.Event
import com.policyboss.policybosspro.core.model.homeDashboard.DashboardMultiLangEntity
import com.policyboss.policybosspro.core.repository.homeRepository.HomeRepository
import com.policyboss.policybosspro.core.requestbuilder.qrScanner.QRCodePRERequest
import com.policyboss.policybosspro.core.requestbuilder.qrScanner.QRCodeRequest
import com.policyboss.policybosspro.core.response.authToken.OauthTokenResponse
import com.policyboss.policybosspro.core.response.home.ProductURLShareEntity
import com.policyboss.policybosspro.core.response.home.UserCallingResponse
import com.policyboss.policybosspro.core.response.master.MasterDataCombine
import com.policyboss.policybosspro.core.response.master.userConstant.UserConstantResponse
import com.policyboss.policybosspro.core.response.qrScanner.QRCodeResponse
import com.policyboss.policybosspro.core.response.salesMaterial.SalesMaterialProductDetailsResponse

import com.policyboss.policybosspro.core.response.salesMaterial.SalesMaterialResponse
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

    private val homeRepository: HomeRepository,
    private val prefManager: PolicyBossPrefsManager,
): ViewModel() {


    //region Declaeration of Master Data State
    private val _masterState = MutableStateFlow<APIState<MasterDataCombine>>(APIState.Empty())
    val masterState: StateFlow<APIState<MasterDataCombine>> = _masterState
    //endregion

    //region Decleration OF Share DashBoard Product State
    private val productShareMutableFlow: MutableStateFlow<Event<APIState<ProductURLShareEntity>>> =
        MutableStateFlow(Event(APIState.Empty()))

    val productShareResponse: StateFlow<Event<APIState<ProductURLShareEntity>>>
        get() = productShareMutableFlow

    //endregion

    //region Decleration OF User Team Details and Custom care
    private val userCallingDtlMutableFlow: MutableStateFlow<Event<APIState<UserCallingResponse>>> =
        MutableStateFlow(Event(APIState.Empty()))

    val userCallingDtlResponse: StateFlow<Event<APIState<UserCallingResponse>>>
        get() = userCallingDtlMutableFlow

    //endregion


    //region Declaeration of AuthToken State
    private val oauthMutuableStateFlow : MutableStateFlow<APIState<OauthTokenResponse>> = MutableStateFlow(APIState.Empty())

    // data is collected in OauthStateFlow variable, we have to get from here
    val OauthStateFlow : StateFlow<APIState<OauthTokenResponse>>
        get() = oauthMutuableStateFlow

    //endregion


    //region Declaeration of UserConstant  State for SyncContact Check
    private val _userConstantStateFlow = MutableStateFlow<Event<APIState<UserConstantResponse>>>(Event(APIState.Empty()))
    val userConstantDataStateFlow: StateFlow<Event<APIState<UserConstantResponse>>> = _userConstantStateFlow

    //endregion

    //region set  CurrentDashboard Entity for Sharing
    private var _currentDashboardEntity: DashboardMultiLangEntity? = null

    // Setter method
    fun setCurrentDashboardShareEntity(shareEntity: DashboardMultiLangEntity) {
        _currentDashboardEntity = shareEntity
    }

    // Getter method
    fun getCurrentDashboardSharedEntity(): DashboardMultiLangEntity? {
        return _currentDashboardEntity
    }

    fun ShareTitle() = getCurrentDashboardSharedEntity()?.title?:""

    //endregion


    // =========================================================
    // QR LOGIN STATE
    // =========================================================

    private val qrLoginMutableFlow :
            MutableStateFlow<Event<APIState<QRCodeResponse>>> =
        MutableStateFlow(
            Event(APIState.Empty())
        )

    val qrLoginStateFlow :
            StateFlow<Event<APIState<QRCodeResponse>>>
        get() = qrLoginMutableFlow




    //region  Master Data
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
                val horizonDetailDeferred = async { homeRepository.getSyncDetails(prefManager.getSSID().toInt()) }


                val userConstantResponse = userConstantDeferred.await()
                val dynamicDashboardResponse = dynamicDashboardDeferred.await()
                 val horizonDetailResponse = horizonDetailDeferred.await()

                // Check if both responses are successful
                if (userConstantResponse?.isSuccessful == true &&
                    dynamicDashboardResponse?.isSuccessful == true &&


                    userConstantResponse.body() != null  &&
                    dynamicDashboardResponse.body() != null &&
                    horizonDetailResponse.body() != null
                    ) {

                    //for Success state hold both data // no need actually
                    _masterState.value = APIState.Success(
                        MasterDataCombine(
                            userConstant = userConstantResponse.body(),
                            menuMaster = dynamicDashboardResponse.body(),
                            horizonDetail = horizonDetailResponse?.body()
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
            //_masterState.value = APIState.Failure("Error occurred: ${e.message}")
            _masterState.value = APIState.Failure(errorMessage = Constant.MasterData)

        }

    }

    //endregion

    fun verifyQRScannerQR(
        token: String
    ) {

        viewModelScope.launch {

            try {

                val request = QRCodePRERequest(

                    token_id = token,

                    secret_key = Constant.SECRET_KEY,

                    client_key = Constant.CLIENT_KEY,

                    status = "SCANNED",

                    update_by = "token"
                )

                val response =
                    homeRepository.verifyQRScannerQR(
                        request = request
                    )

                Log.d(
                    Constant.TAG,
                    "verifyQRScannerQR Success"
                )

            } catch (e: Exception) {

                Log.e(
                    Constant.TAG,
                    "verifyQRScannerQR Error: ${e.message}"
                )
            }
        }
    }

    fun verifyQRLogin(
        token: String
    ) = viewModelScope.launch {

        val request = QRCodeRequest(

            status = "VERIFIED",

            ss_id = prefManager.getSSID(),

            token_id = token,

            update_by = "token",

            secret_key = Constant.SECRET_KEY,

            client_key = Constant.CLIENT_KEY
        )

        qrLoginMutableFlow.value =
            Event(APIState.Loading())

        homeRepository.verifyQRLogin(

            token = token,

            request = request

        ).catch {

            qrLoginMutableFlow.value =
                Event(
                    APIState.Failure(
                        it.message ?: Constant.Fail
                    )
                )

        }.collect {

            if (it.isSuccessful) {

                if (
                    it.body() != null &&
                    it.body()?.Status.equals(
                        "SUCCESS",
                        true
                    )
                ) {

                    qrLoginMutableFlow.value =
                        Event(
                            APIState.Success(
                                it.body()
                            )
                        )

                } else {

                    qrLoginMutableFlow.value =
                        Event(
                            APIState.Failure(
                                it.body()?.Msg
                                    ?: Constant.ErrorMessage
                            )
                        )
                }

            } else {

                qrLoginMutableFlow.value =
                    Event(
                        APIState.Failure(
                            it.message()
                        )
                    )
            }
        }
    }


    //regionDynamic List filter Logic : After Api called
    fun getInsurProductLangList(): List<DashboardMultiLangEntity> {
        val dashboardEntities = mutableListOf<DashboardMultiLangEntity>()

        // Retrieve the dashboard data from prefManager {api :get-dynamic-app-pb }
        val dashBoardItemEntities = prefManager.getMenuDashBoard()?.MasterData?.Dashboard

        dashBoardItemEntities?.let { items ->
            items.filter { it.dashboard_type == 1  && it.isActive == 1 }
                .forEach { dashBoardItemEntity ->
                    val dashboardEntity = DashboardMultiLangEntity(
                        category = "INSURANCE",
                        sequence = dashBoardItemEntity.ProdId.toInt(),
                        productId = dashBoardItemEntity.ProdId.toInt(),
                        menuName = dashBoardItemEntity.menuname,
                        description = dashBoardItemEntity.description,
                        iconResId = -1,  // Assuming no local resource, replace if needed


                        productName = dashBoardItemEntity.menuname,
                        productDetails =   dashBoardItemEntity.description,

                        titleKey = "Insurance",
                        descriptionKey = "",
                        isNewPrdClickable = dashBoardItemEntity.IsNewprdClickable,
                        serverIcon = dashBoardItemEntity.iconimage,
                        link = dashBoardItemEntity.link,

                        productNameFontColor = dashBoardItemEntity.ProductNameFontColor,
                        productDetailsFontColor = dashBoardItemEntity.ProductDetailsFontColor,
                        productBackgroundColor = dashBoardItemEntity.ProductBackgroundColor,

                        isExclusive = dashBoardItemEntity.IsExclusive,
                        isSharable = dashBoardItemEntity.IsSharable,
                        title = dashBoardItemEntity.title,
                        info = dashBoardItemEntity.infourl?:"",
                        popupmsg = dashBoardItemEntity.popupinfo
                    )
                    dashboardEntities.add(dashboardEntity)
                }
        }

        return dashboardEntities
    }



    //endregion

    //region  Share DashBoard Product
    fun getProductShareURL(product_id: String, sub_fba_id: String) = viewModelScope.launch {


        val body = hashMapOf(

            "fba_id" to prefManager.getFBAID(),
            "ss_id" to prefManager.getSSID(),
            "product_id" to product_id,
            "sub_fba_id" to sub_fba_id,


        )

        productShareMutableFlow.value = Event(APIState.Loading())

        homeRepository.getProductShareURL(body)
            .catch {
                productShareMutableFlow.value =  Event(APIState.Failure(it.message ?: Constant.Fail))
            }.collect{
                if (it.isSuccessful) {
                    if (it.body() != null && it.body()?.StatusNo == 0) {

                        productShareMutableFlow.value =  Event(APIState.Success(it.body()?.MasterData))
                    } else {
                        productShareMutableFlow.value =
                            Event(APIState.Failure(it.body()?.Message?: Constant.ErrorMessage ))
                    }
                } else {
                    productShareMutableFlow.value =
                        Event(APIState.Failure(it.message()))
                }
            }


    }

    //endregion


    //region  User Team Details and Custom care
    fun getUserCallingDetail() = viewModelScope.launch {


        val body = hashMapOf(

            "fbaid" to prefManager.getFBAID(),
            "ssid" to prefManager.getSSID(),
            "device_code" to prefManager.getDeviceID(),
            "app_version" to prefManager.getAppVersion(),

            )

        userCallingDtlMutableFlow.value = Event(APIState.Loading())

        homeRepository.getUserCallingDetail(body)
            .catch {
                userCallingDtlMutableFlow.value =  Event(APIState.Failure(it.message ?: Constant.Fail))
            }.collect{
                if (it.isSuccessful) {
                    if (it.body() != null && it.body()?.StatusNo == 0) {

                        userCallingDtlMutableFlow.value =  Event(APIState.Success(it.body()))
                    } else {
                        userCallingDtlMutableFlow.value =
                            Event(APIState.Failure(it.body()?.Message?: Constant.ErrorMessage ))
                    }
                } else {
                    userCallingDtlMutableFlow.value =
                        Event(APIState.Failure(it.message()))
                }
            }


    }

    //endregion


    //region AuthToken
    fun getAuthToken(ss_id : String, deviceID : String,app_version : String,fbaid : String) = viewModelScope.launch {


        var body = HashMap<String,String>()
        body.put("ss_id",ss_id)
        body.put("device_id",deviceID)
        body.put("user_agent","")
        body.put("app_version",app_version)
        body.put("fbaid",fbaid)

        oauthMutuableStateFlow.value = APIState.Loading()


        homeRepository.getAuthToken(body)
            .catch {
                oauthMutuableStateFlow.value =  APIState.Failure(it.message ?: Constant.Fail)

            }.collect{ data ->

                if(data.isSuccessful){

                    if(data.body()?.Status?.uppercase().equals("SUCCESS")){
                        oauthMutuableStateFlow.value = APIState.Success(data = data.body())
                    }else{
                        oauthMutuableStateFlow.value = APIState.Failure(errorMessage = data.body()?.Msg ?: Constant.ErrorMessage)
                    }

                }else{
                    oauthMutuableStateFlow.value = APIState.Failure(errorMessage = Constant.SeverUnavaiable)
                }

            }


    }

    //endregion


    // Now, update your getUserConstant() function to wrap the result in an Event
    fun getUserConstant() = viewModelScope.launch {
        // Show loading state
        _userConstantStateFlow.value = Event(APIState.Loading())

        val body = hashMapOf(
            "app_version" to prefManager.getAppVersion(),
            "device_code" to prefManager.getDeviceID(),
            "ssid" to prefManager.getSSID(),
            "fbaid" to prefManager.getFBAID()
        )

        try {
            val response = homeRepository.getUserConstant(body)// Assuming this is now a suspend fun in repo

            if (response != null) {
                if (response.isSuccessful && response.body()?.StatusNo == 0) {
                    response.body()?.let { prefManager.saveUserConstantResponse(it) }
                    // Emit Success Event
                    _userConstantStateFlow.value = Event(APIState.Success(response.body()!!))
                } else {
                    // Emit Failure Event
                    _userConstantStateFlow.value = Event(APIState.Failure("Failed to get user constants"))
                }
            }
        } catch (e: Exception) {
            // Emit Failure Event
            _userConstantStateFlow.value = Event(APIState.Failure(e.message ?: "Something went wrong"))
        }
    }

//userClickActionOnNotification


    fun userClickActionOnNotification(notifyReqID : String, appVersion: String, deviceCode: String) = viewModelScope.launch {

        var body = HashMap<String, String>()
        body.put("UserNotificationRequestId", notifyReqID)
        body.put("app_version", appVersion)
        body.put("device_code", deviceCode)
        body.put("ssid", prefManager.getSSID())
        body.put("fbaid", prefManager.getFBAID())





        try {
            // Concurrent API calls
            val apiDeferred = async { homeRepository.userClickActionOnNotification(body) }

            val apiResponse = apiDeferred.await()

            if (apiResponse?.isSuccessful() == true) {

                Log.d(Constant.TAG, "User Click on Notification: ${apiResponse.message()}")
            }else{
                Log.d(Constant.TAG, "Error occurred at User Click on Notification : ${apiResponse?.message()}")
            }

        } catch (e: Exception) {
            Log.e(Constant.TAG, "Error occurred: ${e.message}")
        }

    }

    //region Not in Used

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

    //endregion



}