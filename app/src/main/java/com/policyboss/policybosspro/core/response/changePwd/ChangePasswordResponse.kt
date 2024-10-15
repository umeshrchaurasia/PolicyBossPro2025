package com.policyboss.policybosspro.core.response.changePwd

import com.policyboss.policybosspro.core.response.APIResponse

data class ChangePasswordResponse(

    val masterData: List<ChangePwdData>
) :  APIResponse()

data class ChangePwdData(
    val savedStatus: Int,

    val messageX: String
)
