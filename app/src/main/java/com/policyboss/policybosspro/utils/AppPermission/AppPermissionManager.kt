package com.policyboss.policybosspro.utils.AppPermission

import android.Manifest
import android.os.Build

object AppPermissionManager {

    // Permission sets based on Android version

    //Mark : Camera and Storage Permission
    val cameraAndStorage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    //Mark : Storage Permission
    val storage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    //Mark : Contact and Call Log Permission
    val contactsAndCallLog = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG)



    // Mark: Post Notifications Permission
    val postNotifications = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        emptyArray() // No action for versions below TIRAMISU
    }

    // Permission groups for different features
    enum class PermissionType {
        CAMERA_AND_STORAGE,
        STORAGE,
        CONTACTS_AND_CALL_LOG,
        POST_NOTIFICATIONS
    }

    fun getPermissions(type: PermissionType): Array<String> {
        return when (type) {
            PermissionType.CAMERA_AND_STORAGE -> cameraAndStorage
            PermissionType.STORAGE -> storage
            PermissionType.CONTACTS_AND_CALL_LOG -> contactsAndCallLog
            PermissionType.POST_NOTIFICATIONS -> postNotifications
        }
    }
}