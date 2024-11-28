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

interface poilcyBossProLoginApi {

    @POST()
    suspend fun saveContactLead(@Url url: String, @Body body : ContactLeadRequestEntity): Response<ContactLeadResponse>


    @POST()
    suspend fun saveCallLog(@Url url: String, @Body body : CallLogRequestEntity): Response<ContactLogResponse>

    @GET()
    suspend fun getHorizonDetails(@Url url: String): Response<Horizon_sync_contact_agree_Response>



    @POST()
    suspend fun savecheckboxdetails(@Url url: String, @Body body : SaveCheckboxRequestEntity): Response<CheckboxsaveResponse>

    @POST()
    suspend fun saveCallLogOld(@Url url: String, @Body body : CallLogRequestEntity): Call<ContactLogResponse>



    @POST()
    fun saveContactLeadOld(@Url url: String, @Body body : ContactLeadRequestEntity): Call<ContactLeadResponse>


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

    // *****************PB Attendance*******************************

//    @POST
//    suspend fun getPBAttendance(@Url strUrl : String, @Body body : pbAttendRequestEntity ): Response<pbAttendResponse>


    /****************************************************************************************
     *    Login Horizon API
     *************************************************************************************/


    @Headers("token:" + Constant.token)
    @POST("/Postfm/Getusersignup")
    suspend fun getusersignup(@Body body: HashMap<String,String>): Response<UserNewSignUpResponse?>


    @GET("posps/dsas/view/{userId}")
    suspend fun getLoginDsasHorizonDetails( @Path("userId") userId: String): Response<LoginNewResponse_DSAS_Horizon>

    @POST("postservicecall/otp_login")
    //005 temp
    suspend fun otpLoginHorizon(  @Body body : HashMap<String,String>): Response<OtpLoginResponse>
    // suspend fun otpLoginHorizon(  @Body body : HashMap<String,String>): Response<LoginOTPResult>


    @POST("auth_tokens/auth_login")
    suspend fun authLoginHorizon(  @Body body : HashMap<String,String>): Response<AuthLoginResponse>

    @GET("verifyOTP_New/{userId}/{mobileno}")
    suspend fun otpVerifyHorizon(@Path("userId") userId: String, @Path("mobileno") mobileno: String): Response<OtpVerifyResponse>

    // we can take any param in path eg "verifyOTP_New/{pathDaa}
    @GET("generateOTP_New/{mobNo}/ONBOARDING")
    suspend fun otpResendHorizon( @Path("mobNo") userId: String): Response<OtpVerifyResponse>

    @Headers("token:" + Constant.token)
    @POST("/quote/Postfm/forgotPassword")
    suspend fun forgotPassword(@Body body: HashMap<String,String>): Response<ForgotResponse>


    @POST("/Postfm/notification-auth-token")
    suspend fun insert_notification_token(@Body body: HashMap<String,String>): Response<DevicetokenResponse?>



}