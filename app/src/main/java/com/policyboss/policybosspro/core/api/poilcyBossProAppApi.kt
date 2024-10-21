package com.policyboss.policybosspro.core.api

import com.policyboss.policybosspro.core.response.doc.DocumentResponse
import com.policyboss.policybosspro.core.response.profile.MyAcctDtlResponse
import com.policyboss.policybosspro.core.response.salesMaterial.SalesMaterialProductDetailsResponse

import com.policyboss.policybosspro.core.response.salesMaterial.SalesMaterialResponse
import com.policyboss.policybosspro.core.response.salesMaterial.SalesClickResponse
import com.policyboss.policybosspro.utils.Constant
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

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


    /****************************************************************************************
     *   Profile API
     *************************************************************************************/

    @Headers("token:" +  Constant.token)
    @POST("/quote/Postfm/get-my-account")
    suspend fun getProfileDetail( @Body body: HashMap<String,String>): Response<MyAcctDtlResponse?>



    @Headers("token:" + Constant.token)
    @Multipart
    @POST("/quote/Postfm_fileupload/upload-doc")
    suspend fun uploadDocument(
        @Part doc: MultipartBody.Part,
        @PartMap partMap: Map<String, String>
    ): Response<DocumentResponse>

}