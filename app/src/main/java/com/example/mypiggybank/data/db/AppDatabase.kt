package com.example.mypiggybank.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SavingsState::class, Deposit::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savingsDao(): SavingsDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "piggybank.db"
                ).build().also { INSTANCE = it }
            }
    }
}
