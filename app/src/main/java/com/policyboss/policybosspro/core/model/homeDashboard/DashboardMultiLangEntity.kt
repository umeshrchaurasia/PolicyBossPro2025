package com.policyboss.policybosspro.core.model.homeDashboard



 class DashboardMultiLangEntity(
    val category: String,
    val sequence: Int,
    val productId: Int ,
    val menuName: String,
    val description: String,
    val iconResId: Int,
    val titleKey: String,
    val descriptionKey: String,
    val isNewPrdClickable: String,

    var type: String? = null,
    var productName: String,
    var productDetails: String? = null,
    var serverIcon: String? = null,
    var link: String? = null,
    var productNameFontColor: String? = null,
    var productDetailsFontColor: String? = null,
    var productBackgroundColor: String? = null,
    var isExclusive: String? = null,
    var isSharable: String? = null,
    var popupmsg: String? = null,
    var title: String? = null,
    var info: String

)


