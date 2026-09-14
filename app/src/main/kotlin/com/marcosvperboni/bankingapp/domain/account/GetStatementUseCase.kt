package com.marcosvperboni.bankingapp.domain.account

import com.marcosvperboni.bankingapp.core.error.AppError
import javax.inject.Inject

class GetStatementUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(accountId: String, page: Int, size: Int): Result<PagedResult<AccountTransaction>> {
        if (page < 0 || size <= 0) {
            return Result.failure(AppError.Validation("page", "Invalid pagination parameters"))
        }
        return accountRepository.getStatement(accountId, page, size)
    }
}
