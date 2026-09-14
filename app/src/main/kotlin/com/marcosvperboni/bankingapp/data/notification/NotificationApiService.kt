package com.marcosvperboni.bankingapp.data.notification

import com.marcosvperboni.bankingapp.data.notification.dto.NotificationDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotificationApiService {
    @GET("notifications")
    suspend fun getNotifications(): Response<List<NotificationDto>>

    @PUT("notifications/{id}")
    suspend fun markAsRead(@Path("id") notificationId: String): Response<NotificationDto>
}
