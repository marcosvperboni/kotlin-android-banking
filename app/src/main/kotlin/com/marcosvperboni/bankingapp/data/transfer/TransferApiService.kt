package com.marcosvperboni.bankingapp.data.transfer

import com.marcosvperboni.bankingapp.data.transfer.dto.CreateTransferRequestDto
import com.marcosvperboni.bankingapp.data.transfer.dto.TransferDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Query

interface TransferApiService {
    @POST("transfers")
    suspend fun createTransfer(@Body request: CreateTransferRequestDto): Response<TransferDto>

    @GET("transfers/{id}")
    suspend fun getTransfer(@Path("id") transferId: String): Response<TransferDto>

    @DELETE("transfers/{id}")
    suspend fun cancelTransfer(@Path("id") transferId: String): Response<TransferDto>

    @GET("transfers")
    suspend fun listTransfers(@Query("accountId") accountId: String): Response<List<TransferDto>>
}
