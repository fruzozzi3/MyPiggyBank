package com.example.mypiggybank.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mypiggybank.data.SavingsRepository
import com.example.mypiggybank.data.db.Deposit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val goal: Long = 0L,
    val current: Long = 0L,
    val remaining: Long = 0L,
    val progress: Float = 0f,
    val history: List<Deposit> = emptyList()
)

class SavingsViewModel(private val repo: SavingsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        viewModelScope.launch {
            combine(repo.state, repo.history) { state, history ->
                val goal = state.goalAmount
                val current = state.currentAmount
                UiState(
                    goal = goal,
                    current = current,
                    remaining = (goal - current).coerceAtLeast(0),
                    progress = if (goal == 0L) 0f else (current.toFloat() / goal).coerceIn(0f, 1f),
                    history = history
                )
            }.stateIn(viewModelScope).collect { st ->
                _uiState.update { st }
            }
        }
    }

    fun setGoal(goal: Long) = viewModelScope.launch { repo.setGoal(goal) }
    fun addDeposit(amount: Long) = viewModelScope.launch { repo.addDeposit(amount) }
}

@Suppress("UNCHECKED_CAST")
class SavingsViewModelFactory(private val repo: SavingsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SavingsViewModel(repo) as T
    }
}
