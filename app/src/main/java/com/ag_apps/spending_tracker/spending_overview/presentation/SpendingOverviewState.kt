package com.ag_apps.spending_tracker.spending_overview.presentation

import com.ag_apps.spending_tracker.core.domain.Spending
import java.time.Month
import java.time.ZonedDateTime

data class SpendingOverviewState(
    val spendingList: List<Spending> = emptyList(),
    val dateList: List<ZonedDateTime> = emptyList(),
    val balance: Double = 0.0,
    val pickedDate: ZonedDateTime = ZonedDateTime.now(),
    val isDropDownMenuVisible: Boolean = false,
    val month : Int = ZonedDateTime.now().monthValue,
    val year : Int = ZonedDateTime.now().year,
    val monthlySpendingList : List<Double> = emptyList()
)
