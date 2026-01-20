package com.policyboss.policybosspro.view.salesMaterial

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.policyboss.demoandroidapp.Utility.ExtensionFun.applySystemBarInsetsPadding
import com.policyboss.policybosspro.BaseActivity
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.response.salesMaterial.CompanyEntity
import com.policyboss.policybosspro.core.response.salesMaterial.DocEntity
import com.policyboss.policybosspro.core.response.salesMaterial.SalesMateriaProdEntity
import com.policyboss.policybosspro.core.viewModel.SalesMaterialType

import com.policyboss.policybosspro.core.viewModel.salesMaterialVM.SalesMaterialViewNodel
import com.policyboss.policybosspro.databinding.ActivitySalesDetailBinding
import com.policyboss.policybosspro.databinding.ContentSalesDetailBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.BitmapUtility
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.view.salesMaterial.adapter.SalesDocAdapter
import com.webengage.sdk.android.WebEngage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.net.MalformedURLException
import java.net.URL
import javax.inject.Inject

/*
  Note : we used CustomImageView having aspect ratio : 1.5 for displaying Image in GridLayour
 */
@AndroidEntryPoint
class SalesDetailActivity : BaseActivity() {


    //region Decleration
    private lateinit var binding: ActivitySalesDetailBinding

    // Initialize contentBinding for the included layout
    private lateinit var includedBinding: ContentSalesDetailBinding // For the included layout

    private lateinit var salesDocAdapter: SalesDocAdapter

    private val viewModel by viewModels<SalesMaterialViewNodel>()

    private var fullDocList: List<DocEntity> = listOf()

    @Inject
    lateinit var prefsManager: PolicyBossPrefsManager


    lateinit var salesProductEntity: SalesMateriaProdEntity

    var bytePOSPArray: ByteArray? = null
    var byteFBAArray: ByteArray? = null

    //region Posp /FBA Details
    lateinit var pospName: String
    lateinit var pospDesg: String
    lateinit var pospEmail: String
    lateinit var pospMobNo: String

    lateinit var fbaName: String
    lateinit var fbaDesg: String
    lateinit var fbaEmail: String
    lateinit var fbaMobNo: String

    lateinit var pospPhotoUrl: URL
    lateinit var fbaPhotoUrl: URL

    //endregion


    private lateinit var companyLst: ArrayList<CompanyEntity>
    private lateinit var docLst: ArrayList<DocEntity>
    private lateinit var langType: String
    private val companyID: Int = 123 // Example value
    private val numberOfColumns: Int = 2 // Example value


    //endregion


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Opt into edge-to-edge drawing
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT

        binding = ActivitySalesDetailBinding.inflate(layoutInflater)
        //region Toolbar Set
        setContentView(binding.root)

       // binding.root.applySystemBarInsetsPadding()

        applyInsets()

        setSupportActionBar(binding.toolbar)

        supportActionBar!!.apply {

            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(Constant.SalesTitle)
        }
        //endregion

        // Initialize contentBinding for the included layout
        includedBinding = ContentSalesDetailBinding.bind(binding.includeSalesDetail.root)


