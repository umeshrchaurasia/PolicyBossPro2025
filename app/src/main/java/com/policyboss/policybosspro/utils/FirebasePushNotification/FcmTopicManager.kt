package com.policyboss.policybosspro.utils.FirebasePushNotification


import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import com.policyboss.policybosspro.utils.Constant
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume


@Singleton
class FcmTopicManager @Inject constructor(
    private val prefManager: PolicyBossPrefsManager
) {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            Log.e("FCM", "Uncaught error in FcmTopicManager scope", e)
        }
    )

    private suspend fun Task<Void>.awaitResult(): Boolean =
        suspendCancellableCoroutine { cont ->
            addOnCompleteListener { task ->
                if (cont.isActive) cont.resume(task.isSuccessful)
            }
        }

    // Always-on topic, subscribed once regardless of login state
    suspend fun subscribeToAllUsers() {
        if (prefManager.isSubscribedToAllUsers()) return
        val ok = FirebaseMessaging.getInstance()
            .subscribeToTopic(Constant.ALL_USER)
            .awaitResult()
        if (ok) prefManager.setSubscribedToAllUsers(true)
        Log.d("FCM", "subscribeToAllUsers: $ok")
    }

    // Only used for guests — logged-in users get NOTHING extra,
    // they're targeted by device token / user-specific means instead.
    suspend fun subscribeToGuest() {
        if (prefManager.isGuestTopicSubscribed()) return
        val ok = FirebaseMessaging.getInstance()
            .subscribeToTopic(Constant.GUEST_USERS)
            .awaitResult()
        if (ok) prefManager.setGuestTopicSubscribed(true)
        Log.d("FCM", "subscribeToGuest: $ok")
    }

    // Called once, on login: just leave guest topic
    suspend fun unsubscribeGuestOnLogin() {
        if (!prefManager.isGuestTopicSubscribed()) return
        val ok = FirebaseMessaging.getInstance()
            .unsubscribeFromTopic(Constant.GUEST_USERS)
            .awaitResult()
        if (ok) prefManager.setGuestTopicSubscribed(false)
        Log.d("FCM", "unsubscribeGuestOnLogin: $ok")
    }

    // Called on logout: go back to guest
    suspend fun resubscribeGuestOnLogout() = subscribeToGuest()

    // ---- Async fire-and-forget wrappers ----
    fun subscribeToAllUsersAsync() = scope.launch { subscribeToAllUsers() }
    fun subscribeToGuestAsync() = scope.launch { subscribeToGuest() }
    fun unsubscribeGuestOnLoginAsync() = scope.launch { unsubscribeGuestOnLogin() }
}

