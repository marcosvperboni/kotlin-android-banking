package com.marcosvperboni.bankingapp.data.notification

import com.marcosvperboni.bankingapp.data.common.safeApiCall
import com.marcosvperboni.bankingapp.domain.notification.NotificationItem
import com.marcosvperboni.bankingapp.domain.notification.NotificationRepository
import kotlinx.serialization.json.Json
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val api: NotificationApiService,
    private val json: Json,
) : NotificationRepository {

    override suspend fun getNotifications(): Result<List<NotificationItem>> =
        safeApiCall(json) { api.getNotifications() }.map { list ->
            list.map { NotificationItem(it.id, it.title, it.message, it.read, it.createdAt) }
        }

    override suspend fun markAsRead(notificationId: String): Result<Unit> =
        safeApiCall(json) { api.markAsRead(notificationId) }.map { Unit }
}
