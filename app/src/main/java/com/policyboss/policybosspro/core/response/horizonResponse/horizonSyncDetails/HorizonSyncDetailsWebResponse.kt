package com.policyboss.policybosspro.core.response.horizonResponse.horizonSyncDetails

import com.policyboss.policybosspro.core.model.sysncContact.POSPHorizonEntity
import com.policyboss.policybosspro.core.model.sysncContact.SyncContactEntity
import com.policyboss.policybosspro.core.response.APIResponse

data class HorizonSyncDetailsWebResponse(
    var user_type: String? = "",
    var product: String? = "",
    var channel: String? = "",
    var SYNC_CONTACT: SyncContactEntity? = null,
    var POSP: POSPHorizonEntity? = null   //POSPHorizonEntity NOTE : SOME TIME COMMINNG "NA" OR POSPHorizonEntity
) : APIResponse() {
    // If you need to keep the specific getter/setter names, you can add them like this:

    // Getter and Setter methods


}