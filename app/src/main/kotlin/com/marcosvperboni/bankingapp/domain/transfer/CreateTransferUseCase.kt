package com.marcosvperboni.bankingapp.domain.transfer

import com.marcosvperboni.bankingapp.core.error.AppError
import java.math.BigDecimal
import javax.inject.Inject

class CreateTransferUseCase @Inject constructor(
    private val transferRepository: TransferRepository,
) {
    suspend operator fun invoke(
        fromAccountId: String,
        toAccountId: String,
        amount: BigDecimal,
        description: String,
    ): Result<Transfer> {
        if (toAccountId.isBlank()) {
            return Result.failure(AppError.Validation("toAccountId", "Enter a recipient account"))
        }
        if (toAccountId == fromAccountId) {
            return Result.failure(AppError.Validation("toAccountId", "You cannot transfer to your own account"))
        }
        if (amount <= BigDecimal.ZERO) {
            return Result.failure(AppError.Validation("amount", "Amount must be greater than zero"))
        }
        return transferRepository.createTransfer(fromAccountId, toAccountId, amount, description.trim())
    }
}
