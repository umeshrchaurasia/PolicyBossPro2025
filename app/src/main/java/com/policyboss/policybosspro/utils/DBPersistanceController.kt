package com.policyboss.policybosspro.utils

import android.content.Context
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.core.model.homeDashboard.DashboardEntity
import com.policyboss.policybosspro.core.model.homeDashboard.DashboardMultiLangEntity
import com.policyboss.policybosspro.core.response.master.dynamicDashboard.DashBoardItemEntity
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DBPersistanceController @Inject constructor(
    private val mContext: Context,
    private val prefManager: PolicyBossPrefsManager
) {

    companion object {
        const val EXTERNAL_LPG = "External LPG"
        const val EXTERNAL_CNG = "External CNG"
        const val INSURER_LIST = "insurer_list"
        const val FOS_DETAIL = "fos_detail"
    }


// Function to get the insurance product language list
    fun getInsurProductLangList(): List<DashboardMultiLangEntity> {
        val dashboardEntities = mutableListOf<DashboardMultiLangEntity>()

        // Retrieve the dashboard data from prefManager
        val dashBoardItemEntities = prefManager.getMenuDashBoard()?.MasterData?.Dashboard

        dashBoardItemEntities?.let { items ->
            items.filter { it.dashboard_type.equals("1")  && it.isActive == 1 }
                .forEach { dashBoardItemEntity ->
                val dashboardEntity = DashboardMultiLangEntity(
                    category = "INSURANCE",
                    sequence = dashBoardItemEntity.sequence.toInt(),
                    menuName = dashBoardItemEntity.menuname,
                    description = dashBoardItemEntity.description,
                    iconResId = -1,  // Assuming no local resource, replace if needed
                    titleKey = "Insurance",
                    descriptionKey = ""
                ).apply {
                    serverIcon = dashBoardItemEntity.iconimage
                    link = dashBoardItemEntity.link
                    productNameFontColor = dashBoardItemEntity.ProductNameFontColor
                    productDetailsFontColor = dashBoardItemEntity.ProductDetailsFontColor
                    productBackgroundColor = dashBoardItemEntity.ProductBackgroundColor
                    isExclusive = dashBoardItemEntity.IsExclusive
                    isNewPrdClickable = dashBoardItemEntity.IsNewprdClickable
                    isSharable = dashBoardItemEntity.IsSharable
                    title = dashBoardItemEntity.title
                    info = dashBoardItemEntity.info
                    popupmsg = dashBoardItemEntity.popupmsg
                }
                dashboardEntities.add(dashboardEntity)
            }
        }

        return dashboardEntities
    }

}
