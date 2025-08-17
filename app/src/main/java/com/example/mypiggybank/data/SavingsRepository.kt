package com.example.mypiggybank.data

import com.example.mypiggybank.data.db.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SavingsRepository(private val dao: SavingsDao) {

    val state: Flow<SavingsState> = dao.observeState().map { it ?: SavingsState() }
    val history: Flow<List<Deposit>> = dao.observeDeposits()

    suspend fun setGoal(goal: Long) {
        val current = dao.getState() ?: SavingsState()
        dao.upsertState(current.copy(goalAmount = goal))
    }

    suspend fun addDeposit(amount: Long) {
        if (amount <= 0) return
        val current = dao.getState() ?: SavingsState()
        val updated = current.copy(currentAmount = current.currentAmount + amount)
        dao.upsertState(updated)
        dao.insertDeposit(Deposit(amount = amount))
    }
}
