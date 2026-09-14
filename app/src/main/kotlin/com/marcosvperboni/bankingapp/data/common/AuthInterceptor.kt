package com.marcosvperboni.bankingapp.data.common

import com.marcosvperboni.bankingapp.core.session.SessionManager
import com.marcosvperboni.bankingapp.data.auth.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Attaches the stored JWT as a Bearer token on every outgoing request and, on a 401 response,
 * clears the local session and notifies [SessionManager] so the UI can force a re-login.
 */
class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val sessionManager: SessionManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenStorage.getToken()
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        val response = chain.proceed(request)
        if (response.code == 401) {
            tokenStorage.clear()
            sessionManager.notifySessionExpired()
        }
        return response
    }
}
