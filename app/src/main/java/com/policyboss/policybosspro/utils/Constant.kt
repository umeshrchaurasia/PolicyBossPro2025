package com.policyboss.policybosspro.utils


var TAG = "ATMGO"


object Constant {



    const val base_url = "https://horizon.policyboss.com:5443"
    const val SHARED_PREF = "policybosspro_preference"
    const val token = "1234567890"
    const val TAG = "POLICYBOSS"
    // production url
    //public static String URL = "https://horizon.policyboss.com:5443";
    // Test Environment url
    //   public static String URL = "https://qa.mgfm.in";
    //UAT
    //public static String URL = "https://uat.mgfm.in";


    val ErrorMessage : String = "Error occurred,Please try Again!"
    val Fail : String = "Failed"
    var DEMO_MESSAGE = "demo_message"


    const val APP_DATABASE = "atmmgo_database"
    const val USER_TABLE = "user_table"
    const val allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"

    const val INITIAL_PIN = "register_initial_pin"
    const val LoginData = "atmgo_User_data"

    const val PIN_DATA = "pin_data"

    const val MERCHANT_DATA = "merchant_data"

    const val EmptyResponse = "Empty response body"
    const val StatusMessage =  "Network request failed with status"
    var SuccessMessage = "Success"

    const val today = "Today"
    const val yesterday = "Yesterday"
    const val thisWeek = "This Week"
    const val lastWeek = "Last Week"
    const val thisMonth = "This Month"
    const val lastMonth = "Last Month"
    const val SHARE_WHATSAPP = "sharewhatsapp"



    const val   NOTIFICATION_EXTRA = "NOTIFICATION_EXTRA"
    const val   NOTIFICATION_PROGRESS = "NOTIFICATION_PROGRESS"
    const val   NOTIFICATION_MAX = "NOTIFICATION_MAX"
    const val   NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE"

    const val PUSH_NOTITIFICATION = "demoAndroidApp_notification"
    const val   NOTIFICATION_RECEIVERNAME = "receiverName"

    const val CONTACT_LOG_DataFetching = "Data is Fetching Please Wait..."
    const val CONTACT_LOG_DataSending = "Sending Data to Server..."




    const val   NOData = "No Data Found"
    const val   MasterData = "Master API Failed"
    const val   InValidUser = "User Not Found"
    const val   InValidPass = "Invalid UserId and Password"
    const val   ServerError = "Service Unavailable : No Data Found"
    const val   ServerNotFound = "Service Not Found "
    const val   InValidOTP = "Invalid OTP"

    const val PERMISSION_CAMERA_STORACGE_CONSTANT = 103
    const val REQUEST_PERMISSION_SETTING = 101


}