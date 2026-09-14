package com.marcosvperboni.bankingapp.domain.account

import javax.inject.Inject

class GetAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(accountId: String): Result<Account> =
        accountRepository.getAccount(accountId)
}
