package com.marcosvperboni.bankingapp.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcosvperboni.bankingapp.domain.notification.GetNotificationsUseCase
import com.marcosvperboni.bankingapp.domain.notification.MarkNotificationReadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markNotificationReadUseCase: MarkNotificationReadUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getNotificationsUseCase()
                .onSuccess { list -> _uiState.update { it.copy(isLoading = false, notifications = list) } }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, errorMessage = error.message) } }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            markNotificationReadUseCase(notificationId).onSuccess {
                _uiState.update { state ->
                    state.copy(notifications = state.notifications.map { item ->
                        if (item.id == notificationId) item.copy(read = true) else item
                    })
                }
            }
        }
    }
}
