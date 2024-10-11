package com.policyboss.policybosspro.core.oldWayApi.controller.dynamicController;

import android.content.Context;

import com.policyboss.policybosspro.core.oldWayApi.IResponseSubcriber;
import com.policyboss.policybosspro.core.response.horizonResponse.horizonSyncDetails.HorizonSyncDetailsWebResponse;
import com.policyboss.policybosspro.core.response.horizonResponse.horizonSyncDetails.HorizonsyncDetailsResponse;
import com.policyboss.policybosspro.core.response.othere.syncrazorsucessReponse;
import com.policyboss.policybosspro.core.response.syncContact.syncContactDetailsResponse.synctransactionDetailReponse;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nilesh Birhade on 06-08-2018.
 */

public class DynamicController implements IDynamic {

    DynamicUrlBuilder.GenericUrlNetworkService genericUrlNetworkService;
    Context mContext;

    public DynamicController(Context context) {
        genericUrlNetworkService = new DynamicUrlBuilder().getService();
        mContext = context;
    }


    @Override
    public void getSync_trascat_detail(String transaction_Id, IResponseSubcriber iResponseSubcriber) {

        String url = "https://horizon.policyboss.com:5443/razorpay_payment/transaction_details/" + transaction_Id;


        genericUrlNetworkService.getSync_trascat_detail(url).enqueue(new Callback<synctransactionDetailReponse>() {
            @Override
            public void onResponse(Call<synctransactionDetailReponse> call, Response<synctransactionDetailReponse> response) {
                if (response.body() != null) {
                    iResponseSubcriber.OnSuccess(response.body(), "response.body().getMessage()");

                } else {
                    iResponseSubcriber.OnFailure(new RuntimeException(response.body().getMessage()));
                }
            }

            @Override
            public void onFailure(Call<synctransactionDetailReponse> call, Throwable t) {

                if (t instanceof ConnectException) {
                    iResponseSubcriber.OnFailure(t);
                } else if (t instanceof SocketTimeoutException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Check your internet connection"));
                } else if (t instanceof UnknownHostException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Check your internet connection"));
                } else if (t instanceof NumberFormatException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Unexpected server response"));
                } else {
                    iResponseSubcriber.OnFailure(new RuntimeException(t.getMessage()));
                }

            }
        });
    }

    @Override
    public void getSync_trascat_Cancle(String transaction_Id, IResponseSubcriber iResponseSubcriber) {

    }

    @Override
    public void getSync_razor_payment(String transaction_Id, String PayId, IResponseSubcriber iResponseSubcriber) {

        String url = "https://horizon.policyboss.com/razorpay-transaction-status/" + transaction_Id+"/Success/"+PayId;


        genericUrlNetworkService.getSync_razor_payment(url).enqueue(new Callback<syncrazorsucessReponse>() {
            @Override
            public void onResponse(Call<syncrazorsucessReponse> call, Response<syncrazorsucessReponse> response) {
                if (response.body() != null) {
                    iResponseSubcriber.OnSuccess(response.body(), "response.body().getMessage()");

                } else {
                    iResponseSubcriber.OnFailure(new RuntimeException(response.body().getMessage()));
                }
            }

            @Override
            public void onFailure(Call<syncrazorsucessReponse> call, Throwable t) {

                if (t instanceof ConnectException) {
                    iResponseSubcriber.OnFailure(t);
                } else if (t instanceof SocketTimeoutException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Check your internet connection"));
                } else if (t instanceof UnknownHostException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Check your internet connection"));
                } else if (t instanceof NumberFormatException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Unexpected server response"));
                } else {
                    iResponseSubcriber.OnFailure(new RuntimeException(t.getMessage()));
                }

            }
        });

    }

    @Override
    public void getsyncDetailshorizon_java(String ss_id, IResponseSubcriber iResponseSubcriber) {

        String url = "https://horizon.policyboss.com:5443/posps/dsas/view/" + ss_id;

        genericUrlNetworkService.getsyncDetailshorizondetail(url).enqueue(new Callback<HorizonSyncDetailsWebResponse>() {
            @Override
            public void onResponse(Call<HorizonSyncDetailsWebResponse> call, Response<HorizonSyncDetailsWebResponse> response) {
                if (response.body() != null) {
                    iResponseSubcriber.OnSuccess(response.body(), "response.body().getMessage()");

                } else {
                    iResponseSubcriber.OnFailure(new RuntimeException(response.body().getMessage()));
                }
            }

            @Override
            public void onFailure(Call<HorizonSyncDetailsWebResponse> call, Throwable t) {

                if (t instanceof ConnectException) {
                    iResponseSubcriber.OnFailure(t);
                } else if (t instanceof SocketTimeoutException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Check your internet connection"));
                } else if (t instanceof UnknownHostException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Check your internet connection"));
                } else if (t instanceof NumberFormatException) {
                    iResponseSubcriber.OnFailure(new RuntimeException("Unexpected server response"));
                } else {
                    iResponseSubcriber.OnFailure(new RuntimeException(t.getMessage()));
                }

            }
        });

    }

    @Override
    public void getsalesmaterial_contentclick(String app_version, String product_id, String product_name, String device_code, String fbaid, String ssid, String type_of_content, String content_url, String language, String content_source, IResponseSubcriber iResponseSubcriber) {

    }
}
