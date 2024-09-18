package com.policyboss.policybosspro.core.repository.homeRepository

import com.policyboss.policybosspro.core.api.poilcyBossProHomeApi
import com.policyboss.policybosspro.core.response.master.dynamicDashboard.MenuMasterResponse
import com.policyboss.policybosspro.core.response.master.userConstant.UserConstantResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private  val apiService : poilcyBossProHomeApi
){


    suspend fun getUserConstant(body : HashMap<String,String> ): Response<UserConstantResponse?>? {


        return apiService.getUserConstant(body)

    }


    suspend fun getDynamicDashboardMenu(body : HashMap<String,String> ): Response<MenuMasterResponse?>? {


        return apiService.getDynamicDashboardMenu(body)

    }


}