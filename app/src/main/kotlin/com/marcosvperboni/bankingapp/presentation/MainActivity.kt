package com.marcosvperboni.bankingapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.marcosvperboni.bankingapp.core.navigation.BankingNavGraph
import com.marcosvperboni.bankingapp.core.session.SessionManager
import com.marcosvperboni.bankingapp.core.theme.BankingAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BankingAppTheme {
                BankingNavGraph(sessionManager = sessionManager)
            }
        }
    }
}