        //region get parcealize salesProductEntity from SalesMaterial main page
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Use the new method for Android 13 and above
            intent.getParcelableExtra(Constant.PRODUCT_ID, SalesMateriaProdEntity::class.java)
                ?.let { entity ->
                    salesProductEntity = entity
                }
        } else {
            // Fallback for older Android versions
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<SalesMateriaProdEntity>(Constant.PRODUCT_ID)?.let { entity ->
                salesProductEntity = entity
            }
        }
        //endregion

        init()

        if (prefsManager.getUserConstantEntity() != null) {
            try {
                setOtherDetails()
                setPospDetails()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
        }

        apiCall()

        handlingURLImage()

        // Set up language switch Listner
        with(includedBinding) {
            includedBinding.swlang.setOnCheckedChangeListener { _, isChecked ->
                langType = if (isChecked) {
                    txtHindi.setTextColor(
                        ContextCompat.getColor(
                            this@SalesDetailActivity,
                            R.color.colorAccent
                        )
                    )
                    txtHindi.setTypeface(null, Typeface.BOLD)

                    txtEng.setTextColor(
                        ContextCompat.getColor(
                            this@SalesDetailActivity,
                            R.color.seperator_white
                        )
                    )
                    txtEng.setTypeface(null, Typeface.NORMAL)

                   Constant.SalesLangHindi
                } else {
                    txtEng.setTextColor(
                        ContextCompat.getColor(
                            this@SalesDetailActivity,
                            R.color.colorAccent
                        )
                    )
                    txtEng.setTypeface(null, Typeface.BOLD)

                    txtHindi.setTextColor(
                        ContextCompat.getColor(
                            this@SalesDetailActivity,
                            R.color.seperator_white
                        )
                    )
                    txtHindi.setTypeface(null, Typeface.NORMAL)

                    Constant.SalesLangEnglish
                }

                // Call a method to bind document list based on language
                //  bindDocList(companyID, langType)
               // viewModel.filteredSalesDocList(langType)
                updateDocAdapter(langType)
            }
        }

        //region backPress
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Call finish() to close the activity
                this@SalesDetailActivity.finish()
            }
        })

        //endregion

    }


    private fun applyInsets(){

        // Handle insets for status bar & nav bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            binding.appbar.setPadding(
                binding.appbar.paddingLeft,
                statusBars.top,
                binding.appbar.paddingRight,
                binding.appbar.paddingBottom
            )

            binding.includeSalesDetail.root.setPadding(
                binding.includeSalesDetail.root.paddingLeft,
                binding.includeSalesDetail.root.paddingTop,
                binding.includeSalesDetail.root.paddingRight,
                navBars.bottom
            )

            insets
        }
    }

    private fun apiCall(){



        //Mark :-- call Api for Sales Product Detail Page
        viewModel.getSalesProductDetail(salesProductEntity.Product_Id.toString())

        //Mark :- Observing Api, get Api Response
        observeResponse()
    }



    private fun handlingURLImage(){

        ///

        lifecycleScope.launch {


            //Mark : POSP Image
            val pospBitmap = viewModel.processPospBitmapFromUrl(
                salesType = SalesMaterialType.POSP,
                url = pospPhotoUrl,
                pospName = pospName,
                pospDesg = pospDesg,
                pospMob = pospMobNo,
                pospEmail = pospEmail
            )
            // Convert Bitmap to ByteArray
            pospBitmap?.let { bitmap ->
                bytePOSPArray = BitmapUtility.bitmapToByteArray(bitmap, Bitmap.CompressFormat.PNG, 100)
            }



            //Mark : POSP Image
            val fbaBitmap = viewModel.processPospBitmapFromUrl(
                salesType = SalesMaterialType.FBA,
                url = pospPhotoUrl,
                pospName = fbaName,
                pospDesg = fbaDesg,
                pospMob = fbaMobNo,
                pospEmail = fbaEmail
//                textSize = 40f,
//                height = 400,
//                textMargin = 10,
            )

            // Convert Bitmap to ByteArray
            fbaBitmap?.let { bitmap ->
                byteFBAArray = BitmapUtility.bitmapToByteArray(bitmap, Bitmap.CompressFormat.PNG, 100)
            }



        }
        //

    }




    private fun init() {



        companyLst = ArrayList()
        docLst = ArrayList()

        // Initialize the adapter with an empty list
        salesDocAdapter = SalesDocAdapter(
            context = this,
            docList = mutableListOf(), // Initialize with an empty list
            onItemClick = ::onSalesDocListener
        )

        // Use 'with' to reduce the repeated 'includedBinding'
        with(includedBinding) {


            // Set up RecyclerView for product
            rvProduct.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(this@SalesDetailActivity, numberOfColumns)
                setItemViewCacheSize(20)

                adapter = salesDocAdapter // Set the adapter here
            }
        }


    }

    private fun updateDocAdapter(language: String) {
        val filteredList = fullDocList.filter { it.language == language }


        salesDocAdapter.updateDocList(filteredList)
    }
    fun onSalesDocListener(docsEntity: DocEntity) {




        viewModel.getSalesProductClick(
            product_id = salesProductEntity?.Product_Id?.toString() ?: "",
            product_name = salesProductEntity?.Product_Name?.toString() ?: "",

            content_url = docsEntity.image_path,
            content_source = extractImageName(docsEntity.image_path),
            language = docsEntity.language
        )
        val intent = Intent(this@SalesDetailActivity, SalesShareActivity::class.java)
            .apply {
                putExtra(Constant.PRODUCT_ID, salesProductEntity)
                putExtra(Constant.DOC_DATA, docsEntity)
                putExtra(Constant.POSP_IMAGE, bytePOSPArray)    // passing POSP Details via Image footer
                putExtra(Constant.FBA_IMAGE, byteFBAArray)       // passing FBA Details via Image footer
            }


        startActivity(intent)




    }
    private fun setPospDetails() {
         pospName = "POSP Name"
         pospEmail = "XXXXXX@policyboss.com"
         pospDesg = "LandMark POSP"
         pospMobNo = "98XXXXXXXX"
         pospPhotoUrl = URL("https://origin-cdnh.policyboss.com/website/Images/campaign/profile_pic.png")

        prefsManager.getName()?.takeIf { it.isNotBlank() }?.let {
            pospName = it
        }

        prefsManager.getEmailId()?.takeIf { it.isNotBlank() }?.let {
            pospEmail = it
        }

        prefsManager.getMobileNo()?.takeIf { it.isNotBlank() }?.let {
            pospMobNo = it
        }

        prefsManager.getDesignation().let {
            pospDesg = it
        }
//        prefsManager.getUserConstantEntity()?.pospselfdesignation?.takeIf { it.isNotBlank() }?.let {
//            pospDesg = it
//        }

        //region below commneted bec pospselfphoto not founded
//        prefsManager.getUserConstantEntity()?.pospselfphoto?.takeIf { it.isNotBlank() }?.let {url ->
//            pospPhotoUrl = URL(url)
//        }
        //endregion
        pospPhotoUrl = URL(Constant.pospselfphoto)

    }

    private fun setOtherDetails() {
         fbaName = "FBA Name"
         fbaEmail = "XXXXXX@policyboss.com"
         fbaDesg = "FBA SUPPORT ASSISTANT"
         fbaMobNo = "98XXXXXXXX"
         fbaPhotoUrl =
            URL("https://origin-cdnh.policyboss.com/website/Images/campaign/profile_pic.png")



        prefsManager.getFBAID()?.takeIf { it.isNotBlank() }?.let {
            fbaName = it
        }

        prefsManager.getEmailId()?.takeIf { it.isNotBlank() }?.let {
            fbaEmail = it
        }

        prefsManager.getMobileNo()?.takeIf { it.isNotBlank() }?.let {
            fbaMobNo = it
        }
            // 05 temp

        prefsManager.getDesignation().let {
            fbaDesg = it
        }
//        prefsManager.getUserConstantEntity()?.pospselfdesignation?.takeIf { it.isNotBlank() }?.let {
//            fbaDesg = it
//        }

      //region below commneted bec pospselfphoto not founded
    //        prefsManager.getUserConstantEntity()?.pospselfphoto?.takeIf { it.isNotBlank() }?.let {url ->
    //            fbaPhotoUrl = URL(url)
    //        }
        // endregion

        fbaPhotoUrl = URL(Constant.pospselfphoto)

    }



    //region Not In Used {direct assign to adpater
    private fun setupDocAdapter(docList: List<DocEntity>) {

        if (docList.isNotEmpty()) {
            salesDocAdapter = SalesDocAdapter(
                context = this,
                docList = docList.toMutableList(),
                onItemClick = ::onSalesDocListener
            )
            includedBinding.rvProduct.adapter = salesDocAdapter

        } else {
            // Handle empty list case: show placeholder, empty state view, etc.
            includedBinding.rvProduct.adapter = null
        }
    }

    //endregion




    fun extractImageName(imageUrl: String?): String {
            if (imageUrl.isNullOrEmpty()) {
                return "" // Handle empty or null URL
            }

            val lastSlashIndex = imageUrl.lastIndexOf('/')
            if (lastSlashIndex == -1) {
                return "" // No slash found, potentially invalid URL
            }

            return imageUrl.substring(lastSlashIndex + 1)
        }


    private fun observeResponse() {

          lifecycleScope.launch {

                repeatOnLifecycle(Lifecycle.State.STARTED) {

                    launch {

                        viewModel.SalesMaterialDtlResponse.collect { event ->

                            event.contentIfNotHandled?.let {

                                when (it) {
                                    is APIState.Empty -> {
                                        hideLoading()
                                    }

                                    is APIState.Failure -> {
                                        hideLoading()


                                        Log.d(Constant.TAG, it.errorMessage.toString())
                                    }

                                    is APIState.Loading -> {
                                        displayLoadingWithText()
                                    }

                                    is APIState.Success -> {

                                        hideLoading()

                                        it.data?.MasterData?.let { ProdDtlEntity ->

                                            //setupSalesMaterialAdapter(lstSalesProdEntity)

                                            fullDocList  = ProdDtlEntity.docs
                                           // setupDocAdapter(docList = ProdDtlEntity.docs)


                                            updateDocAdapter(Constant.SalesLangEnglish)
                                        }
                                    }
                                }


                            }


                        }
                    }


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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Finish the activity when the Up button is pressed
                this@SalesDetailActivity.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



}