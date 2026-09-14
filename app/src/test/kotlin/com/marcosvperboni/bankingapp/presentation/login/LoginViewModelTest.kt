package com.marcosvperboni.bankingapp.presentation.login

import app.cash.turbine.test
import com.marcosvperboni.bankingapp.core.error.AppError
import com.marcosvperboni.bankingapp.domain.auth.AuthRepository
import com.marcosvperboni.bankingapp.domain.auth.AuthSession
import com.marcosvperboni.bankingapp.domain.auth.LoginUseCase
import com.marcosvperboni.bankingapp.domain.auth.User
import com.marcosvperboni.bankingapp.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepository = mockk<AuthRepository>()
    private val loginUseCase = LoginUseCase(authRepository)

    private fun viewModel(): LoginViewModel {
        coEvery { authRepository.isLoggedIn() } returns false
        return LoginViewModel(loginUseCase, authRepository)
    }

    @Test
    fun `initial state is not logged in when no session is stored`() = runTest {
        viewModel().uiState.test {
            assertFalse(awaitItem().loginSucceeded)
        }
    }

    @Test
    fun `login success updates state and clears error`() = runTest {
        coEvery { authRepository.login("user@example.com", "password123") } returns
            Result.success(AuthSession("token", User("1", "user@example.com", "User")))
        val viewModel = viewModel()

        viewModel.onEmailChanged("user@example.com")
        viewModel.onPasswordChanged("password123")

        viewModel.login()

        // The mocked repository resolves synchronously, so intermediate "isLoading" emissions are
        // conflated by the time a collector subscribes; only the settled terminal state is asserted.
        viewModel.uiState.test {
            val success = awaitItem()
            assertFalse(success.isLoading)
            assertTrue(success.loginSucceeded)
            assertEquals(null, success.errorMessage)
        }
    }

    @Test
    fun `login failure surfaces the error message without succeeding`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns Result.failure(AppError.Unauthorized("Invalid email or password"))
        val viewModel = viewModel()
        viewModel.onEmailChanged("user@example.com")
        viewModel.onPasswordChanged("password123")

        viewModel.login()

        viewModel.uiState.test {
            val failed = awaitItem()
            assertFalse(failed.loginSucceeded)
            assertEquals("Invalid email or password", failed.errorMessage)
        }
    }
}
