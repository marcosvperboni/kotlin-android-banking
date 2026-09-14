package com.marcosvperboni.bankingapp.domain.auth

import com.marcosvperboni.bankingapp.core.error.AppError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private val authRepository = mockk<AuthRepository>()
    private lateinit var useCase: LoginUseCase

    @Before
    fun setUp() {
        useCase = LoginUseCase(authRepository)
    }

    @Test
    fun `rejects a blank or invalid email without calling the repository`() = runTest {
        val result = useCase("not-an-email", "password123")

        assertTrue(result.exceptionOrNull() is AppError.Validation)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `rejects a password shorter than 6 characters`() = runTest {
        val result = useCase("user@example.com", "123")

        assertTrue(result.exceptionOrNull() is AppError.Validation)
    }

    @Test
    fun `delegates to the repository with a trimmed email when input is valid`() = runTest {
        val session = AuthSession("token", User("1", "user@example.com", "User"))
        coEvery { authRepository.login("user@example.com", "password123") } returns Result.success(session)

        val result = useCase("  user@example.com  ", "password123")

        assertEquals(session, result.getOrNull())
    }
}
