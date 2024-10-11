package com.policyboss.policybosspro.core.di

import android.app.Application
import android.content.Context
import androidx.work.WorkManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.policyboss.policybosspro.PolicyBossProApplication
import com.policyboss.policybosspro.core.api.poilcyBossProHomeApi
import com.policyboss.policybosspro.core.api.poilcyBossProLoginApi
import com.policyboss.policybosspro.core.api.poilcyBossProSyncApi
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Collections
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit() : Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl( Constant.base_url)
    }

    @Singleton
    @Provides
    fun provideHttpInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return interceptor
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        tokenInterceptor: TokenInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(tokenInterceptor)
            .protocols(Collections.singletonList(okhttp3.Protocol.HTTP_1_1))
            .build()
    }

    @Singleton
    @Provides
    fun providePolicyBossProLoginAPI(retrofitBuilder: Retrofit.Builder, okHttpClient : OkHttpClient) : poilcyBossProLoginApi {

        return  retrofitBuilder.client(okHttpClient).build().create(poilcyBossProLoginApi::class.java)
    }

    @Singleton
    @Provides
    fun providePolicyBossProHomeAPI(retrofitBuilder: Retrofit.Builder, okHttpClient : OkHttpClient) : poilcyBossProHomeApi {

        return  retrofitBuilder.client(okHttpClient).build().create(poilcyBossProHomeApi::class.java)
    }

    @Singleton
    @Provides
    fun providePolicyBossProSyncAPI(retrofitBuilder: Retrofit.Builder, okHttpClient : OkHttpClient) : poilcyBossProSyncApi {

        return  retrofitBuilder.client(okHttpClient).build().create(poilcyBossProSyncApi::class.java)
    }




}