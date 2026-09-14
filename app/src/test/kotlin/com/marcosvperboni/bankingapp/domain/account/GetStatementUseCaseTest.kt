package com.marcosvperboni.bankingapp.domain.account

import com.marcosvperboni.bankingapp.core.error.AppError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetStatementUseCaseTest {

    private val accountRepository = mockk<AccountRepository>()
    private lateinit var useCase: GetStatementUseCase

    @Before
    fun setUp() {
        useCase = GetStatementUseCase(accountRepository)
    }

    @Test
    fun `rejects a negative page`() = runTest {
        val result = useCase("acc-1", -1, 20)

        assertTrue(result.exceptionOrNull() is AppError.Validation)
        coVerify(exactly = 0) { accountRepository.getStatement(any(), any(), any()) }
    }

    @Test
    fun `rejects a zero or negative page size`() = runTest {
        val result = useCase("acc-1", 0, 0)

        assertTrue(result.exceptionOrNull() is AppError.Validation)
    }

    @Test
    fun `delegates to the repository for valid pagination`() = runTest {
        val expected = PagedResult<AccountTransaction>(emptyList(), page = 0, size = 20, hasMore = false)
        coEvery { accountRepository.getStatement("acc-1", 0, 20) } returns Result.success(expected)

        val result = useCase("acc-1", 0, 20)

        assertTrue(result.isSuccess)
    }
}
