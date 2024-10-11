package com.policyboss.policybosspro.core.model.homeDashboard



class DashboardMultiLangEntity(
    var category: String,
    var sequence: Int,
    var productId: Int ,
    var menuName: String,
    var description: String,
    var iconResId: Int,
    var titleKey: String,
    var descriptionKey: String
) {
    // Optional properties initialized with default values

    var type: String = ""
    var productName: String = ""
    var productDetails: String = ""
    var serverIcon: String = ""

    var link: String = ""
    var productNameFontColor: String = ""
    var productDetailsFontColor: String = ""
    var productBackgroundColor: String = ""
    var isExclusive: String = ""
    var isNewPrdClickable: String = ""
    var isSharable: String = ""
    var popupmsg: String = ""
    var title: String = ""
    var info: String = ""
    var IsNewprdClickable : String = ""
}


