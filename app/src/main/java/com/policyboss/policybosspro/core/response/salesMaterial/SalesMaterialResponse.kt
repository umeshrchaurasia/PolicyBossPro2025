package com.policyboss.policybosspro.core.response.salesMaterial

import android.os.Parcelable
import com.policyboss.policybosspro.core.response.APIResponse
import kotlinx.android.parcel.Parcelize

data class SalesMaterialResponse(
    val MasterData: List<SalesMateriaProdEntity>,

) : APIResponse()


@Parcelize
data class SalesMateriaProdEntity(
    val Count: Int,
    var OldCount: Int = 0,
    val Product_Id: Int,
    val Product_Name: String,
    val Product_image: String
) : Parcelable