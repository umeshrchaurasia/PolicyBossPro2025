package com.policyboss.policybosspro.core.response.syncContact

import com.policyboss.policybosspro.core.response.APIResponse

data class ContactPhotoDocResponse(
    val MasterData: List<PhotoDocMasterData>,
    ) : APIResponse()


data class PhotoDocMasterData(
    val Message: String,
    val RowUpdated: Int,
    val SavedStatus: Int,
    val prv_file: String
)