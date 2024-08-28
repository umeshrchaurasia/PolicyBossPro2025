package com.policyboss.policybosspro.core.di



import com.policyboss.policybosspro.utils.Constant
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class TokenInterceptor @Inject constructor() : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
     //   val token = tokenManager.getToken()

        request.addHeader("token", "${Constant.token}")
        return chain.proceed(request.build())
    }

}