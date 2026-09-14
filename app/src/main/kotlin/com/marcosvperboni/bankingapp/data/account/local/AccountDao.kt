package com.marcosvperboni.bankingapp.data.account.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Upsert
    suspend fun upsert(account: AccountEntity)

    @Query("SELECT * FROM cached_account WHERE id = :accountId LIMIT 1")
    fun observe(accountId: String): Flow<AccountEntity?>

    @Query("SELECT * FROM cached_account WHERE id = :accountId LIMIT 1")
    suspend fun get(accountId: String): AccountEntity?
}
