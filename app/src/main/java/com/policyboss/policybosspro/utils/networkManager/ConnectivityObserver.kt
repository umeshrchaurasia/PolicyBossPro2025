package com.policyboss.policybosspro.utils.networkManager

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {

    enum class Status {
        Available,
        Losing,
        Lost,
        Unavailable
    }

    fun observe(): Flow<Status>
}