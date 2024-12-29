package com.ag_apps.spending_tracker.spending_overview.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ag_apps.spending_tracker.core.domain.CoreRepository
import com.ag_apps.spending_tracker.core.domain.LocalSpendingDataSource
import com.ag_apps.spending_tracker.core.domain.Spending
import com.ag_apps.spending_tracker.spending_overview.presentation.util.randomColor
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class SpendingOverviewViewModel(
    private val spendingDataSource: LocalSpendingDataSource,
    private val coreRepository: CoreRepository
) : ViewModel() {

    var state by mutableStateOf(SpendingOverviewState())
        private set

    fun onAction(action: SpendingOverviewAction) {
        when (action) {
            SpendingOverviewAction.LoadSpendingOverviewAndBalance -> {
                loadSpendingListAndBalance()
            }

            is SpendingOverviewAction.OnDateChange -> {
                viewModelScope.launch {
                    state = state.copy(
                        pickedDate = action.newDate,
                        spendingList = getSpendingListByDate(action.newDate)
                    )
                }
            }

            is SpendingOverviewAction.OnDeleteSpending -> {
                viewModelScope.launch {
                    spendingDataSource.deleteSpending(action.spendingId)
                    state = state.copy(
                        spendingList = getSpendingListByDate(state.pickedDate),
                        dateList = spendingDataSource.getAllDates(),
                        balance = coreRepository.getBalance() - spendingDataSource.getSpendBalance(),
                    )
                }
            }

            is SpendingOverviewAction.OnMonthChange -> {
                viewModelScope.launch {
                    val newMonth = action.newMonth
                    var adjustedYear = state.year
                    var adjustedMonth = newMonth

                    if (newMonth == 13) {
                        adjustedYear += 1
                        adjustedMonth = 1
                    } else if (newMonth == 0) {
                        adjustedYear -= 1
                        adjustedMonth = 12
                    }

                    state = state.copy(
                        year = adjustedYear,
                        month = adjustedMonth,
                        monthlySpendingList = getMonthlySpendingList(adjustedYear, adjustedMonth),
                    )
                }
            }
        }
    }

    private fun loadSpendingListAndBalance() {
        viewModelScope.launch {
            val allDates = spendingDataSource.getAllDates()

            val pickedDate = allDates.lastOrNull() ?: ZonedDateTime.now()
            val year = pickedDate.year
            val month = pickedDate.monthValue

            state = state.copy(
                spendingList = getSpendingListByDate(pickedDate),
                balance = coreRepository.getBalance() - spendingDataSource.getSpendBalance(),
                pickedDate = pickedDate,
                dateList = allDates.reversed(),
                monthlySpendingList = getMonthlySpendingList(year, month),
                year = year,
                month = month
            )
        }
    }


    private suspend fun getSpendingListByDate(date: ZonedDateTime): List<Spending> {

        return spendingDataSource
            .getSpendingsByDate(date)
            .reversed()
            .map { it.copy(color = randomColor()) }

    }

    private suspend fun getMonthlySpendingList(year: Int, month: Int): List<Double> {
        val startOfMonth = LocalDate.of(year, month, 1).atStartOfDay(ZoneId.systemDefault())
        val endOfMonth = LocalDate.of(year, month, LocalDate.of(year, month, 1).lengthOfMonth())
            .atStartOfDay(ZoneId.systemDefault())

        println(startOfMonth)
        println(endOfMonth)
        val spendings = spendingDataSource
            .getAllSpendings()
            .filter {
                val spendingDate = it.dateTimeUtc.withZoneSameInstant(ZoneId.systemDefault())
                spendingDate.year == year &&
                        spendingDate.monthValue == month &&
                        it.dateTimeUtc.isAfter(startOfMonth) &&
                        it.dateTimeUtc.isBefore(endOfMonth)
            }

        val groupedSpendings = spendings.groupBy {
            it.dateTimeUtc.withZoneSameInstant(ZoneId.systemDefault()).toLocalDate().dayOfMonth
        }

        val daysInMonth = startOfMonth.toLocalDate().lengthOfMonth()
        return (1..daysInMonth).map { day ->
            groupedSpendings[day]?.sumOf { it.price } ?: 0.0
        }
    }




}