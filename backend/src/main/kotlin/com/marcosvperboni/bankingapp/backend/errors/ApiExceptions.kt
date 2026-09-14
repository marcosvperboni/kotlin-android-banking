package com.marcosvperboni.bankingapp.backend.errors

sealed class ApiException(message: String) : Exception(message)
class ValidationException(message: String) : ApiException(message)
class UnauthorizedException(message: String = "Unauthorized") : ApiException(message)
class NotFoundException(message: String) : ApiException(message)
