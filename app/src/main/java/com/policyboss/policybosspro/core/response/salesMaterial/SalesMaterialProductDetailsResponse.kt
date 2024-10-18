package com.policyboss.policybosspro.core.response.salesMaterial

import android.os.Parcelable
import com.policyboss.policybosspro.core.response.APIResponse
import kotlinx.android.parcel.Parcelize

data class SalesMaterialProductDetailsResponse(
    val MasterData: SalesMaterialProductDetailsEntity
): APIResponse()


data class SalesMaterialProductDetailsEntity(
    val company: List<CompanyEntity>,
    val docs: List<DocEntity>
)
data class CompanyEntity(
    val Company_Name: String,
    val company_id: Int
)

@Parcelize
data class DocEntity(
    val Company_Name: String,
    val company_id: Int,
    val image_path: String,
    val language: String
) : Parcelable