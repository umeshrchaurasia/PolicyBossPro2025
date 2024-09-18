
package com.policyboss.policybosspro.view.home.adapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.policyboss.policybosspro.BuildConfig
import com.policyboss.policybosspro.MyApplication
import com.policyboss.policybosspro.core.model.homeDashboard.DashboardMultiLangEntity
import com.policyboss.policybosspro.utility.Utility

import org.json.JSONObject
import java.util.HashMap

class SliderDashboardAdapter(
        val mContext: Context,

)
    : RecyclerView.Adapter<SliderDashboardAdapter.ModelViewHolder>(){

    var loanurl = ""
    val mapKey = "map_switchuser"

    lateinit var  viewPager2 : ViewPager2
    lateinit var listInsur: MutableList<DashboardMultiLangEntity>
    var type : Int = 0
    lateinit var mCallback : IDashboardAdapterCallBack

    // var mCallBack : IDashboardAdapterCallBack

    init {
        dbPersistanceController = DBPersistanceController(mContext)
       // this.mCallBack = mCallback
    }

    // called HomeMainActivity : required viewPager2
    // secondary constructor called after init method
    constructor(  mContext: Context,
                    viewPager2 : ViewPager2,
                    listInsur: MutableList<DashboardMultiLangEntity>,
                    type : Int,
                    mCallback : IDashboardAdapterCallBack
                   ) : this(mContext) {


        this.viewPager2 = viewPager2
        this.listInsur = listInsur
        this.type = type
        this.mCallback = mCallback

    }

    // called HomeListProduct :we don't have viewPager2 in it
    constructor(  mContext: Context,
                  listInsur: MutableList<DashboardMultiLangEntity>,
                  type : Int,
                  mCallback : IDashboardAdapterCallBack
    ) : this(mContext) {

        this.listInsur = listInsur
        this.type = type
        this.mCallback = mCallback


    }

    open interface IDashboardAdapterCallBack {

       fun shareProductPopUp(entity : DashboardMultiLangEntity)

       fun infoProductPopUp(entity : DashboardMultiLangEntity)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {



//        val view  = LayoutInflater.from(parent.context).inflate(R.layout.slider_item_model, parent, false)
//
//        return ModelViewHolder(view)

        return if(type == 0){

            val view  = LayoutInflater.from(parent.context).inflate(R.layout.slider_item_model, parent, false)
              ModelViewHolder(view)
        }else{
            val view  = LayoutInflater.from(parent.context).inflate(R.layout.dashboard_prod_item_model, parent, false)
            ModelViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {

        holder.txtProductName.text = listInsur[position].productName
        holder.txtProductDesc.text = listInsur[position].productDetails

        if(type == 0 &&  this::viewPager2.isInitialized){

             if(position == listInsur.size -2){

                viewPager2.post(slideRun)
           }
        }


        if (listInsur[position].icon == -1) {
            Glide.with(mContext)
                    .load(listInsur[position].serverIcon)
                    .into((holder.imgIcon))
        }
        else {
            holder.imgIcon.setImageResource(listInsur[position].icon)
        }

        if(listInsur[position].isExclusive?.isNullOrEmpty() == false){


            // region for Sharing Insurance Prod
            if (listInsur[position].isSharable == "Y") {

                holder.imgShare.visibility =  View.VISIBLE
            } else {
                holder.imgShare.visibility  = View.GONE
            }

            if (!listInsur.get(position).getInfo().isEmpty()) {
                holder.imgInfo.visibility =  View.VISIBLE

            }else{

                holder.imgInfo.visibility =  View.GONE
            }
            //endregion

            //region Font and Background
            if (!listInsur[position].productBackgroundColor.isEmpty()) {
                 holder.cvSlider.setBackgroundColor(Color.parseColor("#" + listInsur.get(position).getProductBackgroundColor()));
            }else{
                holder.cvSlider.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white))
            }

            if (!listInsur[position].productNameFontColor.isEmpty()) {
               holder.txtProductName.setTextColor(Color.parseColor("#" + listInsur[position].productNameFontColor))
            } else {
               holder.txtProductName.setTextColor(ContextCompat.getColor(mContext, R.color.black))
            }

            if (!listInsur[position].productDetailsFontColor.isEmpty()) {
               holder.txtProductDesc.setTextColor(Color.parseColor("#" + listInsur[position].productDetailsFontColor))
            } else {
                holder.txtProductDesc.setTextColor(ContextCompat.getColor(mContext, R.color.black))
            }
            //endregion

        }

        //region  "New" Icon in Product
        // For IsExclusive == Y Showing "New" Icon in Product
        if (listInsur[position].isExclusive == "Y")
         {
           holder.imgNew.visibility = View.VISIBLE
            Glide.with(mContext).load<Any>(R.drawable.newicon)
                    .asGif()
                    .crossFade()
                    .into(holder.imgNew)

        }
        else {
           holder.imgNew.visibility = View.GONE
        }

        //endregion

        //region Click Listener
        holder.cvSlider.setOnClickListener {

            switchDashBoardMenus(listInsur[position])

        }
        holder.cvSlider.setOnLongClickListener {


            //Toast.makeText(mContext, "Long click detected", Toast.LENGTH_SHORT).show()
            ( mContext as HomeMainActivity).stopViewPager()
            true
        }

        holder.imgShare.setOnClickListener {

         //   ( mContext as HomeMainActivity).shareProductPopUp(listInsur[position])

            mCallback.shareProductPopUp(listInsur[position])


        }

        holder.imgInfo.setOnClickListener {

          //  ( mContext as HomeMainActivity).infoProductPopUp(listInsur[position])

            mCallback.infoProductPopUp(listInsur[position])


        }

        //endregion
    }

    override fun getItemCount() = listInsur.size

    val slideRun = object : Runnable{
        override fun run() {

            listInsur.addAll(listInsur)

            notifyDataSetChanged()
        }
        //runnable

    }
    inner  class ModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var cvSlider : MaterialCardView
      var imgIcon : AppCompatImageView
      var txtProductName : AppCompatTextView
      var txtProductDesc : AppCompatTextView

      var imgNew : ImageView
       var imgInfo : ImageView
      var imgShare : ImageView

      init {

          cvSlider = itemView.findViewById(R.id.cvSlider)
          imgIcon = itemView.findViewById(R.id.imgIcon)
          txtProductName = itemView.findViewById(R.id.txtProductName)
          txtProductDesc =  itemView.findViewById(R.id.txtProductDesc)

          imgNew= itemView.findViewById(R.id.imgNew)
          imgInfo = itemView.findViewById(R.id.imgInfo)
          imgShare = itemView.findViewById(R.id.imgShare)

      }


    }


    open fun loadMap(): Map<String, String>? {
        val outputMap: MutableMap<String, String> = HashMap()
        val pSharedPref: SharedPreferences = mContext.getSharedPreferences(Constants.SWITCh_ParentDeatils_FINMART,
                Context.MODE_PRIVATE)
        try {
            if (pSharedPref != null) {
                val jsonString = pSharedPref.getString(mapKey, JSONObject().toString())
                val jsonObject = JSONObject(jsonString)
                val keysItr = jsonObject.keys()
                while (keysItr.hasNext()) {
                    val key = keysItr.next()
                    outputMap[key] = jsonObject[key].toString()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return outputMap
    }



    private fun switchDashBoardMenus(dashboardEntity: DashboardMultiLangEntity) {
        val productID = dashboardEntity.productId

        //Toast.makeText(mContext,"Produvt ID" + productID,Toast.LENGTH_LONG).show();
        //fetching parent ss_id in case of switch user
        val map = loadMap()
        var parent_ssid: String? = ""
        if (map?.size?: 0 > 0) {
            parent_ssid = map!!["Parent_POSPNo"]
        }
        var ipaddress = "0.0.0.0"
        when (productID) {
            1 -> {

                //car
                if (dbPersistanceController.getUserConstantsData().getFourWheelerEnabled().equals("1", ignoreCase = true)) {
                    mContext.startActivity(Intent(mContext, PrivateCarDetailActivity::class.java))
                } else {
                    var motorUrl: String = dbPersistanceController.getUserConstantsData().getFourWheelerUrl()
                    ipaddress = try {
                        Utility.getinfoaddress(mContext)
                    } catch (io: Exception) {
                        "0.0.0.0"
                    }


                    //&ip_address=10.0.3.64&mac_address=10.0.3.64&app_version=2.2.0&product_id=1
                    val append = ("&ip_address=" + ipaddress + "&mac_address=" + ipaddress
                            + "&app_version=policyboss-" + BuildConfig.VERSION_NAME
                            + "&device_id=" + Utility.getDeviceId(mContext)
                            + "&product_id=1&login_ssid=" + parent_ssid)
                    motorUrl = motorUrl + append
                    mContext.startActivity(Intent(mContext, CommonWebViewActivity::class.java)
                            .putExtra("URL", motorUrl)
                            .putExtra("dashBoardtype", "INSURANCE")
                            .putExtra("NAME", "Motor Insurance")
                            .putExtra("TITLE", "Motor Insurance"))
                }

                //mContext.startActivity(new Intent(mContext, PrivateCarDetailActivity.class));
                //  new TrackingController(mContext).sendData(new TrackingRequestEntity(new TrackingData("Motor insurance tab on home page"), Constants.PRIVATE_CAR), null);
                MyApplication.getInstance().trackEvent(Constants.PRIVATE_CAR, "Clicked", "Motor insurance tab on home page")
            }
            23 -> {

                // KOTAK
                var kotakUrl: String = dbPersistanceController.getUserConstantsData().getEliteKotakUrl()
                ipaddress = try {
                    Utility.getinfoaddress(mContext)
                } catch (io: Exception) {
                    "0.0.0.0"
                }


                //&ip_address=10.0.3.64&mac_address=10.0.3.64&app_version=2.2.0&product_id=1
                val appendInKotak = ("&ip_address=" + ipaddress + "&mac_address=" + ipaddress
                        + "&app_version=policyboss-" + BuildConfig.VERSION_NAME
                        + "&device_id=" + Utility.getDeviceId(mContext)
                        + "&product_id=23&login_ssid=" + parent_ssid)
                kotakUrl = kotakUrl + appendInKotak
                mContext.startActivity(Intent(mContext, CommonWebViewActivity::class.java)
                        .putExtra("URL", kotakUrl)
                        .putExtra("dashBoardtype", "INSURANCE")
                        .putExtra("NAME", "Kotak Group health Care")
                        .putExtra("TITLE", "Kotak Group health Care"))
            }
            24 -> {
                //fin peace
                mContext.startActivity(Intent(mContext, CommonWebViewActivity::class.java)
                        .putExtra("URL", "https://10oqcnw.finpeace.ind.in/app#/"
                                + dbPersistanceController.getUserData().getFBAId())
                        .putExtra("NAME", "FIN-PEACE")
                        .putExtra("TITLE", "FIN-PEACE"))
                //  new TrackingController(mContext).sendData(new TrackingRequestEntity(new TrackingData("Fin Peace tab on home page"), Constants.FIN_PEACE), null);
                MyApplication.getInstance().trackEvent(Constants.FIN_PEACE, "Clicked", "Fin Peace tab on home page")
            }
            2 -> {
                //health

                // mContext.startActivity(new Intent(mContext, HealthQuoteAppActivity.class));

                    var healthUrl: String = dbPersistanceController.getUserConstantsData().getHealthurl()
                    //String healthUrl = new DBPersistanceController(mContext).getUserConstantsData().getHealthurltemp();
                    ipaddress = try {
                        Utility.getinfoaddress(mContext)
                    } catch (io: Exception) {
                        "0.0.0.0"
                    }
                    val append = ("&ip_address=" + ipaddress
                            + "&app_version=policyboss-" + Utility.getVersionName(mContext)
                            + "&device_id=" + Utility.getDeviceId(mContext) + "&login_ssid=" + parent_ssid)
                    healthUrl = healthUrl + append

                        mContext.startActivity(Intent(mContext, CommonWebViewActivity::class.java)
                                .putExtra("URL", healthUrl)
                                .putExtra("dashBoardtype", "INSURANCE")
                                .putExtra("NAME", "Health Insurance")
                                .putExtra("TITLE", "Health Insurance"))




                //new TrackingController(mContext).sendData(new TrackingRequestEntity(new TrackingData("Health insurance tab on home page"), Constants.HEALTH_INS), null);
                MyApplication.getInstance().trackEvent(Constants.HEALTH_INS, "Clicked", "Health insurance tab on home page")
            }
            7 -> {
                //home loan
                //  mContext.startActivity(new Intent(mContext, NewHomeApplicaionActivity.class));
                loanurl = "https://www.rupeeboss.com/finmart-home-loan-new?BrokerId=" + dbPersistanceController.getUserData().getLoanId() + "&client_source=finmart"
                mContext.startActivity(Intent(mContext, CommonWebViewActivity::class.java)
                        .putExtra("URL", loanurl)
                        .putExtra("NAME", "HOME LOAN")
                        .putExtra("TITLE", "HOME LOAN"))

                //new TrackingController(mContext).sendData(new TrackingRequestEntity(new TrackingData("Home Loan tab on home page"), Constants.HOME_LOAN), null);
                MyApplication.getInstance().trackEvent(Constants.HOME_LOAN, "Clicked", "Home Loan tab on home page")
            }
            19 -> {
                //personal loan
                // mContext.startActivity(new Intent(mContext, NewPersonalApplicaionActivity.class));
                loanurl = "https://www.rupeeboss.com/finmart-personal-loan-new?BrokerId=" + dbPersistanceController.getUserData().getLoanId() + "&client_source=finmart"
                mContext.startActivity(Intent(mContext, CommonWebViewActivity::class.java)
                        .putExtra("URL", loanurl)
                        .putExtra("NAME", "PERSONAL LOAN")
                        .putExtra("TITLE", "PERSONAL LOAN"))

                //  new TrackingController(mContext).sendData(new TrackingRequestEntity(new TrackingData("Personal loan tab on home page"), Constants.PERSONA_LOAN), null);
                MyApplication.getInstance().trackEvent(Constants.PERSONA_LOAN, "Clicked", "Personal loan tab on home page")
            }
            8 -> {
                //lap
                //   mContext.startActivity(new Intent(mContext, NewLAPApplicaionActivity.class));
                loanurl = "https://www.rupeeboss.com/finmart-property-loan?BrokerId=" + dbPersistanceController.getUserData().getLoanId() + "&client_source=finmart"
                mContext.startActivity(Intent(mContext, CommonWebViewActivity::class.java)
                        .putExtra("URL", loanurl)
                        .putExtra("NAME", "LAP")
                        .putExtra("TITLE", "LAP"))
                //   new TrackingController(mContext).sendData(new TrackingRequestEntity(new TrackingData("LAP tab on home page"), Constants.LAP), null);
                MyApplication.getInstance().trackEvent(Constants.LAP, "Clicked", "LAP tab on home page")
            }
            4 -> {
                //cc
                // mContext.startActivity(new Intent(mContext, CreditCardMainActivity.class));
                //  mContext.startActivity(new Intent(mContext, AppliedCreditListActivity.class));
                loanurl = "https://www.rupeeboss.com/finmart-credit-card-loan-new?BrokerId=" + dbPersistanceController.getUserData().getLoanId() + "&client_source=finmart"
                mContext.startActivity(Intent(mContext, CommonWebViewActivity::class.java)
                        .putExtra("URL", loanurl)
                        .putExtra("NAME", "CREDIT CARD")
                        .putExtra("TITLE", "CREDIT CARD"))
                //  new TrackingController(mContext).sendData(new TrackingRequestEntity(new TrackingData("Credit Card tab on home page"), Constants.CREDIT_CARD), null);
                MyApplication.getInstance().trackEvent(Constants.CREDIT_CARD, "Clicked", "Credit Card tab on home page")
            }
            6 -> {
                //BT
                //  mContext.startActivity(new Intent(mContext, BalanceTransferDetailActivity.class));
                //     mContext.startActivity(new Intent(mContext, NewbusinessApplicaionActivity.class));
                loanurl = "https://www.rupeeboss.com/finmart-business-loan-new?BrokerId=" + dbPersistanceController.getUserData().getLoanId() + "&client_source=finmart"
                mContext.startActivity(Intent(mContext, CommonWebViewActivity::class.java)
                        .putExtra("URL", loanurl)
                        .putExtra("NAME", "BUSINESS LOAN")
                        .putExtra("TITLE", "BUSINESS LOAN"))
                // new TrackingController(mContext).sendData(new TrackingRequestEntity(new TrackingData("Business tab on home page"), Constants.BUSINESS_LOAN), null);
                MyApplication.getInstance().trackEvent(Constants.BUSINESS_LOAN, "Clicked", "Business tab on home page")
            }
            81 -> {
                loanurl = "https://www.rupeeboss.com/car-loan-new?BrokerId=" + dbPersistanceController.getUserData().getLoanId() + "&client_source=finmart"
                mContext.startActivity(Intent(mContext, CommonWebViewActivity::class.java)
                        .putExtra("URL", loanurl)
                        .putExtra("NAME", "CAR LOAN TOP UP")
                        .putExtra("TITLE", "CAR LOAN TOP UP"))
                //  new TrackingController(mContext).sendData(new TrackingRequestEntity(new TrackingData("CAR LOAN TOP UP"), Constants.CAR_TOP_LOAN), null);
                MyApplication.getInstance().trackEvent(Constants.BUSINESS_LOAN, "Clicked", "Business tab on home page")
            }
            9 -> {
                mContext.startActivity(Intent(mContext, QuickLeadActivity::class.java))
                //  new TrackingController(mContext).sendData(new TrackingRequestEntity(new TrackingData("Quick Lead tab on home page"), Constants.QUICK_LEAD), null);
                MyApplication.getInstance().trackEvent(Constants.QUICK_LEAD, "Clicked", "Quick Lead tab on home page")
            }
            10 -> {
                //bike
                if (dbPersistanceController.getUserConstantsData().getTwoWheelerEnabled().equals("1", ignoreCase = true)) {
                    mContext.startActivity(Intent(mContext, TwoWheelerQuoteAppActivity::class.java))
                } else {
                    var motorUrl: String = dbPersistanceController.getUserConstantsData().getTwoWheelerUrl()
                    ipaddress = try {
                        Utility.getinfoaddress(mContext)
                    } catch (io: Exception) {
                        "0.0.0.0"
                    }
                    val append = ("&ip_address=" + ipaddress + "&mac_address=" + ipaddress
                            + "&app_version=policyboss-" + BuildConfig.VERSION_NAME
                            + "&device_id=" + Utility.getDeviceId(mContext)
                            + "&product_id=10&login_ssid=" + parent_ssid)
                    motorUrl = motorUrl + append
                    mContext.startActivity(Intent(mContext, CommonWebViewActivity::class.java)
                            .putExtra("URL", motorUrl)
                            .putExtra("dashBoardtype", "INSURANCE")
                            .putExtra("NAME", "Two Wheeler Insurance")
                            .putExtra("TITLE", "Two Wheeler Insurance"))
                }


                //Toast.makeText(mContext.getContext(), "WIP.", Toast.LENGTH_SHORT).show();
                //mContext.startActivity(new Intent(mContext, TwoWheelerQuoteAppActivity.class));
                // new TrackingController(mContext).sendData(new TrackingRequestEntity(new TrackingData("Two Wheeler tab on home page"), Constants.TWO_WHEELER), null);
                MyApplication.getInstance().trackEvent(Constants.TWO_WHEELER, "Clicked", "Two Wheeler tab on home page")
            }
            11 -> {
                //health check up
                mContext.startActivity(Intent(mContext, HealthCheckUpListActivity::class.java))
                // new TrackingController(mContext).sendData(new TrackingRequestEntity(new TrackingData("Health CheckUp"), Constants.HEALTH_CHECKUP), null);
                MyApplication.getInstance().trackEvent(Constants.HEALTH_CHECKUP, "Clicked", "Health CheckUp tab on home page")
            }
            12 -> {
                //Commercial vehicle
                var cvUrl: String = dbPersistanceController.getUserConstantsData().getCVUrl()
                ipaddress = try {
                    Utility.getinfoaddress(mContext)
                } catch (io: Exception) {
                    "0.0.0.0"
                }
                val append = ("&ip_address=" + ipaddress + "&mac_address="
                        + "&app_version=policyboss-" + BuildConfig.VERSION_NAME
                        + "&device_id=" + Utility.getDeviceId(mContext)
                        + "&product_id=12&login_ssid=" + parent_ssid)
                cvUrl = cvUrl + append
                mContext.startActivity(Intent(mContext, CommonWebViewActivity::class.java)
                        .putExtra("URL", cvUrl)
                        .putExtra("dashBoardtype", "INSURANCE")
                        .putExtra("NAME", "Commercial Vehicle Insurance")
                        .putExtra("TITLE", "Commercial Vehicle Insurance"))
                MyApplication.getInstance().trackEvent(Constants.CV, "Clicked", "Health CheckUp tab on home page")
            }
            13 -> Utility.loadWebViewUrlInBrowser(mContext, "https://www.rupeeboss.com/equifax-finmart?fbaid="
                    + dbPersistanceController.getUserData().getFBAId().toString())
            14 -> {
                Utility.loadWebViewUrlInBrowser(mContext,
                        "https://yesbankbot.buildquickbots.com/chat/rupeeboss/staff/?userid=" + dbPersistanceController.getUserData().getFBAId().toString() + "&usertype=FBA&vkey=b34f02e9-8f1c")
                //car
                mContext.startActivity(Intent(mContext, NCDActivity::class.java))
                // new TrackingController(mContext).sendData(new TrackingRequestEntity(new TrackingData("Campaign "), Constants.CAMPAIGN), null);
                MyApplication.getInstance().trackEvent(Constants.CAMPAIGN, "Clicked", "CAMPAIGN")
            }
            15 -> {
                mContext.startActivity(Intent(mContext, NCDActivity::class.java))
                MyApplication.getInstance().trackEvent(Constants.CAMPAIGN, "Clicked", "CAMPAIGN")
            }
            16 -> {
                mContext.startActivity(Intent(mContext, AddNewOfflineQuotesActivity::class.java))
                // new TrackingController(mContext).sendData(new TrackingRequestEntity(new TrackingData("Offline quote "), Constants.CAMPAIGN), null);
                MyApplication.getInstance().trackEvent(Constants.OFFLINE, "Clicked", "OFFLINE")
            }
            41 -> {
                mContext.startActivity(Intent(mContext, WelcomeSyncContactActivityKotlin::class.java))
                //  new TrackingController(mContext).sendData(new TrackingRequestEntity(new TrackingData("Quick Lead tab on home page"), Constants.QUICK_LEAD), null);
                MyApplication.getInstance().trackEvent(Constants.QUICK_LEAD, "Clicked", "SyncContact")
            }
            49 -> {
                mContext.startActivity(Intent(mContext, UltraLakshaSelectionActivity::class.java))
                //new TrackingController(mContext).sendData(new TrackingRequestEntity(new TrackingData("Ultra lakshya"), Constants.CAMPAIGN), null);
                MyApplication.getInstance().trackEvent(Constants.ULTRA_LAKSHA, "Clicked", "ULTRA_LAKSHYA")
            }
            18 -> {
                //Life Insurance
                mContext.startActivity(Intent(mContext, TermSelectionActivity::class.java))
                // new TrackingController(mContext).sendData(new TrackingRequestEntity(new TrackingData("Life insurance tab on home page"), Constants.LIFE_INS), null);
                MyApplication.getInstance().trackEvent(Constants.LIFE_INS, "Clicked", "Life insurance tab on home page")
            }
            5 -> if (dbPersistanceController.getUserConstantsData().getInvestmentEnabled() == "1") {
                var invUrl: String = dbPersistanceController.getUserConstantsData().getInvestmentUrl()
                ipaddress = try {
                    Utility.getinfoaddress(mContext)
                } catch (io: Exception) {
                    "0.0.0.0"
                }
                var append = ("&ip_address=" + ipaddress
                        + "&app_version=policyboss-" + Utility.getVersionName(mContext)
                        + "&device_id=" + Utility.getDeviceId(mContext) + "&login_ssid=" + parent_ssid)
                invUrl = invUrl + append
                if (dbPersistanceController.getConstantsData().getHealthThrowBrowser() != null &&
                        dbPersistanceController.getConstantsData().getHealthThrowBrowser().equals("1", ignoreCase = true)) {
                    Utility.loadWebViewUrlInBrowser(mContext, invUrl)
                } else {
                    mContext.startActivity(Intent(mContext, CommonWebViewActivity::class.java)
                            .putExtra("URL", invUrl)
                            .putExtra("NAME", "INVESTMENT PLANS")
                            .putExtra("TITLE", "INVESTMENT PLANS"))
                }
            } else {
                Toast.makeText(mContext, "You'r not authorize to sell Investment.", Toast.LENGTH_SHORT).show()
            }
        }
        if (productID < 100 &&  productID != 41) {
            if (dashboardEntity.isNewprdClickable != null) {
                if (dashboardEntity.isNewprdClickable == "Y") {
                    //   region Getting Dynamic Product and Clickable action Using UserConstatnt Data
                    var dynamicUrl = ""
                    for (entity in dbPersistanceController.getUserConstantsData().getDashboardarray()) {
                        if (Integer.valueOf(entity.prodId) == productID) {
                            dynamicUrl = entity.url
                            break
                        }
                    }
                    if (!dynamicUrl.isEmpty()) {
                        ipaddress = try {
                            Utility.getinfoaddress(mContext)
                        } catch (io: Exception) {
                            "0.0.0.0"
                        }


                        //&ip_address=10.0.3.64&mac_address=10.0.3.64&app_version=2.2.0&product_id=1
                        val append = ("&ip_address=" + ipaddress + "&mac_address=" + ipaddress
                                + "&app_version=policyboss-" + BuildConfig.VERSION_NAME
                                + "&device_id=" + Utility.getDeviceId(mContext)
                                + "&product_id=" + productID + "&login_ssid=" + parent_ssid)
                        dynamicUrl = dynamicUrl + append
                        mContext.startActivity(Intent(mContext, CommonWebViewActivity::class.java)
                                .putExtra("URL", dynamicUrl)
                                .putExtra("dashBoardtype", "INSURANCE")
                                .putExtra("NAME", dashboardEntity.productName)
                                .putExtra("TITLE", dashboardEntity.productName))
                    }

                    //endregion
                }
            }
        } else if (productID >= 100) {
            mContext.startActivity(Intent(mContext, CommonWebViewActivity::class.java)
                    .putExtra("URL", "" + dashboardEntity.link)
                    .putExtra("NAME", "" + dashboardEntity.productName)
                    .putExtra("TITLE", "" + dashboardEntity.productName))
        }
    }



}