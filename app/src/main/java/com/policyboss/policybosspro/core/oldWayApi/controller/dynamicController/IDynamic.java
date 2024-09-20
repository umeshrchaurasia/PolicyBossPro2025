package com.policyboss.policybosspro.core.oldWayApi.controller.dynamicController;



import com.policyboss.policybosspro.core.model.GenerateLeadRequestEntity;
import com.policyboss.policybosspro.core.model.UploadNCDRequestEntity;
import com.policyboss.policybosspro.core.oldWayApi.IResponseSubcriber;

import java.util.HashMap;


import okhttp3.MultipartBody;

/**
 * Created by Nilesh Birhade on 06-08-2018.
 */

public interface IDynamic {

//    void getVehicleByVehicleNo(String vehicleNo, IResponseSubcriber iResponseSubcriber);
//
//    void getVehicleByMobileNo(String mobileNo, IResponseSubcriber iResponseSubcriber);
//
//    void saveGenerateLead(GenerateLeadRequestEntity entity, IResponseSubcriber iResponseSubcriber);
//
//    void getNCD(IResponseSubcriber iResponseSubcriber);
//
//    void uploadNCDDetails(UploadNCDRequestEntity entity, IResponseSubcriber iResponseSubcriber);
//
//
//    void uploadNCDDocuments(MultipartBody.Part document, HashMap<String, String> body, final IResponseSubcriber iResponseSubcriber);
//
//    void GetPospAppointmentLetter(CertificateEntity certificateEntity, IResponseSubcriber iResponseSubcriber);
//
//
//    void sendUserBehaviour();

    //My Business
//    void getMyBusiness(String id, IResponseSubcriber iResponseSubcriber);
//
//    //Personal loan
//    void getBankdetail_personalloan(String cityid, String Productid, IResponseSubcriber iResponseSubcriber);
//
//    //Personal loan
//    void getBankdetail_homeloan(String cityid, String Productid, IResponseSubcriber iResponseSubcriber);
//
//    void checkAppAccess(String deviceID, String deviceToken, String uid, IResponseSubcriber iResponseSubcriber);
//
//    void swipeDetailsTop(String deviceID, String deviceToken, String uid, IResponseSubcriber iResponseSubcriber);
//
//    void indoorAttendance(RegisterRequestEntity entity, IResponseSubcriber iResponseSubcriber);
//
//    void outdoorAttendance(RegisterRequestEntity entity,IResponseSubcriber iResponseSubcriber);
//
//    void attendanceRegister(RegisterRequestEntity entity,IResponseSubcriber iResponseSubcriber);
//
//    void getAttendanceReport(String uid,long fdate, long tdate, IResponseSubcriber iResponseSubcriber);
//


    //Sync Contact Razor Pay
    void getSync_trascat_detail(String transaction_Id,IResponseSubcriber iResponseSubcriber);

    void getSync_trascat_Cancle(String transaction_Id,IResponseSubcriber iResponseSubcriber);

    void getSync_razor_payment(String transaction_Id,String PayId,IResponseSubcriber iResponseSubcriber);

    void getsyncDetailshorizon_java(String ss_id,final IResponseSubcriber iResponseSubcriber);


    void getsalesmaterial_contentclick(String app_version, String product_id,String product_name ,String device_code,
                                              String fbaid, String ssid, String type_of_content,
                                              String content_url, String language, String content_source,
                                              final IResponseSubcriber iResponseSubcriber);


    }
