package com.marcosvperboni.bankingapp.data.account.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_transaction")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val accountId: String,
    val description: String,
    val amount: String,
    val direction: String,
    val timestamp: Long,
)
