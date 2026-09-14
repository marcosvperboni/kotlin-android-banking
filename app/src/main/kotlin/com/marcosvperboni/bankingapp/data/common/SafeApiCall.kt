package com.marcosvperboni.bankingapp.data.common

import com.marcosvperboni.bankingapp.core.error.AppError
import kotlinx.serialization.json.Json
import okio.IOException
import retrofit2.Response

/**
 * Executes a Retrofit call and converts HTTP-level failures into the [AppError] hierarchy so
 * repositories never leak raw exceptions or Retrofit types to the domain layer.
 */
suspend fun <T> safeApiCall(json: Json, block: suspend () -> Response<T>): Result<T> {
    return try {
        val response = block()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            Result.success(body)
        } else {
            Result.failure(response.toAppError(json))
        }
    } catch (e: IOException) {
        Result.failure(AppError.Network(e.message ?: "No internet connection"))
    } catch (e: Exception) {
        Result.failure(AppError.Unknown(e.message ?: "Unexpected error"))
    }
}

private fun <T> Response<T>.toAppError(json: Json): AppError {
    val message = errorBody()?.string()?.let { raw ->
        runCatching { json.decodeFromString(ErrorEnvelope.serializer(), raw).message }.getOrNull()
    } ?: message()

    return when (code()) {
        400 -> AppError.Validation("request", message)
        401 -> AppError.Unauthorized(message)
        404 -> AppError.NotFound(message)
        in 500..599 -> AppError.Server(code(), message)
        else -> AppError.Unknown(message)
    }
}

@kotlinx.serialization.Serializable
private data class ErrorEnvelope(val message: String)
