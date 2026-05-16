package com.example.namma_santheledger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.namma_santheledger.data.entity.LedgerTransaction
import com.example.namma_santheledger.data.entity.TransactionType
import com.example.namma_santheledger.ui.viewmodel.LedgerViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(viewModel: LedgerViewModel) {
    val transactions by viewModel.todayTransactions.collectAsState()
    val dailyUdari by viewModel.dailyCredit.collectAsState()
    val dailyCash by viewModel.dailyPayment.collectAsState()
    val customers by viewModel.customers.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Daily Market Report", fontWeight = FontWeight.Black) },
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
            // Requirement 2: Understand "Daily Profit"
            Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("DAILY EARNINGS (PROFIT)", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("₹${"%,.0f".format(dailyUdari + dailyCash)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                    }
                    VerticalDivider(modifier = Modifier.height(40.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text("CASH COLLECTED", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("₹${"%,.0f".format(dailyCash)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Color(0xFF388E3C))
                    }
                }
            }

            Text(
                "TODAY'S ACTIVITY LOG",
                modifier = Modifier.padding(start = 20.dp, bottom = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            if (transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No activity recorded today.", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(transactions) { transaction ->
                        val customerName = customers.find { it.id == transaction.customerId }?.name ?: "Unknown"
                        DailyTransactionItem(transaction, customerName)
                    }
                }
            }
        }
    }
}

@Composable
fun DailyTransactionItem(transaction: LedgerTransaction, customerName: String) {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
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
