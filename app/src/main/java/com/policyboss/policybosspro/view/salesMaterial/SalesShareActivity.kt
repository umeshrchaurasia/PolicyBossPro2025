package com.policyboss.policybosspro.view.salesMaterial

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.policyboss.demoandroidapp.Utility.ExtensionFun.applySystemBarInsetsPadding
import com.policyboss.policybosspro.BaseActivity

import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.core.response.salesMaterial.DocEntity
import com.policyboss.policybosspro.core.response.salesMaterial.SalesMateriaProdEntity
import com.policyboss.policybosspro.core.viewModel.salesMaterialVM.SalesMaterialViewNodel
import com.policyboss.policybosspro.databinding.ActivitySalesShareBinding
import com.policyboss.policybosspro.databinding.ContentSalesShareBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.view.home.HomeActivity
import com.webengage.sdk.android.WebEngage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SalesShareActivity :BaseActivity() {


    //region Decleration
    private lateinit var binding: ActivitySalesShareBinding

    // Initialize contentBinding for the included layout
    private lateinit var includedBinding: ContentSalesShareBinding // For the included layout

    var POSPBitmap: Bitmap? = null
    var FBABitmap: Bitmap? = null

    @Inject
    lateinit var prefsManager: PolicyBossPrefsManager

    lateinit var salesProductEntity: SalesMateriaProdEntity

    lateinit var docsEntity : DocEntity


    private val viewModel by viewModels<SalesMaterialViewNodel>()

    var perms = arrayOf(
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.READ_MEDIA_IMAGES"
    )

    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Opt into edge-to-edge drawing
        WindowCompat.setDecorFitsSystemWindows(window, false)

        //region Toolbar Handling
        binding = ActivitySalesShareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.applySystemBarInsetsPadding()

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.apply {

            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(Constant.SalesTitle)
        }
        // Initialize contentBinding for the included layout
        includedBinding = ContentSalesShareBinding.bind(binding.includeSalesShare.root)

        //endregion


        //region Handle Data

         //Note : getParceableSalesProductData rerun Sequntially, this works fine  bec
         // since it doesn't perform any suspend or asynchronous operations.
        getParceableSalesProductData()

        //Mark : all method below run async operation
        lifecycleScope.launch {
            try {
                // Process bitmaps and combine images
                processImages()
            } catch (e: Exception) {
                Log.d(Constant.TAG,"Error" + e.message.toString())
            }
        }
        //endregion


        if (!checkPermission()) {
            requestPermission()
        }


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Call finish() to close the activity
                this@SalesShareActivity.finish()
            }
        })
    }


    //region onStartMethod fo WebEnage
    override fun onStart() {
        super.onStart()

        WebEngage.get().analytics().screenNavigated("SalesShare Screen")
    }

    //endregion


    //region Handling Parceable Data came through Sales Detail Activity
    fun getParceableSalesProductData(){

        //region get parcealize salesProductEntity from SalesMaterial main page
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Use the new method for Android 13 and above
            intent.getParcelableExtra(Constant.PRODUCT_ID, SalesMateriaProdEntity::class.java)
                ?.let { entity ->
                    salesProductEntity = entity
                }

            intent.getParcelableExtra(Constant.DOC_DATA, DocEntity::class.java)
                ?.let { entity ->
                    docsEntity = entity
                }


        } else {
            // Fallback for older Android versions
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<SalesMateriaProdEntity>(Constant.PRODUCT_ID)?.let { entity ->
                salesProductEntity = entity
            }
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<DocEntity>(Constant.DOC_DATA)?.let { entity ->
                docsEntity = entity
            }
        }
        //endregion
    }
    //endregion


    //region Process Image
    //************************************************************************************************
    //Mark :a> we get POSPBitmap and FBABitmap by byteArray transfer from SalesDetail Activity

   //  b> then according to Sales Product Id we have take decion ie either POSPBitmap or FBABitmap add
   //   at footer of Sales material Image
    //************************************************************************************************
    private suspend fun processImages() {
        // First process the bitmaps
        processIntentBitmaps()

        // Then process combined image using the parcelable data we already have
        processCombinedImage()
    }

    private suspend fun processIntentBitmaps() = withContext(Dispatchers.Default) {
        // Process POSP bitmap
        intent.getByteArrayExtra(Constant.POSP_IMAGE)?.let { bytes ->
            POSPBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

        // Process FBA bitmap
        intent.getByteArrayExtra(Constant.FBA_IMAGE)?.let { bytes ->
            FBABitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
    }

    private suspend fun processCombinedImage(){

            viewModel.retrieveSalesBitmap(
                salesProductId = salesProductEntity.Product_Id,
                docsEntity = docsEntity,
                POSPBitmap = POSPBitmap,
                FBABitmap = FBABitmap
            )

            val result = viewModel.combinedImage

            if (result == null) {
                Glide.with(this@SalesShareActivity)
                    .asBitmap()
                    .load(docsEntity.image_path)
                    .placeholder(ContextCompat.getDrawable(this@SalesShareActivity, R.drawable.finmart_placeholder))
                    .into(includedBinding.ivProduct)
            } else {
                includedBinding.ivProduct.setImageBitmap(result)
            }



    }

    //endregion


    //region Share SalesImage
    fun showShareProduct() {
        if (viewModel.combinedImage != null) datashareList(
            this@SalesShareActivity,
            viewModel.combinedImage,
            "PolicyBossPro",
            ""
        )

    }

    //endregion

    private fun checkPermission(): Boolean {

        val WRITE_EXTERNAL = ActivityCompat.checkSelfPermission(applicationContext, perms[0])
        val READ_EXTERNAL = ActivityCompat.checkSelfPermission(applicationContext, perms[1])
        val READ_MEDIA_IMAGE = ActivityCompat.checkSelfPermission(applicationContext, perms[2])
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            WRITE_EXTERNAL == PackageManager.PERMISSION_GRANTED && READ_MEDIA_IMAGE == PackageManager.PERMISSION_GRANTED
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            WRITE_EXTERNAL == PackageManager.PERMISSION_GRANTED && READ_EXTERNAL == PackageManager.PERMISSION_GRANTED
        } else {
            WRITE_EXTERNAL == PackageManager.PERMISSION_GRANTED && WRITE_EXTERNAL == PackageManager.PERMISSION_GRANTED && READ_EXTERNAL == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            perms,
            Constant.PERMISSION_STORAGE_CONSTANT
        )
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {

            showShareProduct()
        } else {
            // Permission denied, show an error message or take appropriate action

            showAlert("Storage permission is required to share the image")
        }
    }



    //region Option-Menu

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.share_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_share -> {
                showShareProduct()
            }
            R.id.action_home -> {
                val intent = Intent(this, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("MarkTYPE", "FROM_HOME")
                }
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //endregion



}