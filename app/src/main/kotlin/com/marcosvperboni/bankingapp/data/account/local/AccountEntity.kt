package com.marcosvperboni.bankingapp.data.account.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_account")
data class AccountEntity(
    @PrimaryKey val id: String,
    val ownerName: String,
    val balance: String,
    val currency: String,
)
