package com.policyboss.policybosspro.core.api

import com.policyboss.policybosspro.core.response.authToken.OauthTokenResponse
import com.policyboss.policybosspro.core.response.home.ProductURLShareResponse
import com.policyboss.policybosspro.core.response.horizonResponse.horizonSyncDetails.HorizonsyncDetailsResponse
import com.policyboss.policybosspro.core.response.master.dynamicDashboard.MenuMasterResponse
import com.policyboss.policybosspro.core.response.master.userConstant.UserConstantResponse
import com.policyboss.policybosspro.core.response.salesMaterial.SalesMaterialProductDetailsResponse

import com.policyboss.policybosspro.core.response.salesMaterial.SalesMaterialResponse
import com.policyboss.policybosspro.utils.Constant
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

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


    /****************************************************************************************
     *   Home Page API
     *************************************************************************************/

    @Headers("token:" + Constant.token)
    @POST("/quote/Postfm/GetShareUrl")
    suspend fun getProductShareURL(@Body body: HashMap<String,String>): Response<ProductURLShareResponse>


//    @Headers("token:" + Constant.token)
//    @POST("/quote/Postfm/Getusersignup")
//    fun getusersignup(@Body body: HashMap<String,String>): Response<UsersignupResponse?>


    @GET("/posps/dsas/view/{Ss_Id}")
    suspend fun getsyncDetailshorizondetail(@Path("Ss_Id") ssId: Int): Response<HorizonsyncDetailsResponse?>



    /****************************************************************************************
     *   App Code API
     *************************************************************************************/

    @Headers("token:" +  Constant.token)
    @POST("/auth_tokens/generate_web_auth_token")
    suspend fun getOauthToken( @Body body : HashMap<String,String> ): Response<OauthTokenResponse>


    /****************************************************************************************
     *   Sales Material API
     *************************************************************************************/

    @Headers("token:" +  Constant.token)
    @POST("/quote/Postfm/sales-material-product-pb")
    suspend fun getSalesProducts( @Body body: HashMap<String,String>): Response<SalesMaterialResponse>


    @Headers("token:" +  Constant.token)
    @POST("/quote/Postfm/sales-material-product-details-pb")
    suspend fun getSalesProductDetails( @Body body: HashMap<String,String>): Response<SalesMaterialProductDetailsResponse>

}