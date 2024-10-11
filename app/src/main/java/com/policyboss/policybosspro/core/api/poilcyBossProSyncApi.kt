package com.policyboss.policybosspro.core.api

import com.policyboss.policybosspro.core.requestbuilder.ContactLeadRequestEntity
import com.policyboss.policybosspro.core.requestbuilder.syncContact.CallLogRequestEntity
import com.policyboss.policybosspro.core.requestbuilder.syncContact.SaveCheckboxRequestEntity
import com.policyboss.policybosspro.core.response.ContactLeadResponse
import com.policyboss.policybosspro.core.response.authToken.OauthTokenResponse
import com.policyboss.policybosspro.core.response.doc.DocumentResponse
import com.policyboss.policybosspro.core.response.forgotPwd.ForgotResponse
import com.policyboss.policybosspro.core.response.horizonResponse.CheckboxsaveResponse
import com.policyboss.policybosspro.core.response.horizonResponse.Horizon_sync_contact_agree_Response
import com.policyboss.policybosspro.core.response.login.AuthLoginResponse
import com.policyboss.policybosspro.core.response.login.DevicetokenResponse
import com.policyboss.policybosspro.core.response.login.LoginNewResponse_DSAS_Horizon
import com.policyboss.policybosspro.core.response.login.OtpLoginResponse
import com.policyboss.policybosspro.core.response.login.OtpVerifyResponse
import com.policyboss.policybosspro.core.response.login.UserNewSignUpResponse
import com.policyboss.policybosspro.core.response.master.dynamicDashboard.MenuMasterResponse
import com.policyboss.policybosspro.core.response.master.userConstant.UserConstantResponse
import com.policyboss.policybosspro.core.response.syncContact.ContactLogResponse
import com.policyboss.policybosspro.utils.Constant
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Url

interface poilcyBossProSyncApi {


    // region Save Contact
//    @Headers("token:" + Constant.token)
//    @POST("/sync_contacts/contact_entry")
    //endregion

   // Save Contact
    @POST()
    suspend fun saveContactLead(@Url url: String, @Body body : ContactLeadRequestEntity): Response<ContactLeadResponse?>?


    // region Save CallLog
//    @Headers("token:" + Constant.token)
//    @POST("/sync_contacts/contact_call_history")
    //endregion

    //Save CallLog
    @POST()
    suspend fun saveCallLog(@Url url: String, @Body body : CallLogRequestEntity): Response<ContactLogResponse?>?


//    @Headers("token:" + Constant.token)
//    @POST("postservicecall/sync_contacts/online_agreement")

    @POST()
    suspend fun savecheckboxdetails(@Url url: String, @Body body : SaveCheckboxRequestEntity): Response<CheckboxsaveResponse?>?



    @Headers("token:" + Constant.token)
    @POST()
    suspend fun saveDeviceDetails1(@Url url: String, @Body body : HashMap<String,String> ): Response<ContactLogResponse>

    @Headers("token:" + Constant.token)
    @POST("/app_visitor/save_device_details")
    suspend fun saveDeviceDetails( @Body body : HashMap<String,String> ): Response<ContactLogResponse>


    @Headers("token:" + Constant.token)
    @POST("/auth_tokens/generate_web_auth_token")
    suspend fun getOauthToken( @Body body : HashMap<String,String> ): Response<OauthTokenResponse>


    //For Multipart Sending Photos from Contact List
    @Headers("token:" + Constant.token)
    @Multipart
    //@POST()
    @POST("/quote/Postfm_fileupload/upload-doc")
    fun uploadContactsPhotoDoc(

        //@Url url: String,
        @Part doc: MultipartBody.Part,
        @PartMap partMap: Map<String, String>
    ): Response<DocumentResponse>

    // ***************** *******************************

//    @POST
//    suspend fun getPBAttendance(@Url strUrl : String, @Body body : pbAttendRequestEntity ): Response<pbAttendResponse>


    @GET()
    suspend fun getSyncHorizonDetails(@Url url: String): Response<Horizon_sync_contact_agree_Response>



}