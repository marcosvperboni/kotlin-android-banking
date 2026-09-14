package com.marcosvperboni.bankingapp.presentation.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcosvperboni.bankingapp.domain.auth.AuthRepository
import com.marcosvperboni.bankingapp.domain.transfer.CancelTransferUseCase
import com.marcosvperboni.bankingapp.domain.transfer.CreateTransferUseCase
import com.marcosvperboni.bankingapp.domain.transfer.GetTransferUseCase
import com.marcosvperboni.bankingapp.domain.transfer.ListTransfersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val createTransferUseCase: CreateTransferUseCase,
    private val cancelTransferUseCase: CancelTransferUseCase,
    private val getTransferUseCase: GetTransferUseCase,
    private val listTransfersUseCase: ListTransfersUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    init {
        loadTransfers()
    }

    fun onToAccountChanged(value: String) {
        _uiState.update { it.copy(toAccountId = value, formErrorMessage = null) }
    }

    fun onAmountChanged(value: String) {
        _uiState.update { it.copy(amount = value, formErrorMessage = null) }
    }

    fun onDescriptionChanged(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun loadTransfers() {
        viewModelScope.launch {
            val accountId = authRepository.currentUserId() ?: return@launch
            _uiState.update { it.copy(isLoadingList = true, listErrorMessage = null) }
            listTransfersUseCase(accountId)
                .onSuccess { transfers -> _uiState.update { it.copy(isLoadingList = false, transfers = transfers) } }
                .onFailure { error -> _uiState.update { it.copy(isLoadingList = false, listErrorMessage = error.message) } }
        }
    }

    fun submitTransfer() {
        val state = _uiState.value
        val amount = state.amount.toBigDecimalOrNull()
        if (amount == null) {
            _uiState.update { it.copy(formErrorMessage = "Enter a valid amount") }
            return
        }
        viewModelScope.launch {
            val fromAccountId = authRepository.currentUserId()
            if (fromAccountId == null) {
                _uiState.update { it.copy(formErrorMessage = "Session expired") }
                return@launch
            }
            _uiState.update { it.copy(isSubmitting = true, formErrorMessage = null) }
            createTransferUseCase(fromAccountId, state.toAccountId, amount, state.description)
                .onSuccess {
                    _uiState.update {
                        it.copy(isSubmitting = false, toAccountId = "", amount = "", description = "")
                    }
                    loadTransfers()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSubmitting = false, formErrorMessage = error.message) }
                }
        }
    }

    fun cancelTransfer(transferId: String) {
        viewModelScope.launch {
            cancelTransferUseCase(transferId)
                .onSuccess { loadTransfers() }
                .onFailure { error -> _uiState.update { it.copy(listErrorMessage = error.message) } }
        }
    }

    fun viewTransferDetail(transferId: String) {
        viewModelScope.launch {
            getTransferUseCase(transferId)
                .onSuccess { transfer -> _uiState.update { it.copy(selectedTransfer = transfer) } }
                .onFailure { error -> _uiState.update { it.copy(listErrorMessage = error.message) } }
        }
    }

    fun dismissDetail() {
        _uiState.update { it.copy(selectedTransfer = null) }
    }
}
