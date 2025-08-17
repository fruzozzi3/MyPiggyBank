package com.example.mypiggybank.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_state")
data class SavingsState(
    @PrimaryKey val id: Int = 0,
    val goalAmount: Long = 0L,
    val currentAmount: Long = 0L
)
