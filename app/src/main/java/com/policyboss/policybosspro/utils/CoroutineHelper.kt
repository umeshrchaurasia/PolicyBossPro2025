package com.policyboss.policybosspro.utils

import android.content.Context


class CoroutineHelper {



    companion object{

//        @JvmStatic
//        fun saveDeviceDetails(context: Context,   ss_id : String, action_type : String)  {
//
//
//            CoroutineScope(Dispatchers.IO).launch {
//                try { //showDialog()
//
//                    withContext(Dispatchers.IO) {
//
//                       // var url = BuildConfig.FINMART_URL + "/app_visitor/save_device_details"
//
//                        Log.d(Constant.TAG, "DeviceDetail"+ Gson().toJson(UTILITY.getDeviceDetail(context)))
//                        val body = HashMap<String,String>()
//                        body.put("ss_id",ss_id)
//                        body.put("device_id", UTILITY.getDeviceID(context))
//
//                        body.put("device_name",UTILITY.getDeviceName())
//                        body.put("os_detail",UTILITY.getOS())
//                        body.put("device_info", Gson().toJson(UTILITY.getDeviceDetail(context)))
//                        body.put("action_type",action_type)
//                        body.put("App_Version",
//                            "PolicyBossPro-" + com.policyboss.policybosspro.BuildConfig.VERSION_NAME)
//
//                        val resultRespAsync = async { RetroHelper.api.saveDeviceDetails(body) }
//                        val resultResp = resultRespAsync.await()
//                        if (resultResp.isSuccessful) {
//                            // cancelDialog()
//
//
//                            // region No NEED
////                            if(resultResp.body()?.status?.uppercase().equals("SUCCESS")){
////
////                                //var response = resultResp.body()
////
////
////                                Log.d(Constant.TAG,"save_device_details:"+ "SUCCESS" )
////
////                            }else{
////
////                                Log.d(Constant.TAG,"save_device_details: Failure")
////                            }
//                            //endregion
//
//
//                        }else{
//
//                            Log.d(Constant.TAG,"save_device_details:"+ resultResp.errorBody())
//                            // cancelDialog()
//                        }
//
//
//                    }
//
//                }catch (e: Exception){
//
//                    Log.d(Constant.TAG,"save_device_details: Failure")
//
//                }
//            }
//
//        }

        @JvmStatic
        fun getSynHorizonDetails(context: Context,   ss_id : String) : List<String>{

            var myData = mutableListOf("CDDC","eedced","deded","dedede")

            return myData
        }
/*
        @JvmStatic
        fun getsyncDetailshorizon(context: Context,   ss_id : String, action_type : String)  : String  {

            var isContactSync = "0"
            val TAG = "HORIZONEMP"


            CoroutineScope(Dispatchers.IO).launch {
                try { //showDialog()

                    withContext(Dispatchers.IO){

                        // var url =  "https://horizon.policyboss.com:5443/sync_contacts" + "/contact_entry"
                        var url = "https://horizon.policyboss.com:5443/posps/dsas/view/" + ss_id
                        val resultRespAsync = async { RetroHelper.api.getHorizonDetails(url) }
                        val resultResp = resultRespAsync.await()
                        if (resultResp.isSuccessful) {
                            // cancelDialog()
                            Log.d(TAG, resultResp.toString())
                            if(resultResp.body()?.status.equals("SUCCESS")){

                                var HorizonEmpDetailResponse = resultResp.body()
                                isContactSync = HorizonEmpDetailResponse?.POSP?.Is_Contact_Sync.toString()

                                withContext(Dispatchers.Main){

                                    if (isContactSync.equals("1")) {

                                       // viewPager!!.currentItem = 2
                                       // viewPager.beginFakeDrag()
                                       // viewPager.visibility = View.VISIBLE
                                      //  cancelAnimDialog()
                                    }else{
                                     //   viewPager.visibility = View.VISIBLE
                                     //   cancelAnimDialog()
                                    }

                                    return@withContext isContactSync

                                }
                            }else{
                                withContext(Dispatchers.Main) {
                                    Log.d(TAG, resultResp.toString())
                                   // viewPager.visibility = View.VISIBLE
                                   // cancelAnimDialog()
                                }
                            }


                            // delay(8000)



                        }else{

                            withContext(Dispatchers.Main) {
                                Log.d(TAG, resultResp.toString())
                               // viewPager.visibility = View.VISIBLE
                               // cancelAnimDialog()
                            }
                            // cancelDialog()
                        }

                    }

                }catch (e: Exception){

                    Log.d(Constant.TAG,"save_device_details: Failure")

                }
            }

        }*/

    }

}