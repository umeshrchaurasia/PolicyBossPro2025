package com.policyboss.policybosspro.view.salesMaterial

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.policyboss.policybosspro.BaseActivity
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.core.response.salesMaterial.SalesMateriaProdEntity
import com.policyboss.policybosspro.core.response.salesMaterial.SalesMaterialResponse
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.utils.showAlert
import com.webengage.sdk.android.WebEngage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SalesDetailActivity : BaseActivity() {


    @Inject
    lateinit var prefsManager: PolicyBossPrefsManager


    lateinit var salesProductEntity : SalesMateriaProdEntity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_detail)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Use the new method for Android 13 and above
            intent.getParcelableExtra(Constant.PRODUCT_ID, SalesMateriaProdEntity::class.java)?.let { entity ->
                salesProductEntity = entity
            }
        } else {
            // Fallback for older Android versions
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<SalesMateriaProdEntity>(Constant.PRODUCT_ID)?.let { entity ->
                salesProductEntity = entity
            }
        }

    }

    override fun onStart() {
        super.onStart()
        try {
            val screenData = mutableMapOf<String, Any?>()

            screenData["SS ID"] = prefsManager.getSSID()
            screenData["FBA ID"] = prefsManager.getFBAID()
            screenData["Name"] = prefsManager.getName()

            screenData["Product ID"] = salesProductEntity.Product_Id
            screenData["Product Name"] = salesProductEntity.Product_image

            WebEngage.get().analytics().screenNavigated("SalesDetail Screen", screenData)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}