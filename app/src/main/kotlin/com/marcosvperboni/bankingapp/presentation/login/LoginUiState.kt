package com.marcosvperboni.bankingapp.presentation.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSucceeded: Boolean = false,
)
