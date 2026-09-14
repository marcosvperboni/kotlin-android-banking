package com.marcosvperboni.bankingapp.presentation.transfer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.marcosvperboni.bankingapp.domain.transfer.Transfer
import com.marcosvperboni.bankingapp.domain.transfer.TransferStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(viewModel: TransferViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transfers") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxWidth().padding(padding)) {
            item { TransferForm(uiState, viewModel) }
            item {
                Text(
                    text = "Pending transfers",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            val listErrorMessage = uiState.listErrorMessage
            when {
                uiState.isLoadingList -> item {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
                listErrorMessage != null -> item {
                    Text(
                        text = listErrorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp),
                    )
                }
                uiState.transfers.isEmpty() -> item {
                    Text(text = "No transfers yet", modifier = Modifier.padding(16.dp))
                }
                else -> items(uiState.transfers, key = { it.id }) { transfer ->
                    TransferRow(
                        transfer = transfer,
                        onClick = { viewModel.viewTransferDetail(transfer.id) },
                        onCancel = { viewModel.cancelTransfer(transfer.id) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    uiState.selectedTransfer?.let { transfer ->
        TransferDetailDialog(transfer = transfer, onDismiss = viewModel::dismissDetail)
    }
}

@Composable
private fun TransferForm(uiState: TransferUiState, viewModel: TransferViewModel) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "New transfer", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = uiState.toAccountId,
            onValueChange = viewModel::onToAccountChanged,
            label = { Text("Recipient account id") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = uiState.amount,
            onValueChange = viewModel::onAmountChanged,
            label = { Text("Amount") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = uiState.description,
            onValueChange = viewModel::onDescriptionChanged,
            label = { Text("Description (optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        uiState.formErrorMessage?.let { message ->
            Text(text = message, color = MaterialTheme.colorScheme.error)
        }
        Button(
            onClick = viewModel::submitTransfer,
            enabled = !uiState.isSubmitting,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (uiState.isSubmitting) "Sending..." else "Send transfer")
        }
    }
}

@Composable
private fun TransferRow(transfer: Transfer, onClick: () -> Unit, onCancel: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "To ${transfer.toAccountId}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "${transfer.amount} · ${transfer.status}", style = MaterialTheme.typography.bodyMedium)
        }
        Row {
            TextButton(onClick = onClick) { Text("View") }
            if (transfer.status == TransferStatus.PENDING) {
                TextButton(onClick = onCancel) { Text("Cancel") }
            }
        }
    }
}

@Composable
private fun TransferDetailDialog(transfer: Transfer, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
        title = { Text("Transfer details") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("ID: ${transfer.id}")
                Text("From: ${transfer.fromAccountId}")
                Text("To: ${transfer.toAccountId}")
                Text("Amount: ${transfer.amount}")
                Text("Status: ${transfer.status}")
                Text("Description: ${transfer.description.ifBlank { "-" }}")
            }
        },
    )
}
