package com.ag_apps.spending_tracker.balance.presentation

sealed interface BalanceEvent {
    data object NavigateBack : BalanceEvent
}