package com.example.mypiggybank

import android.app.Application
import com.example.mypiggybank.data.db.AppDatabase
import com.example.mypiggybank.data.SavingsRepository

class MyPiggyBankApp : Application() {
    // Very small, simple service locator for the demo
    lateinit var repository: SavingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.get(this)
        repository = SavingsRepository(db.savingsDao())
    }
}
