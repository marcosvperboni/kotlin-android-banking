package com.marcosvperboni.bankingapp.domain.account

import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun getAccount(accountId: String): Result<Account>
    fun observeCachedAccount(accountId: String): Flow<Account?>
    suspend fun getStatement(accountId: String, page: Int, size: Int): Result<PagedResult<AccountTransaction>>
}
