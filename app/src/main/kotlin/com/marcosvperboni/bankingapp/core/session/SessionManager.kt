package com.marcosvperboni.bankingapp.core.session

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Broadcasts forced-logout events (e.g. a 401 from the API) so the navigation layer can react
 * by clearing the back stack and returning to the login screen, without the network layer
 * depending on any UI/navigation code.
 */
@Singleton
class SessionManager @Inject constructor() {
    private val _sessionExpired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpired: SharedFlow<Unit> = _sessionExpired.asSharedFlow()

    fun notifySessionExpired() {
        _sessionExpired.tryEmit(Unit)
    }
}
