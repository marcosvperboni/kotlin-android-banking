package com.marcosvperboni.bankingapp.data.notification.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: String,
    val title: String,
    val message: String,
    val read: Boolean,
    val createdAt: Long,
)
