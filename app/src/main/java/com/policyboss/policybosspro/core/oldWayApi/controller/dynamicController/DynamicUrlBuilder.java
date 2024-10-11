package com.policyboss.policybosspro.core.oldWayApi.controller.dynamicController;

import com.policyboss.policybosspro.core.oldWayApi.oldRequestBuilder.GenericRetroRequestBuilder;
import com.policyboss.policybosspro.core.response.horizonResponse.horizonSyncDetails.HorizonSyncDetailsWebResponse;
import com.policyboss.policybosspro.core.response.horizonResponse.horizonSyncDetails.HorizonsyncDetailsResponse;
import com.policyboss.policybosspro.core.response.othere.SalesclickResponse;
import com.policyboss.policybosspro.core.response.othere.syncrazorsucessReponse;
import com.policyboss.policybosspro.core.response.syncContact.syncContactDetailsResponse.synctransactionDetailReponse;

import java.util.HashMap;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by Nilesh Birhade on 06-08-2018.
 */

public class DynamicUrlBuilder extends GenericRetroRequestBuilder {


    public GenericUrlNetworkService getService() {
        return super.build().create(GenericUrlNetworkService.class);
    }

    public interface GenericUrlNetworkService {

//        @GET
//        Call<VehicleInfoEntity> getVehicleByVehicleNo(@Url String strUrl);

//        @GET
//        Call<VehicleMobileResponse> getVehicleByMobNo(@Url String strGetUrl);
//
//        @Headers("token:" + token)
//        @POST
//        Call<GenerateLeadResponse> saveGenerateLead(@Url String strUrl, @Body GenerateLeadRequestEntity entity);
//
//        @Headers("token:" + token)
//        @POST
//        Call<NCDResponse> getNCD(@Url String strUrl, @Body HashMap<String, String> body);

//        @Headers("token:" + token)
//        @POST
//        Call<UploadNCDResponse> uploadNCD(@Url String s, @Body UploadNCDRequestEntity entity);


//        @Headers("token:" + token)
//        @Multipart
//        @POST
//        Call<DocumentResponse> uploadNCD_Document(@Url String s, @Part() MultipartBody.Part doc, @PartMap() Map<String, String> partMap);

//        @Headers("token:" + token)
//        @POST("/quote/Postfm/GetPospAppointmentLetter")
//        Call<CertificateResponse> GetPospAppointmentLetter(@Body CertificateEntity entity);

//        @Headers("token:" + token)
//        @POST("/quote/Postfm/user-behaviour-data")
//        Call<UserBehaviourResponse> sendUserBehaviour(@Body UserBehaviourRequestEntity entity);
//

//        @POST
//        Call<mybusinessResponse> getMyBusiness(@Url String strUrl, @Body HashMap<String, String> body);

//        @GET
//        Call<personal_bank_list_Response> getBankdetail_personalloan(@Url String strUrl);

//        @GET
//        Call<home_bank_list_Response> getBankdetail_homeloan(@Url String strUrl);

//        @POST
//        Call<CheckAppAccessResponse> checkAppAccess(@Url String url, @Body HashMap<String, String> body);
//
//        @POST
//        Call<SwipeDetailResponse> SwipeDetailsTop(@Url String url, @Body HashMap<String, String> body);
//
//        @POST
//        Call<SwipeDetailResponse> indoorAttendance(@Url String url, @Body RegisterRequestEntity entity);
//
//        @POST
//        Call<SwipeDetailResponse> saveNewRegestration(@Url String url, @Body RegisterRequestEntity entity);
//
//
//        @POST
//        Call<SwipeDetailResponse>attendanceReport(@Url String url, @Body HashMap<String, String> body);
//

        @GET
        Call<synctransactionDetailReponse> getSync_trascat_detail(@Url String strUrl);

        @GET
        Call<synctransactionDetailReponse> getSync_trascat_Cancle(@Url String strUrl);

        @GET
        Call<syncrazorsucessReponse> getSync_razor_payment(@Url String strUrl);

        @GET
        Call<HorizonSyncDetailsWebResponse> getsyncDetailshorizondetail(@Url String strUrl);

        @POST
        Call<SalesclickResponse> save_sales_contentclick(@Url String url, @Body HashMap<String, String> body);
    }
}
