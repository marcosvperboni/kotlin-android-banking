package com.marcosvperboni.bankingapp.data.account.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

/** Only the most recent page is cached, enough to show a statement while offline. */
@Dao
interface TransactionDao {
    @Upsert
    suspend fun upsertAll(transactions: List<TransactionEntity>)

    @Query("SELECT * FROM cached_transaction WHERE accountId = :accountId ORDER BY timestamp DESC LIMIT :size")
    suspend fun getLatest(accountId: String, size: Int): List<TransactionEntity>

    @Query("DELETE FROM cached_transaction WHERE accountId = :accountId")
    suspend fun clearForAccount(accountId: String)
}
