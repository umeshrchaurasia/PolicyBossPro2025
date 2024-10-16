package com.policyboss.policybosspro.core.response.salesMaterial

import com.policyboss.policybosspro.core.response.APIResponse

data class SalesMaterialResponse(
    val MasterData: List<SalesMateriaProdEntity>,

) : APIResponse()


data class SalesMateriaProdEntity(
    val Count: Int,
    val Product_Id: Int,
    val Product_Name: String,
    val Product_image: String
)