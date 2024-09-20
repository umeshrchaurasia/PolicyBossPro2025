package com.policyboss.policybosspro.core.oldWayApi.oldRequestBuilder;

import com.policyboss.policybosspro.core.response.paymentElite.PaymentDetail_EliteResponse;
import com.policyboss.policybosspro.core.response.razorPay.RazorPayResponse;
import com.policyboss.policybosspro.utils.Constant;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class RegisterRequestBuilder extends FinmartRetroRequestBuilder {

    public RegisterRequestBuilder.RegisterQuotesNetworkService getService() {

        return super.build().create(RegisterRequestBuilder.RegisterQuotesNetworkService.class);
    }

    public interface RegisterQuotesNetworkService {

        @Headers("token:" + Constant.token)
        @POST("/quote/Postfm/GetEliteKotakCustomer")
        Call<PaymentDetail_EliteResponse> getDataForPayment_EliteCustomer(@Body HashMap<String, String> body);

        @Headers("token:" + token)
        @POST("/quote/Postfm/EliteKotakRazorPayment")
        Call<RazorPayResponse> addToRazorPayElite(@Body HashMap<String, String> body);


    }



}