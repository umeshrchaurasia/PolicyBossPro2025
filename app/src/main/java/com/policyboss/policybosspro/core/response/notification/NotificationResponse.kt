package com.policyboss.policybosspro.core.response.notification

import com.policyboss.policybosspro.core.response.APIResponse

data class NotificationResponse(
    var MasterData: List<NotificationEntity>? = null
) : APIResponse()


data class NotificationEntity(
    var title: String? = null,
    var body: String? = null,
    var img_url: String? = null,
    var action: String? = null,
    var notifyFlag: String? = null,
    var web_url: String? = null,
    var web_title: String? = null,
    var is_read: String? = null,
    var date: String? = null,
    var message_id: Int = 0,
    var isOpen: Boolean = false
)