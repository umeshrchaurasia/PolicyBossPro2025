package com.policyboss.policybosspro.core.response.login

data class OtpLoginResponse(
    val Msg: OtpLoginMsg? = null,
    val Status: String?
)

data class OtpLoginMsg(
    val Mobile_No: Long ?= 0L ,
    val Name: String ? = "",
    val OTP_Status: String ? ="",
    val Ss_Id: Int ? = 0
)
data class OtpLoginErrorRespnse(
    val Msg: String,
    val Status: String
)

sealed class LoginOTPResult {

    data class Success(val data: OtpLoginResponse) : LoginOTPResult()
    data class Error(val error: OtpLoginErrorRespnse) : LoginOTPResult()
}


//data class test1(
//    val Msg: Msg,
//    val Status: String
//)
//
//data class Msg(
//    val Mobile_No: Long,
//    val Name: String,
//    val OTP_Status: String,
//    val Ss_Id: Int
//)