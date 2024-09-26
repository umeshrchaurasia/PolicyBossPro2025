package com.policyboss.policybosspro.core.response.master


import com.policyboss.policybosspro.core.response.horizonResponse.horizonSyncDetails.HorizonsyncDetailsResponse
import com.policyboss.policybosspro.core.response.master.dynamicDashboard.MenuMasterResponse
import com.policyboss.policybosspro.core.response.master.userConstant.UserConstantResponse

data class MasterDataCombine(
    val userConstant: UserConstantResponse?,
    val menuMaster: MenuMasterResponse?,
    val horizonDetail : HorizonsyncDetailsResponse?
)
