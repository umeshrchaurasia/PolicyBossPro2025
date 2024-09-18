package com.policyboss.policybosspro.core.repository.loginRepository

import com.policyboss.policybosspro.core.api.poilcyBossProLoginApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(
    private  val apiService : poilcyBossProLoginApi
  ) {


    suspend fun getusersignup(body : HashMap<String,String> ) = flow {


        val response = apiService.getusersignup(body = body)

        emit(response)
    }.flowOn(Dispatchers.IO)


    suspend fun  insert_notification_token(body : HashMap<String,String>) = flow {
        val response = apiService.insert_notification_token(body=body)
        emit(response)
    }.flowOn(Dispatchers.IO)


    suspend fun getLoginHorizonDetails(userId: String) = flow {
        val response = apiService.getLoginDsasHorizonDetails(userId = userId)
        emit(response)
    }.flowOn(Dispatchers.IO)


    suspend fun otpLoginHorizon(body : HashMap<String,String> ) = flow {


        val response = apiService.otpLoginHorizon(body = body)

        emit(response)
    }.flowOn(Dispatchers.IO)

    suspend fun otpVerifyHorizon(userId : String, mobileno : String ) = flow {


        val response = apiService.otpVerifyHorizon(userId = userId, mobileno =  mobileno)

        emit(response)
    }.flowOn(Dispatchers.IO)

    suspend fun otpResendHorizon(userId : String ) = flow {


        val response = apiService.otpResendHorizon(userId = userId)

        emit(response)
    }.flowOn(Dispatchers.IO)

    suspend fun authLoginHorizon(body : HashMap<String,String> ) = flow {


        val response = apiService.authLoginHorizon(body = body)

        emit(response)
    }.flowOn(Dispatchers.IO)

    //otpVerifyHorizon

    suspend fun forgotPassword(body : HashMap<String,String> ) = flow {


        val response = apiService.forgotPassword(body = body)

        emit(response)
    }.flowOn(Dispatchers.IO)


}