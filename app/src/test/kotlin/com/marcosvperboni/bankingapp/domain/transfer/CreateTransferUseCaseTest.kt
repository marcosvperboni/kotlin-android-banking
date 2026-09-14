package com.marcosvperboni.bankingapp.domain.transfer

import com.marcosvperboni.bankingapp.core.error.AppError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class CreateTransferUseCaseTest {

    private val transferRepository = mockk<TransferRepository>()
    private lateinit var useCase: CreateTransferUseCase

    @Before
    fun setUp() {
        useCase = CreateTransferUseCase(transferRepository)
    }

    @Test
    fun `rejects a blank recipient`() = runTest {
        val result = useCase("acc-1", "", BigDecimal("10.00"), "rent")

        assertTrue(result.exceptionOrNull() is AppError.Validation)
        coVerify(exactly = 0) { transferRepository.createTransfer(any(), any(), any(), any()) }
    }

    @Test
    fun `rejects transferring to the same account`() = runTest {
        val result = useCase("acc-1", "acc-1", BigDecimal("10.00"), "rent")

        assertTrue(result.exceptionOrNull() is AppError.Validation)
    }

    @Test
    fun `rejects a zero amount`() = runTest {
        val result = useCase("acc-1", "acc-2", BigDecimal.ZERO, "rent")

        assertTrue(result.exceptionOrNull() is AppError.Validation)
    }

    @Test
    fun `rejects a negative amount`() = runTest {
        val result = useCase("acc-1", "acc-2", BigDecimal("-5.00"), "rent")

        assertTrue(result.exceptionOrNull() is AppError.Validation)
    }

    @Test
    fun `delegates to the repository when the request is valid`() = runTest {
        val expected = Transfer("trf-1", "acc-1", "acc-2", BigDecimal("10.00"), "rent", TransferStatus.PENDING, 0L)
        coEvery { transferRepository.createTransfer("acc-1", "acc-2", BigDecimal("10.00"), "rent") } returns Result.success(expected)

        val result = useCase("acc-1", "acc-2", BigDecimal("10.00"), "rent")

        assertTrue(result.isSuccess)
    }
}
