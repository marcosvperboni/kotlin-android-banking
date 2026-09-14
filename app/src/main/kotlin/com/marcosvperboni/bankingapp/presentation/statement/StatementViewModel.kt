package com.marcosvperboni.bankingapp.presentation.statement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcosvperboni.bankingapp.domain.account.GetStatementUseCase
import com.marcosvperboni.bankingapp.domain.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PAGE_SIZE = 20

@HiltViewModel
class StatementViewModel @Inject constructor(
    private val getStatementUseCase: GetStatementUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatementUiState())
    val uiState: StateFlow<StatementUiState> = _uiState.asStateFlow()

    init {
        loadFirstPage()
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            fetchPage(page = 0, append = false)
        }
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoadingMore || !state.hasMore) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            fetchPage(page = state.page + 1, append = true)
        }
    }

    private suspend fun fetchPage(page: Int, append: Boolean) {
        val accountId = authRepository.currentUserId()
        if (accountId == null) {
            _uiState.update { it.copy(isLoading = false, isLoadingMore = false, errorMessage = "Session expired") }
            return
        }
        getStatementUseCase(accountId, page, PAGE_SIZE)
            .onSuccess { result ->
                _uiState.update {
                    it.copy(
                        transactions = if (append) it.transactions + result.items else result.items,
                        page = result.page,
                        hasMore = result.hasMore,
                        isLoading = false,
                        isLoadingMore = false,
                    )
                }
            }
            .onFailure { error ->
                _uiState.update { it.copy(isLoading = false, isLoadingMore = false, errorMessage = error.message) }
            }
    }
}
