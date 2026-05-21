package com.example.namma_santheledger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.namma_santheledger.data.entity.Customer
import com.example.namma_santheledger.data.entity.LedgerTransaction
import com.example.namma_santheledger.data.entity.TransactionType
import com.example.namma_santheledger.ui.viewmodel.LedgerViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(viewModel: LedgerViewModel) {
    val todayTransactions by viewModel.todayTransactions.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val dailyUdari by viewModel.dailyCredit.collectAsState()
    val dailyCash by viewModel.dailyPayment.collectAsState()
    val customers by viewModel.customers.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Business Reports", fontWeight = FontWeight.Black) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Earnings Summary
            Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("TODAY'S PROFIT", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("₹${"%,.0f".format(dailyUdari + dailyCash)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                    }
                    VerticalDivider(modifier = Modifier.height(40.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text("CASH RECEIVED", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("₹${"%,.0f".format(dailyCash)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Color(0xFF388E3C))
                    }
                }
            }

            // Tab Switcher with 3 options
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Today", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Today, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("History", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.History, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Users", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.People, contentDescription = null) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (selectedTab) {
                0 -> TransactionList(todayTransactions, customers, "No activity today.")
                1 -> TransactionList(allTransactions, customers, "No history found.")
                2 -> CustomerLifetimeList(customers)
            }
        }
    }
}

@Composable
fun TransactionList(transactions: List<LedgerTransaction>, customers: List<Customer>, emptyMsg: String) {
    if (transactions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(emptyMsg, color = Color.Gray)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
            items(transactions) { transaction ->
                val customerName = customers.find { it.id == transaction.customerId }?.name ?: "Unknown"
                DailyTransactionItem(transaction, customerName)
            }
        }
    }
}

@Composable
fun CustomerLifetimeList(customers: List<Customer>) {
    if (customers.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No customers registered.", color = Color.Gray)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
            items(customers) { customer ->
                LifetimeCustomerItem(customer)
            }
        }
    }
}

@Composable
fun DailyTransactionItem(transaction: LedgerTransaction, customerName: String) {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val timeString = sdf.format(Date(transaction.timestamp))

    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        ListItem(
            headlineContent = { Text(customerName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
            supportingContent = { Text(timeString, style = MaterialTheme.typography.bodySmall, color = Color.Gray) },
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "₹${"%,.0f".format(transaction.amount)}",
                        fontWeight = FontWeight.Black,
                        color = if (transaction.type == TransactionType.CREDIT) Color(0xFFD32F2F) else Color(0xFF388E3C)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (transaction.type == TransactionType.CREDIT) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (transaction.type == TransactionType.CREDIT) Color(0xFFD32F2F) else Color(0xFF388E3C)
                    )
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Composable
fun LifetimeCustomerItem(customer: Customer) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Box(contentAlignment = Alignment.Center) {
                    Text(customer.name.take(1).uppercase(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(customer.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    if (customer.phoneNumber.isNotEmpty()) customer.phoneNumber else "No phone",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${"%,.0f".format(Math.abs(customer.totalOutstanding))}",
                    color = if (customer.totalOutstanding > 0) Color(0xFFD32F2F) else if (customer.totalOutstanding < 0) Color(0xFF388E3C) else Color.Gray,
                    fontWeight = FontWeight.Black
                )
                Text(
                    if (customer.totalOutstanding > 0) "OWES" else if (customer.totalOutstanding < 0) "ADVANCE" else "SETTLED",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (customer.totalOutstanding > 0) Color(0xFFD32F2F) else if (customer.totalOutstanding < 0) Color(0xFF388E3C) else Color.Gray
                )
            }
        }
    }
}
