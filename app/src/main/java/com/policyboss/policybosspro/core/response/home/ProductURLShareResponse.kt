package com.policyboss.policybosspro.core.response.home

import com.policyboss.policybosspro.core.response.APIResponse

data class ProductURLShareResponse(
    var MasterData: ProductURLShareEntity? = null
) : APIResponse()


data class ProductURLShareEntity(
    var url: String = "",
    var msg: String = "",
    var popupmsg: String = ""
)