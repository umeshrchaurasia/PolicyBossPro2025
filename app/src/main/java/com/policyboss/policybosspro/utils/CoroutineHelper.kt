package com.policyboss.policybosspro.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.policyboss.policybosspro.core.RetroHelper
import com.policyboss.policybosspro.utility.Utility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CoroutineHelper {



    companion object{

        @JvmStatic
        fun saveDeviceDetails(context: Context,   ss_id : String, action_type : String)  {


            CoroutineScope(Dispatchers.IO).launch {
                try { //showDialog()

                    withContext(Dispatchers.IO) {

                       // var url = BuildConfig.FINMART_URL + "/app_visitor/save_device_details"

                        Log.d(Constant.TAG, "DeviceDetail"+ Gson().toJson(Utility.getDeviceDetail(context)))
                        val body = HashMap<String,String>()
                        body.put("ss_id",ss_id)
                        body.put("device_id", Utility.getDeviceID(context))

                        body.put("device_name",Utility.getDeviceName())
                        body.put("os_detail",Utility.getOS())
                        body.put("device_info", Gson().toJson(Utility.getDeviceDetail(context)))
                        body.put("action_type",action_type)
                        body.put("App_Version",
                            "PolicyBossPro-" + com.policyboss.policybosspro.BuildConfig.VERSION_NAME)

                        val resultRespAsync = async { RetroHelper.api.saveDeviceDetails(body) }
                        val resultResp = resultRespAsync.await()
                        if (resultResp.isSuccessful) {
                            // cancelDialog()


                            // region No NEED
//                            if(resultResp.body()?.status?.uppercase().equals("SUCCESS")){
//
//                                //var response = resultResp.body()
//
//
//                                Log.d(Constant.TAG,"save_device_details:"+ "SUCCESS" )
//
//                            }else{
//
//                                Log.d(Constant.TAG,"save_device_details: Failure")
//                            }
                            //endregion


                        }else{

                            Log.d(Constant.TAG,"save_device_details:"+ resultResp.errorBody())
                            // cancelDialog()
                        }


                    }

                }catch (e: Exception){

                    Log.d(Constant.TAG,"save_device_details: Failure")

                }
            }

        }

        @JvmStatic
        fun getSynHorizonDetails(context: Context,   ss_id : String) : List<String>{

            var myData = mutableListOf("CDDC","eedced","deded","dedede")

            return myData
        }


    }

}