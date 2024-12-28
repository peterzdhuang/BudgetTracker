package com.ag_apps.spending_tracker.core.domain

interface CoreRepository {
    suspend fun updateBalance(balance: Double)
    suspend fun getBalance(): Double
}