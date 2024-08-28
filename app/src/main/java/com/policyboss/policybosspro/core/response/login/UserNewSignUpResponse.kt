package com.policyboss.policybosspro.core.response.login

data class UserNewSignUpResponse(
    val MasterData: List<SignUpMasterData>,
    val Message: String,
    val Status: String,
    val StatusNo: Int
)

data class SignUpMasterData(
    val enable_elite_signupurl: String,
    val enable_pro_Addsubuser_url: String,
    val enable_pro_pospurl: String,
    val enable_pro_signupurl: String,
    val enable_otp_only:String
)