//@Singleton
//class FcmTopicManager @Inject constructor(
//    private val prefManager: PolicyBossPrefsManager
//) {
//
//    // Own scope — survives Activity destruction, so fire-and-forget calls
//    // actually complete even if the caller Activity finishes right after.
//    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
//
//    private suspend fun Task<Void>.awaitResult(): Boolean =
//        suspendCancellableCoroutine { cont ->
//            addOnCompleteListener { task ->
//                if (cont.isActive) cont.resume(task.isSuccessful)
//            }
//        }
//
//    // =========================================================
//    // SUSPEND versions — use these when the caller genuinely
//    // needs to await completion (e.g. logout, before clearAll()).
//    // =========================================================
//
//    suspend fun subscribeToAllUsers() {
//        if (prefManager.isSubscribedToAllUsers()) {
//            Log.d("FCM", "Already subscribed to ${Constant.ALL_USER}")
//            return
//        }
//        val ok = FirebaseMessaging.getInstance()
//            .subscribeToTopic(Constant.ALL_USER)
//            .awaitResult()
//
//        if (ok) {
//            prefManager.setSubscribedToAllUsers(true)
//            Log.d("FCM", "Subscribed to ${Constant.ALL_USER}")
//        } else {
//            Log.e("FCM", "Failed to subscribe to ${Constant.ALL_USER}")
//        }
//    }
//
//    suspend fun switchToLoggedIn() {
//        val fcm = FirebaseMessaging.getInstance()
//
//        if (prefManager.isGuestTopicSubscribed()) {
//            val unsubOk = fcm.unsubscribeFromTopic(Constant.GUEST_USERS).awaitResult()
//            if (unsubOk) {
//                prefManager.setGuestTopicSubscribed(false)
//                Log.d("FCM", "Unsubscribed from guest_users")
//            } else {
//                Log.e("FCM", "Failed to unsubscribe from guest_users")
//            }
//        }
//
//        if (!prefManager.isLoggedInTopicSubscribed()) {
//            val subOk = fcm.subscribeToTopic(Constant.LOGGED_IN_USERS).awaitResult()
//            if (subOk) {
//                prefManager.setLoggedInTopicSubscribed(true)
//                Log.d("FCM", "Subscribed to logged_in_users")
//            } else {
//                Log.e("FCM", "Failed to subscribe to logged_in_users")
//            }
//        } else {
//            Log.d("FCM", "Already subscribed to logged_in_users")
//        }
//    }
//
//    suspend fun switchToGuest() {
//        val fcm = FirebaseMessaging.getInstance()
//
//        if (prefManager.isLoggedInTopicSubscribed()) {
//            val unsubOk = fcm.unsubscribeFromTopic(Constant.LOGGED_IN_USERS).awaitResult()
//            if (unsubOk) {
//                prefManager.setLoggedInTopicSubscribed(false)
//                Log.d("FCM", "Unsubscribed from logged_in_users")
//            } else {
//                Log.e("FCM", "Failed to unsubscribe from logged_in_users")
//            }
//        }
//
//        if (!prefManager.isGuestTopicSubscribed()) {
//            val subOk = fcm.subscribeToTopic(Constant.GUEST_USERS).awaitResult()
//            if (subOk) {
//                prefManager.setGuestTopicSubscribed(true)
//                Log.d("FCM", "Subscribed to guest_users")
//            } else {
//                Log.e("FCM", "Failed to subscribe to guest_users")
//            }
//        } else {
//            Log.d("FCM", "Already subscribed to guest_users")
//        }
//    }
//
//    suspend fun reconcileTopicForUser(isLoggedIn: Boolean) {
//        if (isLoggedIn) switchToLoggedIn() else switchToGuest()
//    }
//
//    // =========================================================
//    // ASYNC (fire-and-forget) versions — use these when the
//    // caller is about to navigate/finish() and shouldn't wait
//    // (e.g. splash screen, login success).
//    // Runs on this manager's own scope, so it survives the
//    // calling Activity being destroyed.
//    // =========================================================
//
//    fun subscribeToAllUsersAsync() {
//        scope.launch { subscribeToAllUsers() }
//    }
//
//    fun switchToLoggedInAsync() {
//        scope.launch { switchToLoggedIn() }
//    }
//
//    fun switchToGuestAsync() {
//        scope.launch { switchToGuest() }
//    }
//
//    fun reconcileTopicForUserAsync(isLoggedIn: Boolean) {
//        scope.launch { reconcileTopicForUser(isLoggedIn) }
//    }
//}

