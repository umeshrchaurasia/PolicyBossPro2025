package com.policyboss.policybosspro.core.repository.notificationRepository

import android.provider.Settings.Global
import android.util.Log
import com.policyboss.policybosspro.core.model.notification.NotifyEntity

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor() : INotificationRepository {

    private val notificationFlow = MutableSharedFlow<NotifyEntity>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun sendNotification(notifyEntity: NotifyEntity) {
        ioScope.launch {
            try {
                notificationFlow.emit(notifyEntity)
            } catch (e: Exception) {
                Log.e("NotificationRepository", "Error emitting notification", e)
            }
        }
    }

    override fun observeNotifications(): Flow<NotifyEntity> =
        notificationFlow.asSharedFlow()

    override fun close() {
        ioScope.cancel()
    }
}



// 1. Repository Interface
interface INotificationRepository {
    fun sendNotification(notifyEntity: NotifyEntity)
    fun observeNotifications(): Flow<NotifyEntity>
    fun close()
}