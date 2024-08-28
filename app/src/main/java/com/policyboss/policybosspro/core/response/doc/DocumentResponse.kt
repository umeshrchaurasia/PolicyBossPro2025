package com.policyboss.policybosspro.core.response.doc

import com.policyboss.policybosspro.core.response.APIResponse

data class DocumentResponse(
    var masterData: List<MasterDataEntity>? = null
) : APIResponse() {

    data class MasterDataEntity(
        var savedStatus: Int = 0,
        var message: String? = null,
        var rowUpdated: Int = 0,
        var prvFile: String? = null
    )
}