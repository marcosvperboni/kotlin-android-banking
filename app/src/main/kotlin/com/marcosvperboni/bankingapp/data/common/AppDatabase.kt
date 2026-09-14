package com.marcosvperboni.bankingapp.data.common

import androidx.room.Database
import androidx.room.RoomDatabase
import com.marcosvperboni.bankingapp.data.account.local.AccountDao
import com.marcosvperboni.bankingapp.data.account.local.AccountEntity
import com.marcosvperboni.bankingapp.data.account.local.TransactionDao
import com.marcosvperboni.bankingapp.data.account.local.TransactionEntity

@Database(entities = [AccountEntity::class, TransactionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
}
