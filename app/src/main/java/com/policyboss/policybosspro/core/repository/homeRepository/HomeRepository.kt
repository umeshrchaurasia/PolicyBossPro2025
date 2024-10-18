package com.policyboss.policybosspro.core.repository.homeRepository

import com.policyboss.policybosspro.core.api.poilcyBossProHomeApi
import com.policyboss.policybosspro.core.response.home.ProductURLShareResponse
import com.policyboss.policybosspro.core.response.home.UsersignupResponse
import com.policyboss.policybosspro.core.response.horizonResponse.horizonSyncDetails.HorizonsyncDetailsResponse
import com.policyboss.policybosspro.core.response.master.dynamicDashboard.MenuMasterResponse
import com.policyboss.policybosspro.core.response.master.userConstant.UserConstantResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import retrofit2.http.Url
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private  val apiService : poilcyBossProHomeApi
){


    //region  Master API
    /****************************************************************************************
     *   Master Repository
     *************************************************************************************/

    suspend fun getUserConstant(body : HashMap<String,String> ): Response<UserConstantResponse?>? {


        return apiService.getUserConstant(body)

    }


    suspend fun getDynamicDashboardMenu(body : HashMap<String,String> ): Response<MenuMasterResponse?>? {


        return apiService.getDynamicDashboardMenu(body)

    }



    suspend fun getSyncDetails(ssId: Int): Response<HorizonsyncDetailsResponse?> {
        return apiService.getsyncDetailshorizondetail(ssId)
    }
    //*************************************************************************************/
    //endregion

    //region Share URL
    suspend fun getProductShareURL(body : HashMap<String,String> ) = flow {
        val response = apiService.getProductShareURL(body)

        emit(response)

    }.flowOn(Dispatchers.IO)

    //endregion

    //region  App Code API
    /****************************************************************************************
     *   App Code
     *************************************************************************************/

    suspend fun getAuthToken(body : HashMap<String,String>) = flow {


        val response = apiService.getOauthToken(body)
        emit(response)

    }.flowOn(Dispatchers.IO)

    //endregion



}