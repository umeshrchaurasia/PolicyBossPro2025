package com.policyboss.policybosspro.core.response.doc

import com.policyboss.policybosspro.core.response.APIResponse

data class DocumentResponse(
    var MasterDataEntity: List<DocUploadEntity?>? = null
) : APIResponse() {

    data class DocUploadEntity(
        var savedStatus: Int = 0,
        var message: String? = null,
        var rowUpdated: Int = 0,
        var prvFile: String? = null
    )
}