package com.policyboss.policybosspro.view.syncContact.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.policyboss.policybosspro.BaseActivity
import com.policyboss.policybosspro.R
import com.policyboss.policybosspro.analytics.WebEngageAnalytics
import com.policyboss.policybosspro.core.RetroHelper

import com.policyboss.policybosspro.core.requestbuilder.syncContact.SaveCheckboxRequestEntity
import com.policyboss.policybosspro.core.response.horizonResponse.sync_contact_agree
import com.policyboss.policybosspro.databinding.ActivityWelcomeSyncContactKotlinBinding
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant
import com.policyboss.policybosspro.utils.NetworkUtils
import com.policyboss.policybosspro.view.myAccount.MyAccountActivity
import com.policyboss.policybosspro.webview.CommonWebViewActivity

import com.webengage.sdk.android.WebEngage
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class WelcomeSyncContactActivityKotlin : BaseActivity() , View.OnClickListener {

    lateinit var binding: ActivityWelcomeSyncContactKotlinBinding


    private lateinit var dialogAnim : Dialog
    val TAG = "HORIZONEMP"

    private lateinit var dialog: Dialog
    lateinit var viewPager: ViewPager
    lateinit var myViewPagerAdapter: MyViewPagerAdapter
    private lateinit var onPageChangeListener: ViewPager.OnPageChangeListener

    lateinit var dotsLayout: LinearLayout
    lateinit var  layouts: IntArray
    lateinit var btnNext: Button
    lateinit var dot1: ImageView
    lateinit var dot2: ImageView
    lateinit var dot3: ImageView
    lateinit var txtprivacy: TextView
    lateinit  var txtterm: TextView


    var current = 0
    lateinit var btnchkagree: CheckBox
    lateinit var btnchkcommunication_sms: CheckBox
    lateinit var btnchktele_call: CheckBox

    lateinit  var ll_term: LinearLayout
    lateinit var  txtsetting: TextView

    var isContactSync_msg = 0


    lateinit var shareProdSyncDialog: AlertDialog
    var POSPNO = ""
    var FBAID = ""

    @Inject
    lateinit var prefManager : PolicyBossPrefsManager

    var perms = arrayOf(
        "android.permission.READ_CONTACTS",
        "android.permission.READ_CALL_LOG"
    )
    val READ_CONTACTS_CODE = 101

    override fun onStart() {
        super.onStart()
        val weAnalytics = WebEngage.get().analytics()
        weAnalytics.screenNavigated("Welcome Sync Contact Screen")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeSyncContactKotlinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialogAnim = Dialog(this)

        POSPNO = prefManager.getPOSPNo()

        FBAID = prefManager.getFBAID()

        viewPager = binding.viewPager
        init_widgets()
        setListener()
        myViewPagerAdapter =
            MyViewPagerAdapter(this@WelcomeSyncContactActivityKotlin)
        viewPager!!.adapter = myViewPagerAdapter


        showAnimDialog("Please Wait...")

        CoroutineScope(Dispatchers.IO).launch {
            try {

                getHorizonDetails()

            }catch (e: Exception){

                withContext(Dispatchers.Main) {
                    //   viewPager.visibility = View.VISIBLE
                    cancelAnimDialog()
                }
            }
        }

        onPageChangeListener = object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

                //addBottomDots(position);
                current = position

                setSelectedDot(position + 1)
                // changing the next button text 'NEXT' / 'GOT IT'
                // changing the next button text 'NEXT' / 'GOT IT'
                if (position == layouts.size - 1) {
                    // last page. make button text to GOT IT
                    btnNext.text = "GET STARTED"
                    //  btnNext.setVisibility(View.GONE);
                    btnNext.isEnabled = false
                    btnNext.alpha = 0.4f
                    btnNext.tag = 0
                    ll_term.visibility = View.VISIBLE
                    txtsetting.visibility = View.VISIBLE


                    // btnSkip.setVisibility(View.VISIBLE);
                } else {
                    // still pages are left
                    ll_term.visibility = View.GONE
                    txtsetting.visibility = View.GONE
                    //btnNext.setVisibility(View.VISIBLE);
                    btnNext.tag = 1
                    btnNext.alpha = 1f
                    btnNext.text = "NEXT"
                    btnchkagree.isChecked = false
                    btnNext.isEnabled = true
                    //  btnNext.setVisibility(View.GONE);
                    // btnSkip.setVisibility(View.VISIBLE);
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        }

        viewPage2Listener()
    }

    //region method of Welcome Page
    private fun init_widgets() {

        dot1 = binding.dot1
        dot2 = binding.dot2
        dot3 = binding.dot3

        layouts = intArrayOf(
            R.layout.sync_welcome_slide1,
            R.layout.sync_welcome_slide2,
            R.layout.sync_welcome_slide3
        )


        dotsLayout = binding.layoutDots

        btnNext = binding.btnNext
        btnchkagree = binding.chkagree
        btnchkcommunication_sms = binding.chkcommunicationSms
        btnchktele_call = binding.chkteleCall


        txtterm = binding.txtterm
        txtprivacy = binding.txtprivacy



        ll_term = binding.llTerm
        txtsetting = binding.txtsetting

        btnchkagree!!.tag = 0

        btnchkcommunication_sms!!.tag = 0
        btnchktele_call!!.tag = 0

//        btnNext.isEnabled = false
//        btnNext.alpha = 0.4f
        btnNext.tag = 0

        ll_term.visibility = View.GONE
        txtsetting.visibility =View.GONE
    }

    private fun setListener() {

        //    viewPager!!.addOnPageChangeListener(viewPagerPageChangeListener)
        btnNext.setOnClickListener(this)
        btnchkagree.setOnClickListener(this)

        btnchkcommunication_sms.setOnClickListener(this)
        btnchktele_call.setOnClickListener(this)

        txtprivacy.setOnClickListener(this)
        txtterm.setOnClickListener(this)

        txtsetting.setOnClickListener(this)

        // ll_term.setOnClickListener(this)
        //  ll_term.visibility = View.GONE
        btnchkagree!!.isChecked = false
        btnchkcommunication_sms!!.isChecked = false
        btnchktele_call!!.isChecked = false
    }

    private fun viewPage2Listener(){

        viewPager.addOnPageChangeListener(onPageChangeListener)
    }


    private fun setSelectedDot(current: Int) {

        dot1.setImageDrawable(ContextCompat.getDrawable(this@WelcomeSyncContactActivityKotlin,R.drawable.unselected_dot))
        dot2.setImageDrawable(ContextCompat.getDrawable(this@WelcomeSyncContactActivityKotlin,R.drawable.unselected_dot))
        dot3.setImageDrawable(ContextCompat.getDrawable(this@WelcomeSyncContactActivityKotlin,R.drawable.unselected_dot))

        when (current) {
            1 -> dot1.setImageDrawable(ContextCompat.getDrawable(this@WelcomeSyncContactActivityKotlin,R.drawable.indicator_active))
            2 -> dot2.setImageDrawable(ContextCompat.getDrawable(this@WelcomeSyncContactActivityKotlin,R.drawable.indicator_active))
            3 -> dot3.setImageDrawable(ContextCompat.getDrawable(this@WelcomeSyncContactActivityKotlin,R.drawable.indicator_active))
        }
    }


    private suspend fun getHorizonDetails(){

        lifecycleScope.launch {

            withContext(Dispatchers.IO) {


                var url =
                    "https://horizon.policyboss.com:5443/sync_contact/get_sync_contact_agreements?ss_id=" + POSPNO +
                            "&device_code=" + prefManager.getDeviceID() + "&app_version=" + prefManager.getAppVersion() + "&fbaid=" + FBAID


                Log.d(Constant.TAG,"URL: " + url)
                val resultRespAsync = async { RetroHelper.api.getSyncHorizonDetails(url) }
                val resultResp = resultRespAsync.await()


                if (resultResp.isSuccessful) {
                    cancelAnimDialog()
                    Log.d(TAG, resultResp.toString())

                    val responseBody = resultResp.body()

                    if (responseBody?.Status?.equals("Success", ignoreCase = true) == true) {

                        if (responseBody?.Msg is List<*>) {

                            val msgList = (responseBody.Msg as? List<*>)?.mapNotNull { item ->

                                when (item) {

                                    is Map<*, *> -> {

                                        try {
                                            sync_contact_agree(
//
                                                ss_id = (item["ss_id"] as? Number)?.toInt() ?: 0,
                                                is_sms = item["is_sms"] as? String ?: "",
                                                is_call = item["is_call"] as? String ?: "",
                                                fba_id = (item["fba_id"] as? Number)?.toInt() ?: 0,

                                                )
                                        } catch (e: Exception) {
                                            Log.e(
                                                TAG,
                                                "Error parsing sync_contact_agree: ${e.message}"
                                            )
                                            null
                                        }
                                    }

                                    else -> null
                                }
                            } ?: emptyList()

                            // Now you have a properly cast list
                            isContactSync_msg = msgList.size

                            if (msgList.isNotEmpty()) {
                                withContext(Dispatchers.Main) {
                                    val lastItem = msgList.lastOrNull()

                                    // Get the values for is_call and is_sms from the last item
                                    val is_call = lastItem?.is_call ?: ""
                                    val is_sms = lastItem?.is_sms ?: ""

                                    // Set the UI elements
                                    btnchktele_call.isChecked = (is_call == "yes")
                                    btnchkcommunication_sms.isChecked = (is_sms == "yes")

                                    if (!checkPermission()) {
                                        requestPermission()
                                    }
                                }
                            }
                        }
                    }


                }

                else {
                    // Handle the case when the response itself failed (e.g., network failure)
                    withContext(Dispatchers.Main) {
                        Log.d(TAG, resultResp.toString())
                        cancelAnimDialog()
                    }
                }



            }
        }

    }

    fun test(){

//        val responseBody = resultResp.body()
//
//
//        if (responseBody?.Status=="Success") {
//
//
//            // Handle Msg field based on its type (List or other)
//            when (responseBody.Msg) {
//
//                is List<*> -> { // Ensure Msg is a List
//
//                    //  val msgList = responseBody.Msg  as? List<sync_contact_agree> // Safe cast to List<sync_contact_agree>
//                    // Safely cast Msg to List<*> and then filter for sync_contact_agree objects
//                    val msgList = (responseBody.Msg as? List<sync_contact_agree>) ?: emptyList()
//
//                    isContactSync_msg = msgList?.size?:0
//
//                    withContext(Dispatchers.Main) {
//                        val lastItem = msgList?.lastOrNull()
//
//                        // Get the values for is_call and is_sms from the last item
//                        val is_call = lastItem?.is_call ?: ""
//                        val is_sms = lastItem?.is_sms ?: ""
//
//                        // Set UI elements
//                        btnchktele_call.isChecked = (is_call == "yes")
//                        btnchkcommunication_sms.isChecked = (is_sms == "yes")
//
//                        if (!checkPermission()) {
//                            requestPermission()
//                        }
//                    }
//
//                }
//
//                is String -> { // If Msg is an error message
//                    withContext(Dispatchers.Main) {
//                        Log.d(TAG, "Error Message: ${responseBody.Msg}")
//                        cancelAnimDialog() // Stop loader in case of error
//                    }
//                }
//
//                else -> {
//                    withContext(Dispatchers.Main) {
//                        Log.d(TAG, "Unexpected response structure")
//                        cancelAnimDialog() // Handle unknown structure gracefully
//                    }
//                }
//
//
//            }
//
//
//        }
//
//        else {
//            // Handle the case when the response itself failed (e.g., network failure)
//            withContext(Dispatchers.Main) {
//                Log.d(TAG, resultResp.toString())
//                cancelAnimDialog()
//            }
//        }
    }

    //region permission
    private fun checkPermission(): Boolean {
        val read_contact = ActivityCompat.checkSelfPermission(this@WelcomeSyncContactActivityKotlin, perms[0])
        val read_call_log = ActivityCompat.checkSelfPermission(this@WelcomeSyncContactActivityKotlin, perms[1])

        return (read_contact == PackageManager.PERMISSION_GRANTED) && (read_call_log == PackageManager.PERMISSION_GRANTED)
    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(this@WelcomeSyncContactActivityKotlin, perms, READ_CONTACTS_CODE)

    }
    //endregion
    fun showAnimDialog(msg: String? = ""){


        try {
            if (!this::dialog.isInitialized) {
                dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.progressdialog2_loading)
                dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setCancelable(false)

            }
            val txtMessage = dialog.findViewById<TextView>(R.id.txtMessage)

            txtMessage.text = msg


            //hide keyboard
            //view.context.hideKeyboard(view)

            dialog.let {
                if (!it.isShowing) {
                    it.show()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    fun cancelAnimDialog(){


        if (dialog != null) {
            dialog!!.dismiss()
        }
    }



    private suspend fun savecheckboxdetails(){

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {

                // var url =  "https://horizon.policyboss.com:5443/sync_contacts" + "/contact_entry"
                var url =
                    "https://horizon.policyboss.com:5443/postservicecall/sync_contacts/online_agreement"


                var smschk = "no"

                var telechk = "no"

                if (btnchkcommunication_sms!!.isChecked) {
                    smschk = "yes"
                } else {
                    smschk = "no"
                }

                if (btnchktele_call!!.isChecked) {
                    telechk = "yes"
                } else {
                    telechk = "no"
                }

                val saveCheckboxRequestEntity = SaveCheckboxRequestEntity(

                    fba_id = Integer.parseInt(FBAID),
                    is_sms = smschk,
                    is_call = telechk,
                    online_agreement = "online_agreement",
                    ss_id = Integer.parseInt(POSPNO),
                    app_version = prefManager.getAppVersion(),
                    device_code = prefManager.getDeviceID()

                )

                // val resultRespAsync1 =  RetroHelper.api.savecheckboxdetails(url,saveCheckboxRequestEntity)
                //  val resultResp = resultRespAsync.await()

                val resultRespAsync =
                    async { RetroHelper.api.savecheckboxdetails(url, saveCheckboxRequestEntity) }
                val resultResp = resultRespAsync.await()

                if (resultResp?.isSuccessful == true) {
                    // cancelAnimDialog()
                    Log.d(TAG, resultResp.toString())
                    // delay(8000)
                } else {

                    withContext(Dispatchers.Main) {
                        Log.d(TAG, resultResp.toString())
                        // viewPager.visibility = View.VISIBLE
                        // cancelAnimDialog()
                    }
                    //cancelAnimDialog()
                }

            }

        }

    }

    //endregion
    override fun onClick(view: View?) {

        when (view!!.getId()) {
            btnNext.id -> {

                if (NetworkUtils.isNetworkAvailable(this@WelcomeSyncContactActivityKotlin)) {

                    //     btnchktele!!.isChecked && btnchkcommunication!!.isChecked &&
                    // if ( btnchkagree!!.isChecked ) {
                    current = current + 1
                    if (current < layouts.size) {
                        //move to next screen
                        viewPager.currentItem = current
                    } else {

                        // For Submit : Get Started
                        CoroutineScope(Dispatchers.IO).launch {
                            try { //showDialog()

                                savecheckboxdetails()

                            } catch (e: Exception) {

                                withContext(Dispatchers.Main) {
                                    //   viewPager.visibility = View.VISIBLE
                                    //   cancelAnimDialog()
                                }
                            }
                        }
                        trackSyncContactEvent("Get Started on Sync Contacts")
                        startActivity(Intent(this, SyncContactActivity::class.java))

                    }
                } else {
                    Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_SHORT).show()
                }


                //  }
            }

            //   R.id.btn_skip -> startActivity(Intent(this, SyncContactActivity::class.java))

            txtprivacy.id -> {
                trackSyncContactEvent("Read Privacy Policy for Sync Contacts")

                startActivity (
                    Intent(this, CommonWebViewActivity::class.java)
                        .putExtra(
                            "URL",
                            "https://www.policyboss.com//privacy-policy-policyboss-pro?app_version=" + prefManager.getAppVersion() + "&device_code=" + prefManager.getDeviceID() + "&ssid=" + POSPNO + "&fbaid=" + FBAID
                        )
                        .putExtra("NAME", "" + "privacy-policy")
                        .putExtra("TITLE", "" + "privacy-policy")
                )
            }
            txtterm.id -> {
                trackSyncContactEvent("T&C Viewed for Sync Contacts")

                startActivity(
                    Intent(this, CommonWebViewActivity::class.java)
                        .putExtra(
                            "URL",
                            "https://www.policyboss.com/terms-condition?app_version=" + prefManager.getAppVersion() + "&device_code=" + prefManager.getDeviceID() + "&ssid=" + POSPNO + "&fbaid=" + FBAID
                        )
                        .putExtra("NAME", "" + "Terms & Conditions")
                        .putExtra("TITLE", "" + "Terms & Conditions")


                )
            }

            txtsetting.id -> {
                trackSyncContactEvent("Sync Contacts Setting")

                startActivity(

                    (Intent(this, MyAccountActivity::class.java))

                )
            }
//            tvClickHere.id -> SyncTermPopUp()

            btnchkagree.id -> if (btnchkagree!!.tag != "1") {
                if (btnchkagree!!.isChecked ) {
                    btnNext.isEnabled = true
                    //   btnNext.alpha = 1f

                    //   btnNext.setVisibility(View.VISIBLE);
                    btnNext.tag = 1
                    btnNext.alpha = 1f
                    //  btnNext.text = "NEXT"
                } else {
                    btnNext.isEnabled = false
                    btnNext.alpha = 0.4f
                }
            }

//            btnchktele.id -> if (btnchktele!!.tag != "1") {
//                if (btnchktele!!.isChecked && btnchkcommunication!!.isChecked && btnchkagree!!.isChecked ) {
//                    btnNext.isEnabled = true
//                //    btnNext.alpha = 1f
//                    //btnNext.setVisibility(View.VISIBLE);
//                    btnNext.tag = 1
//                    btnNext.alpha = 1f
//                  //  btnNext.text = "NEXT"
//                } else {
//                    btnNext.isEnabled = false
//                    btnNext.alpha = 0.4f
//                }
//            }
//            btnchkcommunication.id -> if (btnchkcommunication!!.tag != "1") {
//                if (btnchktele!!.isChecked && btnchkcommunication!!.isChecked && btnchkagree!!.isChecked ) {
//                    btnNext.isEnabled = true
//                //    btnNext.alpha = 1f
//                    //btnNext.setVisibility(View.VISIBLE);
//                    btnNext.tag = 1
//                    btnNext.alpha = 1f
//                //    btnNext.text = "NEXT"
//                } else {
//                    btnNext.isEnabled = false
//                    btnNext.alpha = 0.4f
//                }
//            }


        }
    }

    override fun onDestroy() {
        super.onDestroy()


        if (::onPageChangeListener.isInitialized) {
            viewPager.removeOnPageChangeListener(onPageChangeListener)
        }
    }

    inner class MyViewPagerAdapter(private val context: Context) :
        PagerAdapter() {

        private val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = layoutInflater.inflate(layouts[position], container, false)
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return layouts.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            val view = obj as View
            container.removeView(view)
        }
    }

    private fun trackSyncContactEvent(strEvent: String) {
        // Create event attributes
        val eventAttributes: Map<String, Any> = HashMap()
        // Track the login event using WebEngageHelper
        WebEngageAnalytics.getInstance().trackEvent(strEvent, eventAttributes)
    }

}