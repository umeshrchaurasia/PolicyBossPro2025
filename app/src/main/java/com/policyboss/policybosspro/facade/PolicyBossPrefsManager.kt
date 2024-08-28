package com.policyboss.policybosspro.facade

import android.content.Context
import android.content.SharedPreferences
import com.policyboss.policybosspro.utils.Constant
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PolicyBossPrefsManager @Inject constructor(@ApplicationContext context: Context) {

    private var pref = context.getSharedPreferences(Constant.SHARED_PREF, Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = pref.edit()

    private fun getEditor(): SharedPreferences.Editor {
        return pref.edit()
    }

    companion object {
        const val PREF_NAME = "magic-finmart"
        private const val MOTOR_VERSION = "motor_master_version"
        private const val POPUP_COUNTER = "popup_counter_value"
        private const val POPUP_ID = "popup_id"
        private const val IS_LANGUAGE = "user_language"
        private const val IS_USER_PASSWORD = "user_password"
        private const val NOTIFICATION_TYPE_ENABLE = "NotificationType_Enable"
        private const val MSG_FIRST_CHECK = "msgfirst_check"
        private const val CONTACT_FIRST_CHECK = "Contactfirst_check"
        private const val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
        private const val IS_BIKE_MASTER_UPDATE = "isBikeMasterUpdate"
        private const val IS_CAR_MASTER_UPDATE = "isCarMasterUpdate"
        private const val IS_RTO_MASTER_UPDATE = "isRtoMasterUpdate"
        private const val IS_INSURANCE_MASTER_UPDATE = "isRtoMasterUpdate"
        private const val IS_DEVICE_TOKEN = "devicetoken"
        private const val IS_RBL_CITY_MASTER = "isRblCityMaster"
        private const val IS_EMPLOYER_NAME_MASTER = "employernamemaster"
        private const val IS_ZOHO_MASTER = "iszohomaster"
        private const val POSP_INFO = "pospinfo"
        private const val IS_UPDATE_SHOWN = "updateshown"
        private const val CAR_VEHICLE_NUMBER_LOG = "vehicle_number_log"
        private const val CAR_VEHICLE_MOBILE_LOG = "vehicle_mobile_log"
        private const val FOS_USER_AUTHENTICATIONN = "fos_user_authenticationn"
        const val PUSH_VERIFY_LOGIN = "push_verify_login"
        const val NOTIFICATION_COUNTER = "Notification_Counter"
        const val IS_ENABLE_PRO_POSPURL = "IS_enable_pro_POSPurl"
        const val IS_ENABLE_PRO_ADDSUBUSER_URL = "IS_enable_pro_Addsubuser_url"
        const val SHARED_KEY_PUSH_NOTIFY = "shared_notifyFlag"
        const val SHARED_KEY_PUSH_WEB_URL = "shared_notify_webUrl"
        const val SHARED_KEY_PUSH_WEB_TITLE = "shared_notify_webTitle"
        const val SHARED_KEY_PUSH_BODY = "shared_notify_Title"
        const val SHARED_KEY_PUSH_TITLE = "shared_notify_Body"
        const val PUSH_NOTIFICATION = "push_notifyication_data"
        private const val DEEP_LINK = "DeepLink"
        const val MPS_DATA = "mps_data"
        private const val MENU_DASHBOARD = "menu_dashboard"
        private const val CONTACT_COUNT = "contact_count"
        private const val DEVICE_ID = "policybossproDeviceID"
        private const val APP_VERSION = "policybossproAppVersion"
    }



    fun clearData(){

        getEditor().clear().apply()
    }

    fun setLanguage(language: String) {
        editor.putString(IS_LANGUAGE, language).apply()
    }

    fun getLanguage(): String? {
        return pref.getString(IS_LANGUAGE, "")
    }

    fun setUserPassword(pwd: String) {
        editor.putString(IS_USER_PASSWORD, pwd).apply()
    }

    fun getUserPassword(): String? {
        return pref.getString(IS_USER_PASSWORD, "")
    }

    // Vehicle Detail Methods
    fun setVehicleCarVehicleLog(): Boolean {
        editor.putInt(CAR_VEHICLE_NUMBER_LOG, pref.getInt(CAR_VEHICLE_NUMBER_LOG, 0) + 1)
        return editor.commit()
    }

    fun getVehicleCarVehicleLog(): Int {
        return pref.getInt(CAR_VEHICLE_NUMBER_LOG, 0)
    }

    fun setVehicleCarMobileLog(): Boolean {
        editor.putInt(CAR_VEHICLE_MOBILE_LOG, pref.getInt(CAR_VEHICLE_MOBILE_LOG, 0) + 1)
        return editor.commit()
    }

    fun getVehicleCarMobileLog(): Int {
        return pref.getInt(CAR_VEHICLE_MOBILE_LOG, 0)
    }

    // MPS Data Methods

    // Update Status Methods
    fun setIsUpdateShown(isFirstTime: Boolean) {
        editor.putBoolean(IS_UPDATE_SHOWN, isFirstTime).apply()
    }

    fun getUpdateShown(): Boolean {
        return pref.getBoolean(IS_UPDATE_SHOWN, true)
    }

    fun setIsZohoMaster(isFirstTime: Boolean) {
        editor.putBoolean(IS_ZOHO_MASTER, isFirstTime).apply()
    }

    fun getIsZohoMaster(): Boolean {
        return pref.getBoolean(IS_ZOHO_MASTER, true)
    }

    fun setIsRblCityMaster(isFirstTime: Boolean) {
        editor.putBoolean(IS_RBL_CITY_MASTER, isFirstTime).apply()
    }

    fun getIsRblCityMaster(): Boolean {
        return pref.getBoolean(IS_RBL_CITY_MASTER, true)
    }

    fun setFirstTimeLaunch(isFirstTime: Boolean) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime).apply()
    }

    fun isFirstTimeLaunch(): Boolean {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
    }

    fun setIsCarMasterUpdate(isFirstTime: Boolean) {
        editor.putBoolean(IS_CAR_MASTER_UPDATE, isFirstTime).apply()
    }

    fun isCarMasterUpdate(): Boolean {
        return pref.getBoolean(IS_CAR_MASTER_UPDATE, true)
    }

    fun setIsBikeMasterUpdate(isFirstTime: Boolean) {
        editor.putBoolean(IS_BIKE_MASTER_UPDATE, isFirstTime).apply()
    }

    fun isBikeMasterUpdate(): Boolean {
        return pref.getBoolean(IS_BIKE_MASTER_UPDATE, true)
    }

    fun setIsEmployerNameUpdate(isFirstTime: Boolean) {
        editor.putBoolean(IS_EMPLOYER_NAME_MASTER, isFirstTime).apply()
    }

    fun isEmployerNameUpdate(): Boolean {
        return pref.getBoolean(IS_EMPLOYER_NAME_MASTER, true)
    }

    fun setIsRtoMasterUpdate(isFirstTime: Boolean) {
        editor.putBoolean(IS_RTO_MASTER_UPDATE, isFirstTime).apply()
    }

    fun isRtoMasterUpdate(): Boolean {
        return pref.getBoolean(IS_RTO_MASTER_UPDATE, true)
    }

    fun setIsInsuranceMasterUpdate(isFirstTime: Boolean) {
        editor.putBoolean(IS_INSURANCE_MASTER_UPDATE, isFirstTime).apply()
    }

    fun isInsuranceMasterUpdate(): Boolean {
        return pref.getBoolean(IS_INSURANCE_MASTER_UPDATE, true)
    }

    fun setFosUser(strData: String) {
        editor.putString(FOS_USER_AUTHENTICATIONN, strData).apply()
    }

    fun getFosUser(): String? {
        return pref.getString(FOS_USER_AUTHENTICATIONN, "")
    }

    fun setToken(token: String) {
        editor.putString(IS_DEVICE_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return pref.getString(IS_DEVICE_TOKEN, "")
    }

    fun getNotificationCounter(): Int {
        return pref.getInt(NOTIFICATION_COUNTER, 0)
    }

    fun setNotificationCounter(counter: Int) {
        editor.putInt(NOTIFICATION_COUNTER, counter).apply()
    }

    fun setIsUserLogin(isUserLogin: Boolean) {
        editor.putBoolean(PUSH_VERIFY_LOGIN, isUserLogin).apply()
    }

    fun isUserLogin(): Boolean {
        return pref.getBoolean(PUSH_VERIFY_LOGIN, false)
    }

    fun setEnableProPOSPurl(proSignupUrl: String) {
        editor.putString(IS_ENABLE_PRO_POSPURL, proSignupUrl).apply()
    }

    fun getEnableProPOSPurl(): String? {
        return pref.getString(IS_ENABLE_PRO_POSPURL, "")
    }

    fun setEnableProAddSubUserUrl(proSignupUrl: String) {
        editor.putString(IS_ENABLE_PRO_ADDSUBUSER_URL, proSignupUrl).apply()
    }

    fun getEnableProAddSubUserUrl(): String? {
        return pref.getString(IS_ENABLE_PRO_ADDSUBUSER_URL, "")
    }

    // Notification Methods
    fun setSharePushType(type: String) {
        editor.putString(SHARED_KEY_PUSH_NOTIFY, type).apply()
    }

    fun getSharePushType(): String? {
        return pref.getString(SHARED_KEY_PUSH_NOTIFY, "")
    }

}