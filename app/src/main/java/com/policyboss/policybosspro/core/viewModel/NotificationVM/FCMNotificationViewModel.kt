package com.policyboss.policybosspro.core.viewModel.NotificationVM

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.policyboss.policybosspro.core.model.notification.NotifyEntity
import com.policyboss.policybosspro.core.repository.notificationRepository.INotificationRepository
import com.policyboss.policybosspro.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FCMNotificationViewModel @Inject constructor(

    private val notificationRepository: INotificationRepository

) : ViewModel() {

    private val _notificationState = MutableStateFlow<NotifyEntity?>(null)
    val notificationFlow = _notificationState.asSharedFlow()



    init {
        viewModelScope.launch {
            notificationRepository.observeNotifications()
                .catch { e ->
                    Log.d(Constant.TAG, "Error collecting notifications", e)
                }
                .collect { notification ->
                    _notificationState.value = notification
                }
        }
    }





    override fun onCleared() {
        super.onCleared()

        notificationRepository.close() // Cancel the scope to avoid memory leaks

    }

}