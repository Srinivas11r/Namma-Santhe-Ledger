package com.example.namma_santheledger.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Reports : Screen("reports", "Reports", Icons.Default.Assessment)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)

    object AddTransaction : Screen("add_transaction/{customerId}") {
        fun createRoute(customerId: Long) = "add_transaction/$customerId"
    }
    object CustomerDetail : Screen("customer_detail/{customerId}") {
        fun createRoute(customerId: Long) = "customer_detail/$customerId"
    }
}