//@Singleton
//class FcmTopicManager @Inject constructor(
//    private val prefManager: PolicyBossPrefsManager
//) {
//
//    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
//
//
//    fun subscribeToAllUsersAsync() {
//        scope.launch { subscribeToAllUsers() }
//    }
//    // Wrap a Firebase Task in a suspend fun so callers can await completion
//    private suspend fun Task<Void>.awaitResult(): Boolean =
//        suspendCancellableCoroutine { cont ->
//            addOnCompleteListener { task ->
//                if (cont.isActive) cont.resume(task.isSuccessful)
//            }
//        }
//
//    suspend fun subscribeToAllUsers() {
//        if (prefManager.isSubscribedToAllUsers()) {
//            Log.d("FCM", "Already subscribed to ${Constant.ALL_USER}")
//            return
//        }
//        val ok = FirebaseMessaging.getInstance()
//            .subscribeToTopic(Constant.ALL_USER)
//            .awaitResult()
//
//        if (ok) {
//            prefManager.setSubscribedToAllUsers(true)
//            Log.d("FCM", "Subscribed to ${Constant.ALL_USER}")
//        } else {
//            Log.e("FCM", "Failed to subscribe to ${Constant.ALL_USER}")
//        }
//    }
//
//    /**
//     * Switches the device to logged_in_users, unsubscribing from guest_users.
//     * Each step updates its own flag independently so a partial failure
//     * doesn't leave stale/incorrect local state.
//     */
//    suspend fun switchToLoggedIn() {
//        val fcm = FirebaseMessaging.getInstance()
//
//        if (prefManager.isGuestTopicSubscribed()) {
//            val unsubOk = fcm.unsubscribeFromTopic(Constant.GUEST_USERS).awaitResult()
//            if (unsubOk) {
//                prefManager.setGuestTopicSubscribed(false)
//                Log.d("FCM", "Unsubscribed from guest_users")
//            } else {
//                Log.e("FCM", "Failed to unsubscribe from guest_users")
//            }
//        }
//
//        if (!prefManager.isLoggedInTopicSubscribed()) {
//            val subOk = fcm.subscribeToTopic(Constant.LOGGED_IN_USERS).awaitResult()
//            if (subOk) {
//                prefManager.setLoggedInTopicSubscribed(true)
//                Log.d("FCM", "Subscribed to logged_in_users")
//            } else {
//                Log.e("FCM", "Failed to subscribe to logged_in_users")
//            }
//        } else {
//            Log.d("FCM", "Already subscribed to logged_in_users")
//        }
//    }
//
//    suspend fun switchToGuest() {
//        val fcm = FirebaseMessaging.getInstance()
//
//        if (prefManager.isLoggedInTopicSubscribed()) {
//            val unsubOk = fcm.unsubscribeFromTopic(Constant.LOGGED_IN_USERS).awaitResult()
//            if (unsubOk) {
//                prefManager.setLoggedInTopicSubscribed(false)
//                Log.d("FCM", "Unsubscribed from logged_in_users")
//            } else {
//                Log.e("FCM", "Failed to unsubscribe from logged_in_users")
//            }
//        }
//
//        if (!prefManager.isGuestTopicSubscribed()) {
//            val subOk = fcm.subscribeToTopic(Constant.GUEST_USERS).awaitResult()
//            if (subOk) {
//                prefManager.setGuestTopicSubscribed(true)
//                Log.d("FCM", "Subscribed to guest_users")
//            } else {
//                Log.e("FCM", "Failed to subscribe to guest_users")
//            }
//        } else {
//            Log.d("FCM", "Already subscribed to guest_users")
//        }
//    }
//
//    suspend fun reconcileTopicForUser(isLoggedIn: Boolean) {
//        if (isLoggedIn) switchToLoggedIn() else switchToGuest()
//    }
//}

//@Singleton
//class FcmTopicManager @Inject constructor(
//    private val prefManager: PolicyBossPrefsManager
//) {
//
//    fun subscribeToAllUsers() {
//        if (prefManager.isSubscribedToAllUsers()) return
//
//        FirebaseMessaging.getInstance()
//            .subscribeToTopic(Constant.ALL_USER)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    prefManager.setSubscribedToAllUsers(true)
//                    Log.d("FCM", "Subscribed to ${Constant.ALL_USER}")
//                } else {
//                    Log.e("FCM", "Failed to subscribe to ${Constant.ALL_USER}", task.exception)
//                }
//            }
//    }
//
//    fun switchToLoggedIn() {
//        if (prefManager.isLoggedInTopicSubscribed()) {
//            Log.d("FCM", "Already subscribed to logged_in_users")
//            return
//        }
//
//        val fcm = FirebaseMessaging.getInstance()
//
//        // Unsubscribe from guest_users topic first
//        fcm.unsubscribeFromTopic(Constant.GUEST_USERS)
//            .addOnCompleteListener {
//                // Proceed to subscribe to logged_in_users regardless of unsubscribe success
//                fcm.subscribeToTopic(Constant.LOGGED_IN_USERS)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            prefManager.setLoggedInTopicSubscribed(true)
//                            prefManager.setGuestTopicSubscribed(false)
//                            Log.d("FCM", "Subscribed to logged_in_users")
//                        } else {
//                            Log.e("FCM", "Failed to subscribe to logged_in_users", task.exception)
//                        }
//                    }
//            }
//    }
//
//    fun switchToGuest() {
//        if (prefManager.isGuestTopicSubscribed()) {
//            Log.d("FCM", "Already subscribed to guest_users")
//            return
//        }
//
//        val fcm = FirebaseMessaging.getInstance()
//
//        // 1. Unsubscribe from logged_in_users topic
//        fcm.unsubscribeFromTopic(Constant.LOGGED_IN_USERS)
//            .addOnCompleteListener {
//                // 2. Subscribe to guest_users topic
//                fcm.subscribeToTopic(Constant.GUEST_USERS)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            prefManager.setGuestTopicSubscribed(true)
//                            prefManager.setLoggedInTopicSubscribed(false)
//                            Log.d("FCM", "Subscribed to guest_users")
//                        } else {
//                            Log.e("FCM", "Failed to subscribe to guest_users", task.exception)
//                        }
//                    }
//            }
//    }
//}