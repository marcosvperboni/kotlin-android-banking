package com.marcosvperboni.bankingapp.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.marcosvperboni.bankingapp.core.session.SessionManager
import com.marcosvperboni.bankingapp.presentation.dashboard.DashboardScreen
import com.marcosvperboni.bankingapp.presentation.login.LoginScreen
import com.marcosvperboni.bankingapp.presentation.notifications.NotificationsScreen
import com.marcosvperboni.bankingapp.presentation.statement.StatementScreen
import com.marcosvperboni.bankingapp.presentation.transfer.TransferScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BankingNavGraph(
    sessionManager: SessionManager,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.LOGIN,
) {
    LaunchedEffect(sessionManager) {
        sessionManager.sessionExpired.collectLatest {
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = hiltViewModel(),
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                viewModel = hiltViewModel(),
                onOpenStatement = { navController.navigate(Routes.STATEMENT) },
                onOpenTransfer = { navController.navigate(Routes.TRANSFER) },
                onOpenNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
            )
        }
        composable(Routes.STATEMENT) {
            StatementScreen(viewModel = hiltViewModel(), onBack = { navController.popBackStack() })
        }
        composable(Routes.TRANSFER) {
            TransferScreen(viewModel = hiltViewModel(), onBack = { navController.popBackStack() })
        }
        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(viewModel = hiltViewModel(), onBack = { navController.popBackStack() })
        }
    }
}
