package com.marcosvperboni.bankingapp.data.account

import com.marcosvperboni.bankingapp.core.error.AppError
import com.marcosvperboni.bankingapp.data.account.local.AccountDao
import com.marcosvperboni.bankingapp.data.account.local.TransactionDao
import com.marcosvperboni.bankingapp.data.common.safeApiCall
import com.marcosvperboni.bankingapp.domain.account.Account
import com.marcosvperboni.bankingapp.domain.account.AccountRepository
import com.marcosvperboni.bankingapp.domain.account.AccountTransaction
import com.marcosvperboni.bankingapp.domain.account.PagedResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val api: AccountApiService,
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val json: Json,
) : AccountRepository {

    override suspend fun getAccount(accountId: String): Result<Account> =
        safeApiCall(json) { api.getAccount(accountId) }
            .onSuccess { accountDao.upsert(it.toEntity()) }
            .map { it.toDomain() }

    override fun observeCachedAccount(accountId: String): Flow<Account?> =
        accountDao.observe(accountId).map { it?.toDomain() }

    override suspend fun getStatement(accountId: String, page: Int, size: Int): Result<PagedResult<AccountTransaction>> =
        safeApiCall(json) { api.getStatement(accountId, page, size) }
            .onSuccess { dto ->
                if (page == 0) {
                    transactionDao.clearForAccount(accountId)
                    transactionDao.upsertAll(dto.items.map { it.toEntity() })
                }
            }
            .map { dto ->
                PagedResult(dto.items.map { it.toDomain() }, dto.page, dto.size, dto.hasMore)
            }
            .recoverCatching { error ->
                if (page == 0 && error is AppError.Network) {
                    val cached = transactionDao.getLatest(accountId, size).map { it.toDomain() }
                    if (cached.isNotEmpty()) {
                        return@recoverCatching PagedResult(cached, page = 0, size = size, hasMore = false)
                    }
                }
                throw error
            }
}
