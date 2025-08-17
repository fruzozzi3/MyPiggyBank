package com.example.mypiggybank.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deposits")
data class Deposit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Long,
    val timestamp: Long = System.currentTimeMillis()
)
