package com.policyboss.policybosspro.core.api

import com.policyboss.policybosspro.core.response.master.dynamicDashboard.MenuMasterResponse
import com.policyboss.policybosspro.core.response.master.userConstant.UserConstantResponse
import com.policyboss.policybosspro.utils.Constant
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface poilcyBossProHomeApi {

    /****************************************************************************************
     *   Master API
     *************************************************************************************/

    // note : UserConstantResponse?: This means that the UserConstantResponse object itself can be null. You may receive either a UserConstantResponse or null.
    //Response<UserConstantResponse?>?: This adds an additional layer of nullability. It means that the entire Response object can be null, and within it, the UserConstantResponse can also be null.
    @Headers("token:" + Constant.token)
    @POST("/quote/Postfm/user-constant-pb")
    suspend fun getUserConstant(@Body body: HashMap<String,String>): Response<UserConstantResponse?>?

    @Headers("token:" + Constant.token)
    @POST("/quote/Postfm/get-dynamic-app-pb")
    suspend fun getDynamicDashboardMenu(@Body body: HashMap<String,String>): Response<MenuMasterResponse?>?

}