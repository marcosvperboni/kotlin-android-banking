package com.marcosvperboni.bankingapp.presentation.transfer

import com.marcosvperboni.bankingapp.domain.transfer.Transfer

data class TransferUiState(
    val toAccountId: String = "",
    val amount: String = "",
    val description: String = "",
    val isSubmitting: Boolean = false,
    val formErrorMessage: String? = null,
    val transfers: List<Transfer> = emptyList(),
    val isLoadingList: Boolean = true,
    val listErrorMessage: String? = null,
    val selectedTransfer: Transfer? = null,
)
