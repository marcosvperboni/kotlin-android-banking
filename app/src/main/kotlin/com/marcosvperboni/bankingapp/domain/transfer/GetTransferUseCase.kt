package com.marcosvperboni.bankingapp.domain.transfer

import javax.inject.Inject

class GetTransferUseCase @Inject constructor(
    private val transferRepository: TransferRepository,
) {
    suspend operator fun invoke(transferId: String): Result<Transfer> =
        transferRepository.getTransfer(transferId)
}

class ListTransfersUseCase @Inject constructor(
    private val transferRepository: TransferRepository,
) {
    suspend operator fun invoke(accountId: String): Result<List<Transfer>> =
        transferRepository.listTransfers(accountId)
}
