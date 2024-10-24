package com.policyboss.policybosspro.core.model.notification

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotifyEntity(
    var title: String? = null,
    var body: String? = null,
    var notifyFlag: String? = null,
    var web_url: String? = null,
    var web_title: String? = null,
    var message_id: String? = null
) : Parcelable