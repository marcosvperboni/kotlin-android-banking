package com.marcosvperboni.bankingapp.data.transfer

import com.marcosvperboni.bankingapp.data.common.safeApiCall
import com.marcosvperboni.bankingapp.data.transfer.dto.CreateTransferRequestDto
import com.marcosvperboni.bankingapp.domain.transfer.Transfer
import com.marcosvperboni.bankingapp.domain.transfer.TransferRepository
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import javax.inject.Inject

class TransferRepositoryImpl @Inject constructor(
    private val api: TransferApiService,
    private val json: Json,
) : TransferRepository {

    override suspend fun createTransfer(
        fromAccountId: String,
        toAccountId: String,
        amount: BigDecimal,
        description: String,
    ): Result<Transfer> =
        safeApiCall(json) {
            api.createTransfer(
                CreateTransferRequestDto(fromAccountId, toAccountId, amount.toPlainString(), description),
            )
        }.map { it.toDomain() }

    override suspend fun getTransfer(transferId: String): Result<Transfer> =
        safeApiCall(json) { api.getTransfer(transferId) }.map { it.toDomain() }

    override suspend fun cancelTransfer(transferId: String): Result<Unit> =
        safeApiCall(json) { api.cancelTransfer(transferId) }.map { Unit }

    override suspend fun listTransfers(accountId: String): Result<List<Transfer>> =
        safeApiCall(json) { api.listTransfers(accountId) }.map { list -> list.map { it.toDomain() } }
}
