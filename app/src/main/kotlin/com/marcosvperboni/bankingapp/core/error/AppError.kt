package com.marcosvperboni.bankingapp.core.error

/** Typed failure hierarchy so ViewModels can render specific UI states instead of raw exceptions. */
sealed class AppError(message: String, cause: Throwable? = null) : Exception(message, cause) {
    data class Validation(val field: String, val reason: String) : AppError(reason)
    data class Unauthorized(val reason: String = "Session expired") : AppError(reason)
    data class NotFound(val resource: String) : AppError("$resource not found")
    data class Network(val reason: String) : AppError(reason)
    data class Server(val httpCode: Int, val reason: String) : AppError(reason)
    data class Unknown(val reason: String) : AppError(reason)
}

fun Throwable.toAppError(): AppError = when (this) {
    is AppError -> this
    else -> AppError.Unknown(message ?: "Unexpected error")
}
