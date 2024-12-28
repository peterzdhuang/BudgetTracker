package com.ag_apps.spending_tracker.balance.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ag_apps.spending_tracker.core.domain.CoreRepository
import com.ag_apps.spending_tracker.core.presentation.util.Screen
import com.ag_apps.spending_tracker.spending_details.presentation.SpendingDetailsEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class BalanceViewModel(
    private val coreRepository: CoreRepository
) : ViewModel() {

    var state by mutableStateOf(BalanceState())
        private set

    private val _eventChannel = Channel<BalanceEvent>()
    val event = _eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            state = state.copy(
                balance = coreRepository.getBalance()
            )
        }
    }

    fun onAction(action: BalanceAction) {
        when (action) {
            is BalanceAction.OnBalanceChanged -> {
                state = state.copy(
                    balance = action.newBalance
                )
            }

            BalanceAction.OnBalanceSaved -> {
                viewModelScope.launch {
                    coreRepository.updateBalance(state.balance)
                }
            }

            BalanceAction.goBack -> {
                viewModelScope.launch {
                    _eventChannel.send(BalanceEvent.NavigateBack)
                }
            }


        }
    }

}