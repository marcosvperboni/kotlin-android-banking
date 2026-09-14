package com.marcosvperboni.bankingapp.data.account

import com.marcosvperboni.bankingapp.data.account.dto.AccountDto
import com.marcosvperboni.bankingapp.data.account.dto.StatementPageDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AccountApiService {
    @GET("accounts/{id}")
    suspend fun getAccount(@Path("id") accountId: String): Response<AccountDto>

    @GET("accounts/{id}/statement")
    suspend fun getStatement(
        @Path("id") accountId: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Response<StatementPageDto>
}
