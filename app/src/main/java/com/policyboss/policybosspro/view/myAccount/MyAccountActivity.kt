package com.policyboss.policybosspro.view.myAccount

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.policyboss.demoandroidapp.Utility.ExtensionFun.applySystemBarInsetsPadding
import com.policyboss.policybosspro.BaseActivity
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.analytics.WebEngageAnalytics
import com.policyboss.policybosspro.core.APIState
import com.policyboss.policybosspro.core.response.profile.AccountDtlEntity
import com.policyboss.policybosspro.core.viewModel.profile.ProfileViewModel
import com.policyboss.policybosspro.databinding.ActivityMyAccountBinding
import com.policyboss.policybosspro.databinding.ContentMyaccountBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utility.Utility
import com.policyboss.policybosspro.utility.UtilityNew
import com.policyboss.policybosspro.utils.AppPermission.AppPermissionManager
import com.policyboss.policybosspro.utils.AppPermission.PermissionHandler
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.utils.hideKeyboard
import com.policyboss.policybosspro.utils.showToast
import com.webengage.sdk.android.User
import com.webengage.sdk.android.WebEngage
import com.webengage.sdk.android.utils.Gender
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyAccountActivity : BaseActivity() , View.OnClickListener{

    //region Decleration
    private lateinit var binding: ActivityMyAccountBinding

    // Initialize contentBinding for the included layout
    private lateinit var includedBinding: ContentMyaccountBinding // For the included layout


    private val viewModel by viewModels<ProfileViewModel>()
    @Inject
    lateinit var prefsManager: PolicyBossPrefsManager

    var accountDtlEntity : AccountDtlEntity? =  null

    var ACCOUNT_TYPE = Constant.ProfileSaving

    var type = 0
    lateinit var weUser: User

   // region Layout Declere layout
    private lateinit var llMyProfile: LinearLayout
    private lateinit var llAddress: LinearLayout
    private lateinit var llBankDetail: LinearLayout
    private lateinit var llDocumentUpload: LinearLayout
    private lateinit var llPosp: LinearLayout
    private lateinit var llAbout: LinearLayout
    private lateinit var llNotify: LinearLayout


    private lateinit var rlMyProfile: RelativeLayout
    private lateinit var rlAddress: RelativeLayout
    private lateinit var rlBankDetail: RelativeLayout
    private lateinit var rlDocumentUpload: RelativeLayout
    private lateinit var rlPOSP: RelativeLayout
    private lateinit var rlAbout: RelativeLayout
    private lateinit var rlNotify: RelativeLayout


    private lateinit var ivMyProfile: ImageView
    private lateinit var ivAddress: ImageView
    private lateinit var ivBankDetail: ImageView
    private lateinit var ivDocumentUpload: ImageView
    private lateinit var ivPOSP: ImageView
    private lateinit var ivProfile: ImageView
    private lateinit var ivAbout: ImageView
    private lateinit var ivNotify: ImageView

    //endregion

    private lateinit var pickVisualMediaLauncher: ActivityResultLauncher<PickVisualMediaRequest>

    lateinit var galleryLauncher: ActivityResultLauncher<String>
    lateinit var cameraLauncher: ActivityResultLauncher<Uri>

    private var imageUri: Uri? = null

    private var profileBitmap : Bitmap? = null

    private val perms = arrayOf(
        "android.permission.CAMERA",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.READ_MEDIA_IMAGES"
    )

    //endregion

    private lateinit var permissionHandler: PermissionHandler






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //region Toolbar Set

        // Opt into edge-to-edge drawing
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMyAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

       // binding.root.applySystemBarInsetsPadding()
        applyInsets()

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.apply {

            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle("MY PROFILE")
        }
        // Initialize contentBinding for the included layout
        includedBinding = ContentMyaccountBinding.bind(binding.includeMyaccount.root)

        //endregion

        //  loginResponseEntity = dbPersistanceController.getUserData();
        // loginEntity = dbPersistanceController.getUserData();
        weUser = WebEngage.get().user()

        // Initialize permission handler here
        permissionHandler = PermissionHandler(this@MyAccountActivity)

        initLayouts()

        setListener()

        bindAboutMe()

   //     apiGetProfileDetails()

        setAcctDtlInfo ()

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {

            // binding.imgProfile.setImageURI(null)
            if(imageUri != null) {

                val correctedBitmap = Utility.handleImageOrientation(imageUri!!,this@MyAccountActivity)

                // Make the bitmap circular
                val circularBitmap = correctedBitmap?.let {
                    Utility.getCircularBitmap(it)



                }
                includedBinding.ivUser.setImageBitmap(circularBitmap)
                includedBinding.ivUser.setImageBitmap(correctedBitmap)


                circularBitmap?.let {
                   profileBitmap = circularBitmap
                }
                // Set the circular bitmap to the ImageView



            }



        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {uri ->
            if (uri != null) {
                includedBinding.ivUser.load(uri) {
                    transformations(CircleCropTransformation()) // Apply circular transformation
                    placeholder(R.drawable.circle_placeholder)        // Optional: Placeholder while loading
                    error(R.drawable.circle_placeholder)        // Optional: Error placeholder
                }

                val selectedBitmap = Utility.getBitmapFromUri(contentResolver, uri)
                val circularBitmap = selectedBitmap?.let {
                    Utility.getCircularBitmap(it)

                }
                circularBitmap?.let {
                    profileBitmap = circularBitmap
                }

            }
        }

        // Register launcher for Android 13+ (API 33 and above) using PickVisualMedia
        pickVisualMediaLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                includedBinding.ivUser.load(uri) {
                    transformations(CircleCropTransformation()) // Apply circular transformation
                    placeholder(R.drawable.circle_placeholder)        // Optional: Placeholder while loading
                    error(R.drawable.circle_placeholder)        // Optional: Error placeholder
                }

                val selectedBitmap = Utility.getBitmapFromUri(contentResolver, uri)
                val circularBitmap = selectedBitmap?.let {
                    Utility.getCircularBitmap(it)

                }
                circularBitmap?.let {
                    profileBitmap = circularBitmap
                }
            }
        }


        // Initialize launchers in onCreate or a similar lifecycle method

    }


    private fun applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            // ✅ push appbar down below status bar
            binding.appbar.setPadding(
                binding.appbar.paddingLeft,
                statusBars.top,
                binding.appbar.paddingRight,
                binding.appbar.paddingBottom
            )

            // ✅ push bottom content above navigation bar
            binding.includeMyaccount.root.setPadding(
                binding.includeMyaccount.root.paddingLeft,
                binding.includeMyaccount.root.paddingTop,
                binding.includeMyaccount.root.paddingRight,
                navBars.bottom
            )

            insets
        }
    }


    private fun bindAboutMe() {
        val userConstantEntity = prefsManager.getUserConstantEntity()

        with(includedBinding){

            tvName.text = prefsManager.getName() // Safe call with null fallback
            tvFbaCode.text = "${prefsManager.getFBAID()}" // String interpolation for conversion

            userConstantEntity?.let {
                tvPospNo.text = prefsManager.getSSID()// Only set if SSID is not null
                tvLoginId.text = prefsManager.getEmailId()// Use elvis operator to avoid null values
                //tvPospStatus.text = it.POSP_STATUS ?: ""

                txtManagerName.text = it.ManagName ?: ""
                txtManagerMobile.text = it.MangMobile ?: ""
                txtManagerEmail.text = it.MangEmail ?: ""

                txtSupportMobile.text = it.SuppMobile ?: ""
                txtSupportEmail.text = it.SuppEmail ?: ""
            }
        }

    }


    fun apiGetProfileDetails(){

        viewModel.getProfileDetails()

        observeResponse()
    }

    fun docUpload(){

        val file = Utility.saveImageToStorage(profileBitmap!!, "ProfileImage", this@MyAccountActivity)
        val part = Utility.getMultipartImage(file)
        val body = Utility.getBody(
              FbaID = prefsManager.getFBAID() ,
            DocType = "1",
            DocName = "Photo",
            ssid = prefsManager.getSSID(),
            appVersion = prefsManager.getAppVersion(),
            deviceCode = prefsManager.getDeviceID()

        )

    }


    private fun initLayouts() {

        llMyProfile = includedBinding.llMyProfile
        llAddress = includedBinding.llAddress
        llBankDetail = includedBinding.llBankDetail
        llDocumentUpload = includedBinding.llDocumentUpload
        llPosp = includedBinding.llPosp
        llAbout = includedBinding.llAbout
        llNotify = includedBinding.llNotify

        rlMyProfile = includedBinding.rlMyProfile
        rlAddress = includedBinding.rlAddress
        rlBankDetail = includedBinding.rlBankDetail
        rlDocumentUpload = includedBinding.rlDocumentUpload
        rlPOSP = includedBinding.rlPOSP
        rlAbout = includedBinding.rlAbout
        rlNotify = includedBinding.rlNotify


        ivMyProfile = includedBinding.ivMyProfile
        ivAddress = includedBinding.ivAddress
        ivBankDetail = includedBinding.ivBankDetail
        ivDocumentUpload = includedBinding.ivDocumentUpload
        ivPOSP = includedBinding.ivPOSP
        ivProfile = includedBinding.ivProfile
        ivAbout = includedBinding.ivAbout
        ivNotify = includedBinding.ivNotify


        includedBinding.apply {
           llMyProfile.visibility = View.GONE
           llAddress.visibility = View.GONE
           llBankDetail.visibility = View.GONE
           llDocumentUpload.visibility = View.GONE
           hideAllLayouts(llMyProfile, ivMyProfile)
       }

    }

    private fun setListener() {

        with(includedBinding) {


            rlMyProfile.setOnClickListener(this@MyAccountActivity)
            rlAddress.setOnClickListener(this@MyAccountActivity)

            rlBankDetail.setOnClickListener(this@MyAccountActivity)


            rlDocumentUpload.setOnClickListener(this@MyAccountActivity)


            rlPOSP.setOnClickListener(this@MyAccountActivity)


            rlAbout.setOnClickListener(this@MyAccountActivity)


            ivPOSP.setOnClickListener(this@MyAccountActivity)

            ivDocumentUpload.setOnClickListener(this@MyAccountActivity)
            ivBankDetail.setOnClickListener(this@MyAccountActivity)

            ivAddress.setOnClickListener(this@MyAccountActivity)
            ivMyProfile.setOnClickListener(this@MyAccountActivity)

            ivAbout.setOnClickListener(this@MyAccountActivity)

            rlNotify.setOnClickListener(this@MyAccountActivity)
            ivNotify.setOnClickListener(this@MyAccountActivity)

           ivProfile.setOnClickListener(this@MyAccountActivity)
            ivPhotoCam.setOnClickListener(this@MyAccountActivity)

            ivManagerMobile.setOnClickListener(this@MyAccountActivity)
            ivManagerEmail.setOnClickListener(this@MyAccountActivity)
            ivSupportMobile.setOnClickListener(this@MyAccountActivity)
            ivSupportEmail.setOnClickListener(this@MyAccountActivity)
//            ivPhotoView.setOnClickListener(this@MyAccountActivity)
//            ivPanCam.setOnClickListener(this@MyAccountActivity)
//            ivPanView.setOnClickListener(this@MyAccountActivity)
//
//            ivCancelCam.setOnClickListener(this@MyAccountActivity)
//            ivCancelView.setOnClickListener(this@MyAccountActivity)
//            ivAadharCam.setOnClickListener(this@MyAccountActivity)
//            ivAadharView.setOnClickListener(this@MyAccountActivity)
//

//
//            btnSave.setOnClickListener(this@MyAccountActivity)
//            txtSaving.setOnClickListener(this@MyAccountActivity)
//            txtCurrent.setOnClickListener(this@MyAccountActivity)


        }
    }

    //region WeBView Handling
    override fun onStart() {
        super.onStart()

        WebEngage.get().analytics().screenNavigated("MyAccount Screen")
    }

    private fun setUserInfoToWebEngAnalytic() {
        weUser.setPhoneNumber(prefsManager.getMobileNo())
        weUser.setEmail(prefsManager.getEmailId())



        if (prefsManager.getGender()=="M") {
            weUser.setGender(Gender.MALE)
        } else {
            weUser.setGender(Gender.FEMALE)
        }

        weUser.setBirthDate("" + Utility.getDateFromWeb1(prefsManager.getBirthdate()?:""))

    }

    private fun trackDocUploadEvent(strDocType: String) {
        // Create event attributes
        val eventAttributes = mutableMapOf<String, Any>()
        eventAttributes["Section"] = "My Account"
        eventAttributes["Document Type"] = strDocType

        // Track the document upload event using WebEngageHelper
        WebEngageAnalytics.getInstance().trackEvent("Document Upload Initiated", eventAttributes)
    }

    private fun trackDocUploadSuccessEvent(strDocType: String) {
        // Create event attributes
        val eventAttributes = mutableMapOf<String, Any>()
        eventAttributes["Section"] = "My Account"
        eventAttributes["Document Type"] = strDocType

        // Track the document upload success event using WebEngageHelper
        WebEngageAnalytics.getInstance().trackEvent("Document Uploaded Successfully", eventAttributes)
    }


    private fun trackMyAccountSubmitEvent() {
        // Create event attributes
        val eventAttributes = mutableMapOf<String, Any>()
        eventAttributes["Section"] = "My Account"

        // Track the event using WebEngageHelper
        WebEngageAnalytics.getInstance().trackEvent("Bank Details Submitted", eventAttributes)
    }



    //endregion




    //region ShowAccount Details
    private fun setAcctDtlInfo1(accountDtlEntity: AccountDtlEntity) {
        with(includedBinding) {
            etSubHeading.setText(accountDtlEntity.Designation ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)
            etMobileNo.setText(accountDtlEntity.EditMobiNumb ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)
            etEmailId.setText(accountDtlEntity.EditEmailId?.trim() ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)
            
            etAddress1.setText(accountDtlEntity.Address_1?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)

            etAddress2.setText(accountDtlEntity.Address_2 ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)
            etAddress3.setText(accountDtlEntity.Address_3 ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)
            etPincode.setText(accountDtlEntity.PinCode ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)
            etCity.setText(accountDtlEntity.City ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)
            etState.setText(accountDtlEntity.StateName ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)

            etAccountHolderName.setText(accountDtlEntity.LoanName ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)
            etPAN.setText(accountDtlEntity.Loan_PAN ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)
            etAadhaar.setText(accountDtlEntity.Loan_Aadhaar ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)
            etBankAcNo.setText(accountDtlEntity.Loan_BankAcNo ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)
            etIfscCode.setText(accountDtlEntity.Loan_IFSC ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)
            etMicrCode.setText(accountDtlEntity.Loan_MICR ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)
            etBankName.setText(accountDtlEntity.Loan_BankName ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)
            etBankBranch.setText(accountDtlEntity.Loan_BankBranch ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)
            etBankCity.setText(accountDtlEntity.Loan_BankCity ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)

            when (accountDtlEntity.Loan_Account_Type) {
                "SAVING" -> setSavingAcc()
                "CURRENT" -> setCurrentAcc()
            }

            etSubHeadingPosp.setText(accountDtlEntity.DisplayDesignation?.uppercase() ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)
            etMobileNoPosp.setText(accountDtlEntity.DisplayPhoneNo ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)
            etEmailIdPosp.setText(accountDtlEntity.DisplayEmail?.trim() ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)

            accountDtlEntity?.doc_available.let { docList ->
                if (!docList.isNullOrEmpty()) {
                    for (docEntity in docList) {
                        if (!docEntity.FileName.isNullOrEmpty()) {
                            setDocumentUpload(docEntity.DocType, docEntity.FileName)
                        }
                    }
                }
            }

           // setUserInfoToWebEngAnalytic()
        }
    }


    //endregion

    private fun setAcctDtlInfo() {
        with(includedBinding) {

            var accountDtlEntity : AccountDtlEntity? =  null
        //    etSubHeading.setText(accountDtlEntity.Designation ?.ifEmpty { Constant.DEFAULT } ?: Constant.DEFAULT)
            etMobileNo.setText(prefsManager.getMobileNo())
          //  etEmailId.setText(prefsManager.getLoginHorizonResponse()?.EMP?.Email_Id?: "")
            etEmailId.setText(prefsManager.getEmailId())

          etAddress1.setText(prefsManager.getPermanant_Add1())

            etAddress2.setText(prefsManager.getPermanant_Add2())
          etAddress3.setText(prefsManager.getPermanant_Add3())

            etPincode.setText("" + prefsManager.getPermanant_Pincode())
            etCity.setText(prefsManager.getPermanant_City())
           etState.setText(prefsManager.getPermanant_State())





//            accountDtlEntity?.doc_available.let { docList ->
//                if (!docList.isNullOrEmpty()) {
//                    for (docEntity in docList) {
//                        if (!docEntity.FileName.isNullOrEmpty()) {
//                            setDocumentUpload(docEntity.DocType, docEntity.FileName)
//                        }
//                    }
//                }
//            }


            //temp
           setUserInfoToWebEngAnalytic()
        }
    }



    private fun setSavingAcc() {
        ACCOUNT_TYPE = "SAVING"
        with(includedBinding) {
            txtSaving.setBackgroundResource(R.drawable.customeborder_blue)
            txtSaving.setTextColor(ContextCompat.getColor(this@MyAccountActivity, R.color.colorPrimary))

            txtCurrent.setBackgroundResource(R.drawable.customeborder)
            txtCurrent.setTextColor(ContextCompat.getColor(this@MyAccountActivity, R.color.description_text))
        }
    }

    private fun setCurrentAcc() {
        ACCOUNT_TYPE = "CURRENT"
        with(includedBinding) {
            txtCurrent.setBackgroundResource(R.drawable.customeborder_blue)
            txtCurrent.setTextColor(ContextCompat.getColor(this@MyAccountActivity, R.color.colorPrimary))

            txtSaving.setBackgroundResource(R.drawable.customeborder)
            txtSaving.setTextColor(ContextCompat.getColor(this@MyAccountActivity, R.color.description_text))
        }
    }

    private fun setDocumentUpload(fileType: Int, fileName: String?) {

        with(includedBinding) {
            if (fileType == 1 || fileType == 2) {
                if (!fileName.isNullOrEmpty()) {
                    ivPhotoView.tag = fileName
                    ivPhotoView.visibility = View.VISIBLE
                    ivPhoto.setImageResource(R.drawable.doc_uploaded)


                    ivUser.load(fileName) {
                        placeholder(R.drawable.circle_placeholder)
                        error(R.drawable.finmart_user_icon)  // Use this if the image fails to load
                        crossfade(true)
                        transformations(CircleCropTransformation())  // Circular transformation
                        size(120, 120)  // Override image size
                        memoryCachePolicy(CachePolicy.DISABLED)  // Disable memory caching
                        diskCachePolicy(CachePolicy.DISABLED)    // Disable disk caching
                    }
                } else {


                    ivUser.load(R.drawable.finmart_user_icon) {
                        placeholder(R.drawable.finmart_user_icon)  // Placeholder while loading
                        error(R.drawable.finmart_user_icon)        // Error image if loading fails
                        crossfade(true)                            // Crossfade animation
                        transformations(CircleCropTransformation()) // Circular transformation
                        size(120, 120)                             // Override image size
                        memoryCachePolicy(CachePolicy.DISABLED)     // Disable memory caching
                        diskCachePolicy(CachePolicy.DISABLED)       // Disable disk caching
                    }
                }
            }

            when (fileType) {
                2 -> {
                    ivPhoto.setImageResource(R.drawable.doc_uploaded)
                    ivPhotoView.tag = fileName
                    ivPhotoView.visibility = View.VISIBLE
                }
                3 -> {
                    ivPan.setImageResource(R.drawable.doc_uploaded)
                    ivPanView.tag = fileName
                    ivPanView.visibility = View.VISIBLE
                }
                4 -> {
                    ivCancel.setImageResource(R.drawable.doc_uploaded)
                    ivCancelView.tag = fileName
                    ivCancelView.visibility = View.VISIBLE
                }
                5 -> {
                    ivAadhar.setImageResource(R.drawable.doc_uploaded)
                    ivAadharView.tag = fileName
                    ivAadharView.visibility = View.VISIBLE
                }
            }
        }
    }

    //endregion


    private fun startCamera() {

        // Camera Start Using Camera Launcher
//        imageUri = Utility.createImageUri(this@MyAccountActivity)
//        imageUri?.let {
//            cameraLauncher.launch(it)
//        }
    }


    private fun openGallery() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 and above: Use PickVisualMedia
            val mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
            val request = PickVisualMediaRequest.Builder()
                .setMediaType(mediaType)
                .build()
            pickVisualMediaLauncher.launch(request)
        } else {
            // Android 12 and below: Use GetContent
            if (hasGalleryPermission()) {
                galleryLauncher.launch("image/*") // Request to pick an image
            } else {
                requestGalleryPermission()
            }
        }
    }

    //region permission Handling

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            perms,
            Constant.PERMISSION_CAMERA_STORAGE_CONSTANT
        )
    }

    private fun checkPermission(): Boolean {
        val camera = ActivityCompat.checkSelfPermission(applicationContext, perms[0])
        val writeExternal = ActivityCompat.checkSelfPermission(applicationContext, perms[1])
        val readExternal = ActivityCompat.checkSelfPermission(applicationContext, perms[2])
        val readMediaImage = ActivityCompat.checkSelfPermission(applicationContext, perms[3])

        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                camera == PackageManager.PERMISSION_GRANTED &&
                        readMediaImage == PackageManager.PERMISSION_GRANTED
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                camera == PackageManager.PERMISSION_GRANTED &&
                        readExternal == PackageManager.PERMISSION_GRANTED
            }
            else -> {
                camera == PackageManager.PERMISSION_GRANTED &&
                        writeExternal == PackageManager.PERMISSION_GRANTED &&
                        readExternal == PackageManager.PERMISSION_GRANTED
            }
        }
    }


    private fun checkRationalePermission(): Boolean {
        val camera = ActivityCompat.shouldShowRequestPermissionRationale(this, perms[0])
        val writeExternal = ActivityCompat.shouldShowRequestPermissionRationale(this, perms[1])
        val readExternal = ActivityCompat.shouldShowRequestPermissionRationale(this, perms[2])

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            camera || readExternal
        } else {
            camera || writeExternal || readExternal
        }
    }



    private fun hasGalleryPermission(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            // No permission needed for Android 13+
            true
        }
    }

    // Function to request permission for Android 12 and below
    private fun requestGalleryPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constant.PERMISSION_STORAGE_CONSTANT)
        }
    }

    // Handle permission result for Android 12 and below
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.PERMISSION_STORAGE_CONSTANT && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, open gallery
            galleryLauncher.launch("image/*")
        }
    }

    //endregion



    private fun galleryCamPopUp(strHeader: String) {

        permissionHandler.checkAndRequestPermissions(

            AppPermissionManager.PermissionType.CAMERA_AND_STORAGE,
            onResult = { granted ->
                if (granted) {
                    // Open camera or Gallery

                    UtilityNew.showCameraGalleryPopUp(
                        context = this@MyAccountActivity,
                        strHeader =  strHeader,
                        onCameraClick = ::onCameraClick,
                        onGalleryClick = ::onGalleryClick
                    )

                } else {

                    //showAlert("App need perission")
                }
            },
            onPermanentlyDenied = { permissions ->
                // Show dialog suggesting to open app settings
                UtilityNew.openSetting(this@MyAccountActivity)
            }
        )

    }

    fun onCameraClick() {

       // startCamera()
    }
    fun onGalleryClick() {

      //  openGallery()

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Finish the activity when the Up button is pressed
                this@MyAccountActivity.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeResponse() {

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {

                    viewModel.ProfileDtlResponse.collect { event ->

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

                                    it.data?.MasterData?.let { accountDtlEntity ->

                                        //setupSalesMaterialAdapter(lstSalesProdEntity)

                                      //  setAcctDtlInfo(accountDtlEntity = accountDtlEntity.get(0))

                                    }
                                }
                            }


                        }


                    }
                }


                viewModel.uploadDocumentResponse.collect { event ->

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

                                it.data?.MasterDataEntity?.let { it ->

                                    //setupSalesMaterialAdapter(lstSalesProdEntity)

                                    showToast("Document Uploaded Successfully!!")

                                }
                            }
                        }


                    }


                }


            }
        }


    }


   // region manage layout

    private fun manageMainLayouts(
        visibleLayout: LinearLayout,
        hideLayout1: LinearLayout,
        hideLayout2: LinearLayout,
        hideLayout3: LinearLayout,
        hideLayout4: LinearLayout,
        hideLayout5: LinearLayout,
        hideLayout6: LinearLayout
    ) {
        if (visibleLayout.visibility == View.GONE) {
            visibleLayout.visibility = View.VISIBLE

            // You can uncomment and modify the animation if needed
            // visibleLayout.animate().translationY(visibleLayout.height.toFloat()).alpha(1.0f).setDuration(1000)

            hideLayout1.visibility = View.GONE
            hideLayout2.visibility = View.GONE
            hideLayout3.visibility = View.GONE
            hideLayout4.visibility = View.GONE
            hideLayout5.visibility = View.GONE
            hideLayout6.visibility = View.GONE
        } else {
            visibleLayout.visibility = View.GONE
            // Uncomment if you need animation
            // visibleLayout.animate().translationY(visibleLayout.height.toFloat()).alpha(0.0f).setDuration(1000)
        }
    }


    //Mark :---> Varargs: The manageImages method now uses vararg for upImages,
    // which allows you to pass any number of ImageView parameters.
    private fun manageImages(
        clickedLayout: LinearLayout,
        downImage: ImageView,
        vararg upImages: ImageView // Using vararg to simplify parameters
    ) {
        if (clickedLayout.visibility == View.GONE) {
            downImage.setImageDrawable(getDrawable(R.drawable.down_arrow))

            upImages.forEach { it.setImageDrawable(getDrawable(R.drawable.down_arrow)) }
        } else {
            downImage.setImageDrawable(getDrawable(R.drawable.up_arrow))

            upImages.forEach { it.setImageDrawable(getDrawable(R.drawable.down_arrow)) }
        }
    }

    private fun hideAllLayouts(linearLayout: LinearLayout, imageView: ImageView) {
        if (linearLayout.visibility == View.GONE) {

            with(includedBinding) {

                ivMyProfile.setImageDrawable(getDrawable(R.drawable.down_arrow))
                llMyProfile.visibility = View.GONE

                ivAddress.setImageDrawable(getDrawable(R.drawable.down_arrow))
                llAddress.visibility = View.GONE

                ivPOSP.setImageDrawable(getDrawable(R.drawable.down_arrow))
                llPosp.visibility = View.GONE

                ivAbout.setImageDrawable(getDrawable(R.drawable.down_arrow))
                llAbout.visibility = View.GONE

                linearLayout.visibility = View.GONE
                imageView.setImageDrawable(getDrawable(R.drawable.down_arrow))
            }

            // Hide all layouts

        } else {
            linearLayout.visibility = View.GONE
            imageView.setImageDrawable(getDrawable(R.drawable.down_arrow))
        }
    }


    //endregion

    fun ConfirmAlert(title: String, body: String, mobile: String) {
        try {
            AlertDialog.Builder(this).apply {
                setTitle(title)
                setMessage(body)
                setPositiveButton("Call") { _, _ ->
                    val intentCalling = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$mobile")
                    }
                    startActivity(intentCalling)
                }
                setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                create().apply {
                    setCancelable(false)
                    setCanceledOnTouchOutside(false)
                }.show()
            }
        } catch (ex: Exception) {
            Toast.makeText(this, "Please try again..", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onClick(view: View?) {

        hideKeyboard(binding.root)

        includedBinding.mainScrollView.postDelayed({
            includedBinding.mainScrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }, 600)
        when (view?.id) {



            R.id.ivMyProfile, R.id.rlMyProfile -> {
                manageMainLayouts(llMyProfile, llAddress, llBankDetail, llDocumentUpload, llPosp, llAbout, llNotify)
                manageImages(llMyProfile, ivMyProfile, ivAddress, ivBankDetail, ivDocumentUpload, ivPOSP, ivAbout, ivNotify)
            }
            R.id.ivAddress, R.id.rlAddress -> {
                manageMainLayouts(llAddress, llMyProfile, llBankDetail, llDocumentUpload, llPosp, llAbout, llNotify)
                manageImages(llAddress, ivAddress, ivMyProfile, ivBankDetail, ivDocumentUpload, ivPOSP, ivAbout, ivNotify)
                //saveProfile()
            }
            R.id.ivBankDetail, R.id.rlBankDetail -> {
                manageMainLayouts(llBankDetail, llMyProfile, llAddress, llDocumentUpload, llPosp, llAbout, llNotify)
                manageImages(llBankDetail, ivBankDetail, ivAddress, ivMyProfile, ivDocumentUpload, ivPOSP, ivAbout, ivNotify)
                //saveAddress()
            }
            R.id.ivDocumentUpload, R.id.rlDocumentUpload -> {
                manageMainLayouts(llDocumentUpload, llBankDetail, llMyProfile, llAddress, llPosp, llAbout, llNotify)
                manageImages(llDocumentUpload, ivDocumentUpload, ivBankDetail, ivAddress, ivMyProfile, ivPOSP, ivAbout, ivNotify)
               // saveBankDtl()
            }
            R.id.ivPOSP, R.id.rlPOSP -> {
                manageMainLayouts(llPosp, llDocumentUpload, llBankDetail, llMyProfile, llAddress, llAbout, llNotify)
                manageImages(llPosp, ivPOSP, ivDocumentUpload, ivBankDetail, ivAddress, ivMyProfile, ivAbout, ivNotify)
                //savePOSP()
            }
            R.id.ivAbout, R.id.rlAbout -> {
                manageMainLayouts(llAbout, llPosp, llDocumentUpload, llBankDetail, llMyProfile, llAddress, llNotify)
                manageImages(llAbout, ivAbout, ivBankDetail, ivAddress, ivMyProfile, ivDocumentUpload, ivPOSP, ivNotify)
            }
            R.id.ivNotify, R.id.rlNotify -> {
                manageMainLayouts(llNotify, llAbout, llPosp, llDocumentUpload, llBankDetail, llMyProfile, llAddress)
                manageImages(llNotify, ivNotify, ivAbout, ivBankDetail, ivAddress, ivMyProfile, ivDocumentUpload, ivPOSP)
            }
            R.id.txtSaving -> setSavingAcc()
            R.id.txtCurrent -> setCurrentAcc()
            R.id.ivProfile -> {
                type = 1
                trackDocUploadEvent(getString(R.string.popup_Profile))
                galleryCamPopUp(getString(R.string.popup_Profile))
            }

            R.id.ivPhotoView -> {
               // createBitmapFromURL(ivPhotoView.tag.toString(), "FBA PHOTOGRAPH").execute()
            }

            R.id.ivManagerMobile ->{

                ConfirmAlert("Calling",
                    resources.getString(R.string.RM_Calling) + " " + prefsManager.getUserConstantEntity()?.ManagName?:"",
                    prefsManager.getUserConstantEntity()?.MangMobile?: ""
                )


            }

            R.id.ivManagerEmail ->{

                composeEmail(
                    address= prefsManager.getUserConstantEntity()?.MangEmail?:"",
                    subject = ""

                )
            }

            R.id.ivSupportMobile ->{


                ConfirmAlert(
                    "Calling",
                    resources.getString(R.string.Support_Calling),
                    prefsManager.getUserConstantEntity()?.SuppMobile?: ""

                )


            }

            R.id.ivSupportEmail ->{
                composeEmail(
                    address= prefsManager.getUserConstantEntity()?.SuppEmail?:"",
                    subject = ""

                )

            }


        }

        }


}