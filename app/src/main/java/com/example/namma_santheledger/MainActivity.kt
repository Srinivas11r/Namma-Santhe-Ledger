package com.example.namma_santheledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.namma_santheledger.ui.Screen
import com.example.namma_santheledger.ui.screens.*
import com.example.namma_santheledger.ui.theme.NammaSantheLedgerTheme
import com.example.namma_santheledger.ui.viewmodel.LedgerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NammaSantheLedgerTheme {
                LedgerApp()
            }
        }
    }
}

@Composable
fun LedgerApp() {
    val navController = rememberNavController()
    val viewModel: LedgerViewModel = hiltViewModel()
    
    val items = listOf(Screen.Home, Screen.Reports, Screen.Settings)

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            // Only show bottom bar on main screens
            if (items.any { it.route == currentDestination?.route }) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = null) },
                            label = { Text(screen.title, fontWeight = FontWeight.Bold) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController, 
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onCustomerClick = { customerId ->
                        navController.navigate(Screen.CustomerDetail.createRoute(customerId))
                    },
                    onAddTransactionClick = { customerId ->
                        navController.navigate(Screen.AddTransaction.createRoute(customerId))
                    }
                )
            }
            
            composable(Screen.Reports.route) {
                ReportsScreen(viewModel = viewModel)
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
            
            composable(
                route = Screen.AddTransaction.route,
                arguments = listOf(navArgument("customerId") { type = NavType.LongType })
            ) { backStackEntry ->
                val customerId = backStackEntry.arguments?.getLong("customerId") ?: 0L
                TransactionScreen(
                    customerId = customerId,
                    viewModel = viewModel,
                    onTransactionAdded = {
                        navController.popBackStack()
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(
                route = Screen.CustomerDetail.route,
                arguments = listOf(navArgument("customerId") { type = NavType.LongType })
            ) { backStackEntry ->
                val customerId = backStackEntry.arguments?.getLong("customerId") ?: 0L
                CustomerDetailScreen(
                    customerId = customerId,
                    viewModel = viewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
