package com.marcosvperboni.bankingapp.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onOpenStatement: () -> Unit,
    onOpenTransfer: () -> Unit,
    onOpenNotifications: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My account") },
                actions = {
                    IconButtonAction(onClick = onOpenNotifications)
                },
            )
        },
    ) { padding ->
        DashboardContent(
            uiState = uiState,
            padding = padding,
            onRetry = viewModel::loadAccount,
            onOpenStatement = onOpenStatement,
            onOpenTransfer = onOpenTransfer,
        )
    }
}

@Composable
private fun IconButtonAction(onClick: () -> Unit) {
    androidx.compose.material3.IconButton(onClick = onClick) {
        Icon(imageVector = Icons.Filled.Notifications, contentDescription = "Notifications")
    }
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    padding: PaddingValues,
    onRetry: () -> Unit,
    onOpenStatement: () -> Unit,
    onOpenTransfer: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when {
            uiState.isLoading -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) { CircularProgressIndicator() }

            uiState.errorMessage != null -> Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                Button(onClick = onRetry) { Text("Retry") }
            }

            uiState.account != null -> {
                val account = uiState.account
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = account.ownerName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        Text(
                            text = "${account.currency} ${account.balance}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }

                OutlinedButton(onClick = onOpenStatement, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.Receipt, contentDescription = null)
                    Text(text = "  View statement")
                }
                Button(onClick = onOpenTransfer, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.SwapHoriz, contentDescription = null)
                    Text(text = "  New transfer")
                }
            }
        }
    }
}
