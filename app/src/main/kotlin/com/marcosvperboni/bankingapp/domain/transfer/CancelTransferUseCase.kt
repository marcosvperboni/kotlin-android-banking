package com.marcosvperboni.bankingapp.domain.transfer

import javax.inject.Inject

class CancelTransferUseCase @Inject constructor(
    private val transferRepository: TransferRepository,
) {
    suspend operator fun invoke(transferId: String): Result<Unit> =
        transferRepository.cancelTransfer(transferId)
}
