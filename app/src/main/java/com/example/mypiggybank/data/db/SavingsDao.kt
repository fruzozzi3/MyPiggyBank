package com.example.mypiggybank.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsDao {
    // State
    @Query("SELECT * FROM savings_state WHERE id = 0")
    fun observeState(): Flow<SavingsState?>

    @Query("SELECT * FROM savings_state WHERE id = 0")
    suspend fun getState(): SavingsState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertState(state: SavingsState)

    // Deposits
    @Insert
    suspend fun insertDeposit(deposit: Deposit)

    @Query("SELECT * FROM deposits ORDER BY timestamp DESC")
    fun observeDeposits(): Flow<List<Deposit>>
}
