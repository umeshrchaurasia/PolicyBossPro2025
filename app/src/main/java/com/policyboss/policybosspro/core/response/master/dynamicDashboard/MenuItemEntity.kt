
package com.policyboss.policybosspro.core.response.master.dynamicDashboard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class MenuItemEntity(
    var menuid: Int = 0,
    var menuname: String? = null,
    var link: String? = null,
    var iconimage: String? = null,
    var isActive: Int = 0,
    var description: String? = null,
    var type: Int = 0,
    var sequence: String? = "0"
) : Parcelable