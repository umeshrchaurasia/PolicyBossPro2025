package com.policyboss.policybosspro.core.oldWayApi.oldRequestBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.policyboss.policybosspro.core.oldWayApi.UnsafeOkHttpClient;
import com.policyboss.policybosspro.utils.Constant;

import java.util.concurrent.TimeUnit;


import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rajeev Ranjan on 22/01/2018.
 */

public class FinmartRetroRequestBuilder {

    //protected String url = "https://services.rupeeboss.com/LoginDtls.svc/";
    public static Retrofit restAdapter = null;
    // production url
    //public static String URL = "https://horizon.policyboss.com:5443";
    // Test Environment url
    //   public static String URL = "https://qa.mgfm.in";

    //UAT
    //public static String URL = "https://uat.mgfm.in";


    public static final String token = "1234567890";

    private OkHttpClient okHttp() {
      return  UnsafeOkHttpClient.getUnsafeOkHttpClient();
    }
    protected Retrofit build() {
        if (restAdapter == null) {


            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    // .setLenient()
                    .create();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.MINUTES)
                    .writeTimeout(3, TimeUnit.MINUTES)
                    .readTimeout(3, TimeUnit.MINUTES)
                    .addInterceptor(interceptor)
                    .build();

            restAdapter = new Retrofit.Builder()
                    .baseUrl(Constant.base_url)
                    .client(okHttp())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        }
        return restAdapter;
    }

}
