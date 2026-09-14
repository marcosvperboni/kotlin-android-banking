package com.marcosvperboni.bankingapp.presentation.statement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.marcosvperboni.bankingapp.domain.account.AccountTransaction
import com.marcosvperboni.bankingapp.domain.account.TransactionDirection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatementScreen(viewModel: StatementViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statement") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        val errorMessage = uiState.errorMessage
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                errorMessage != null -> Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                )
                else -> LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(uiState.transactions, key = { it.id }) { transaction ->
                        TransactionRow(transaction)
                        HorizontalDivider()
                    }
                    if (uiState.hasMore) {
                        item {
                            LoadMoreTrigger(isLoading = uiState.isLoadingMore, onLoadMore = viewModel::loadNextPage)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(transaction: AccountTransaction) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = transaction.description, style = MaterialTheme.typography.bodyLarge)
        val sign = if (transaction.direction == TransactionDirection.CREDIT) "+" else "-"
        val color = if (transaction.direction == TransactionDirection.CREDIT) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.error
        }
        Text(text = "$sign ${transaction.amount}", style = MaterialTheme.typography.bodyMedium, color = color)
    }
}

@Composable
private fun LoadMoreTrigger(isLoading: Boolean, onLoadMore: () -> Unit) {
    androidx.compose.runtime.LaunchedEffect(Unit) { onLoadMore() }
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(8.dp))
        }
    }
}
