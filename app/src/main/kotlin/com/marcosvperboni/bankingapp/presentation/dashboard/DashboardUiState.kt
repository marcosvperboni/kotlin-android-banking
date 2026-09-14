package com.marcosvperboni.bankingapp.presentation.dashboard

import com.marcosvperboni.bankingapp.domain.account.Account

data class DashboardUiState(
    val isLoading: Boolean = true,
    val account: Account? = null,
    val errorMessage: String? = null,
)
