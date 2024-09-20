package com.policyboss.policybosspro.core.oldWayApi.oldRequestBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.policyboss.policybosspro.utils.Constant;

import java.util.concurrent.TimeUnit;


import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GenericRetroRequestBuilder {

    //protected String url = "https://services.rupeeboss.com/LoginDtls.svc/";
    static Retrofit restAdapter = null;
    // production url
    //public static String URL = "https://www.healthassure.in";

    public static final String token = "1234567890";


    protected Retrofit build() {
        if (restAdapter == null) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .setLenient()
                    .create();

            okhttp3.OkHttpClient okHttpClient = new okhttp3.OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .addInterceptor(interceptor)
                    .build();

            restAdapter = new Retrofit.Builder()
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(Constant.base_url)
                    .build();

        }
        return restAdapter;
    }

}