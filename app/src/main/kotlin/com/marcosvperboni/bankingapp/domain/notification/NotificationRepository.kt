package com.marcosvperboni.bankingapp.domain.notification

interface NotificationRepository {
    suspend fun getNotifications(): Result<List<NotificationItem>>
    suspend fun markAsRead(notificationId: String): Result<Unit>
}
