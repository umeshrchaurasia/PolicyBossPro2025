package com.policyboss.policybosspro.core.response.master.dynamicDashboard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class DashBoardItemEntity (
    val IsExclusive: String,
    val IsNewprdClickable: String,
    val IsSharable: String,
    val ProdId: String,
    val ProductBackgroundColor: String,
    val ProductDetailsFontColor: String,
    val ProductNameFontColor: String,
    val dashboard_type: String,
    val dashdescription: String,
    val description: String,
    val iconimage: String,
    val info: String,
    val isActive: Int,
    val link: String,
    val menuid: Int,
    val menuname: String,
    val popupmsg: String,
    val sequence: String,
    val title: String,
    val type: Int
) : Parcelable