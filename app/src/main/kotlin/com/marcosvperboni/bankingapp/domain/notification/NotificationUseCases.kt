package com.marcosvperboni.bankingapp.domain.notification

import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(): Result<List<NotificationItem>> =
        notificationRepository.getNotifications()
}

class MarkNotificationReadUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(notificationId: String): Result<Unit> =
        notificationRepository.markAsRead(notificationId)
}
