package com.policyboss.policybosspro.core.oldWayApi.controller.registerController;

import android.content.Context;

import androidx.annotation.NonNull;

import com.policyboss.policybosspro.core.oldWayApi.IResponseSubcriber;
import com.policyboss.policybosspro.core.oldWayApi.oldRequestBuilder.RegisterRequestBuilder;
import com.policyboss.policybosspro.core.response.paymentElite.PaymentDetail_EliteResponse;
import com.policyboss.policybosspro.core.response.razorPay.RazorPayResponse;
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager;
import com.policyboss.policybosspro.utils.DBPersistanceController;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


import javax.inject.Inject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rajeev Ranjan on 22/01/2018.
 */

public class RegisterController implements IRegister {
    RegisterRequestBuilder.RegisterQuotesNetworkService registerQuotesNetworkService;
    Context mContext;


    @Inject
    PolicyBossPrefsManager prefManager;


    public RegisterController(Context context) {
        registerQuotesNetworkService = new RegisterRequestBuilder().getService();
        mContext = context;


    }

    @Override
    public void getDataForPayment_elite(String custid, final IResponseSubcriber iResponseSubcriber) {

        HashMap<String, String> body = new HashMap<>();
        body.put("custid", custid);

        registerQuotesNetworkService.getDataForPayment_EliteCustomer(body).enqueue(new Callback<PaymentDetail_EliteResponse>() {
            @Override
            public void onResponse(Call<PaymentDetail_EliteResponse> call, Response<PaymentDetail_EliteResponse> response) {
                if (response.body() != null) {

                    //callback of data
                    iResponseSubcriber.OnSuccess(response.body(), response.body().getMessage());

                } else {
                    //failure
                    iResponseSubcriber.OnFailure(new RuntimeException("Enable to reach server, Try again later"));
                }
            }

            @Override
            public void onFailure(Call<PaymentDetail_EliteResponse> call, Throwable t) {
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
    public void addToRazorPay_elite(String FBAID, String CustId, String PayId, final IResponseSubcriber iResponseSubcriber) {


        HashMap<String, String> body = new HashMap<>();
        // body.put("FBAID",FBAID);
        body.put("custid", CustId);
        body.put("PayId", PayId);


        registerQuotesNetworkService.addToRazorPayElite(body).enqueue(new Callback<RazorPayResponse>() {
            @Override
            public void onResponse(Call<RazorPayResponse> call, Response<RazorPayResponse> response) {
                if (response.body() != null) {

                    //callback of data
                    iResponseSubcriber.OnSuccess(response.body(), response.body().getMessage());

                } else {
                    //failure
                    iResponseSubcriber.OnFailure(new RuntimeException("Enable to reach server, Try again later"));
                }
            }

            @Override
            public void onFailure(Call<RazorPayResponse> call, Throwable t) {
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

}
