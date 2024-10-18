package com.policyboss.policybosspro.core.api

import com.policyboss.policybosspro.core.response.salesMaterial.SalesMaterialProductDetailsResponse

import com.policyboss.policybosspro.core.response.salesMaterial.SalesMaterialResponse
import com.policyboss.policybosspro.core.response.salesMaterial.SalesClickResponse
import com.policyboss.policybosspro.utils.Constant
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface poilcyBossProAppApi {


    /****************************************************************************************
     *   Sales Material API
     *************************************************************************************/

    @Headers("token:" +  Constant.token)
    @POST("/quote/Postfm/sales-material-product-pb")
    suspend fun getSalesProducts( @Body body: HashMap<String,String>): Response<SalesMaterialResponse>


    @Headers("token:" +  Constant.token)
    @POST("/quote/Postfm/sales-material-product-details-pb")
    suspend fun getSalesProductDetails( @Body body: HashMap<String,String>): Response<SalesMaterialProductDetailsResponse>


    @Headers("token:" +  Constant.token)
    @POST("/postservicecall/content_usage")
    suspend fun getSalesProductClick( @Body body: HashMap<String,String>): Response<SalesClickResponse?>?

}