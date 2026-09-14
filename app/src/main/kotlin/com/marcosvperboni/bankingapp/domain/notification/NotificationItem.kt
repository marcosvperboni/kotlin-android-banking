package com.marcosvperboni.bankingapp.domain.notification

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val read: Boolean,
    val createdAtEpochMillis: Long,
)
