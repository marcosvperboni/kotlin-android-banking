package com.marcosvperboni.bankingapp.presentation.notifications

import com.marcosvperboni.bankingapp.domain.notification.NotificationItem

data class NotificationsUiState(
    val notifications: List<NotificationItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
