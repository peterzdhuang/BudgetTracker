package com.ag_apps.spending_tracker.spending_overview.presentation

import java.time.ZonedDateTime

/**
 * @author Ahmed Guedmioui
 */
sealed interface SpendingOverviewAction {
    data object LoadSpendingOverviewAndBalance: SpendingOverviewAction
    data class OnDateChange(val newDate: ZonedDateTime): SpendingOverviewAction
    data class OnDeleteSpending(val spendingId: Int): SpendingOverviewAction
    data class OnMonthChange(val newMonth : Int) : SpendingOverviewAction
}