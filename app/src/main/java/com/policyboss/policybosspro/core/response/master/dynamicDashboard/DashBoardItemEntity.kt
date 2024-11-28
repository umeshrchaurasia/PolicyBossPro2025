package com.policyboss.policybosspro.core.response.master.dynamicDashboard

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class DashBoardItemEntity (
    val IsExclusive: String,
    val IsNewprdClickable: String,
    val IsSharable: String,
    val ProdId: Int,
    val ProductBackgroundColor: String,
    val ProductDetailsFontColor: String,
    val ProductNameFontColor: String,
    val dashboard_type: Int,
 //   val dashdescription: String,
    val description: String,
    val iconimage: String,
    val infourl: String,
    val isActive: Int,
    val link: String,
    val menuid: Int,
    val menuname: String,
    val popupinfo: String,
  //  val sequence: String,
    val title: String,
    val type: Int
) : Parcelable