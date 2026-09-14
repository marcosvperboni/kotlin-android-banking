package com.marcosvperboni.bankingapp.presentation.statement

import com.marcosvperboni.bankingapp.domain.account.AccountTransaction

data class StatementUiState(
    val transactions: List<AccountTransaction> = emptyList(),
    val page: Int = 0,
    val hasMore: Boolean = true,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
)
