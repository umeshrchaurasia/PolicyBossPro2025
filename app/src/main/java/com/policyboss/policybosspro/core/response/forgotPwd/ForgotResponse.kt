package com.policyboss.policybosspro.core.response.forgotPwd

import com.policyboss.policybosspro.core.response.APIResponse

data class ForgotResponse(
    var masterData: String? = null
) : APIResponse()