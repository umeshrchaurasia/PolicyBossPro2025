package com.policyboss.policybosspro.core.model.sysncContact

data class SyncContactEntity(

    var contact: Long = 0,
    var erp_id: Long = 0,
    var fba_id: Long = 0,
    var ss_id: Long = 0,
    var ACTION_NEEDED: String? = null,
    var FIRST_SYNC_CAMPAIGN_CREATIVE: String? = null,
    var RE_SYNC_CAMPAIGN_CREATIVE: String? = null,
    var Days_From_Last_Sync: Long = 0
)