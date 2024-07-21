package com.stathis.util.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {

    suspend fun observe(): Flow<Status>

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}