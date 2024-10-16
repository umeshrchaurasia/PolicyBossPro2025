package com.policyboss.policybosspro.core.response.salesMaterial

import com.policyboss.policybosspro.core.response.APIResponse

data class SalesMaterialProductDetailsResponse(
    val MasterData: SalesMaterialProductDetailsEntity
): APIResponse()


data class SalesMaterialProductDetailsEntity(
    val company: List<CompanyEntity>,
    val docs: List<Doc>
)
data class CompanyEntity(
    val Company_Name: String,
    val company_id: Int
)

data class Doc(
    val Company_Name: String,
    val company_id: Int,
    val image_path: String,
    val language: String
)