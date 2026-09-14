package com.marcosvperboni.bankingapp.presentation.transfer

import app.cash.turbine.test
import com.marcosvperboni.bankingapp.domain.auth.AuthRepository
import com.marcosvperboni.bankingapp.domain.transfer.CancelTransferUseCase
import com.marcosvperboni.bankingapp.domain.transfer.CreateTransferUseCase
import com.marcosvperboni.bankingapp.domain.transfer.GetTransferUseCase
import com.marcosvperboni.bankingapp.domain.transfer.ListTransfersUseCase
import com.marcosvperboni.bankingapp.domain.transfer.Transfer
import com.marcosvperboni.bankingapp.domain.transfer.TransferRepository
import com.marcosvperboni.bankingapp.domain.transfer.TransferStatus
import com.marcosvperboni.bankingapp.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class TransferViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val transferRepository = mockk<TransferRepository>()
    private val authRepository = mockk<AuthRepository> {
        coEvery { currentUserId() } returns "acc-1"
    }

    private fun viewModel(): TransferViewModel = TransferViewModel(
        createTransferUseCase = CreateTransferUseCase(transferRepository),
        cancelTransferUseCase = CancelTransferUseCase(transferRepository),
        getTransferUseCase = GetTransferUseCase(transferRepository),
        listTransfersUseCase = ListTransfersUseCase(transferRepository),
        authRepository = authRepository,
    )

    @Before
    fun setUp() {
        coEvery { transferRepository.listTransfers("acc-1") } returns Result.success(emptyList())
    }

    @Test
    fun `loads pending transfers on init`() = runTest {
        val transfer = Transfer("trf-1", "acc-1", "acc-2", BigDecimal("10.00"), "", TransferStatus.PENDING, 0L)
        coEvery { transferRepository.listTransfers("acc-1") } returns Result.success(listOf(transfer))

        viewModel().uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoadingList)
            assertEquals(1, state.transfers.size)
        }
    }

    @Test
    fun `submit with an invalid amount shows a form error and does not call the repository`() = runTest {
        val viewModel = viewModel()
        viewModel.onToAccountChanged("acc-2")
        viewModel.onAmountChanged("not-a-number")

        viewModel.submitTransfer()

        viewModel.uiState.test {
            assertEquals("Enter a valid amount", awaitItem().formErrorMessage)
        }
        coVerify(exactly = 0) { transferRepository.createTransfer(any(), any(), any(), any()) }
    }

    @Test
    fun `successful submit clears the form and reloads the list`() = runTest {
        val created = Transfer("trf-1", "acc-1", "acc-2", BigDecimal("10.00"), "", TransferStatus.PENDING, 0L)
        coEvery { transferRepository.createTransfer("acc-1", "acc-2", BigDecimal("10.00"), "") } returns Result.success(created)
        val viewModel = viewModel()
        viewModel.onToAccountChanged("acc-2")
        viewModel.onAmountChanged("10.00")

        viewModel.submitTransfer()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isSubmitting)
            assertEquals("", state.toAccountId)
            assertEquals("", state.amount)
        }
        coVerify { transferRepository.listTransfers("acc-1") }
    }

    @Test
    fun `cancelling a transfer refreshes the list`() = runTest {
        coEvery { transferRepository.cancelTransfer("trf-1") } returns Result.success(Unit)
        val viewModel = viewModel()

        viewModel.cancelTransfer("trf-1")

        coVerify { transferRepository.cancelTransfer("trf-1") }
        coVerify(atLeast = 2) { transferRepository.listTransfers("acc-1") }
    }

    @Test
    fun `viewing a transfer detail populates the selected transfer`() = runTest {
        val transfer = Transfer("trf-1", "acc-1", "acc-2", BigDecimal("10.00"), "", TransferStatus.PENDING, 0L)
        coEvery { transferRepository.getTransfer("trf-1") } returns Result.success(transfer)
        val viewModel = viewModel()

        viewModel.viewTransferDetail("trf-1")

        viewModel.uiState.test {
            assertEquals(transfer, awaitItem().selectedTransfer)
        }

        viewModel.dismissDetail()
        viewModel.uiState.test {
            assertTrue(awaitItem().selectedTransfer == null)
        }
    }
}
