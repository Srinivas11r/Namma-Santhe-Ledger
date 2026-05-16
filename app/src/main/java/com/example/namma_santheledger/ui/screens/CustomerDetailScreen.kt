package com.example.namma_santheledger.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
fun CustomerDetailScreen(
    customerId: Long,
    viewModel: LedgerViewModel,
    onBack: () -> Unit
) {
    val customers by viewModel.customers.collectAsState()
    val customer = customers.find { it.id == customerId }
    val transactions by viewModel.getTransactionsForCustomer(customerId).collectAsState(initial = emptyList())
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(customer?.name ?: "Customer Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (customer != null) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Professional Header Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(customer.name.take(1).uppercase(), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(customer.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        if (customer.phoneNumber.isNotEmpty()) {
                            Text(customer.phoneNumber, color = Color.Gray)
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Total Balance", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                Text(
                                    "₹${"%,.2f".format(customer.totalOutstanding)}",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Black,
                                    color = if (customer.totalOutstanding > 0) Color(0xFFD32F2F) else Color(0xFF388E3C)
                                )
                            }
                        }

                        if (customer.phoneNumber.isNotEmpty() && customer.totalOutstanding > 0) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { sendWhatsAppReminder(context, customer.phoneNumber, customer.totalOutstanding) },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("WhatsApp Reminder", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Text(
                    "RECENT TRANSACTIONS",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                if (transactions.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No transactions yet.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(transactions) { transaction ->
                            TransactionItem(transaction)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: LedgerTransaction) {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateString = sdf.format(Date(transaction.timestamp))

    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        ListItem(
            headlineContent = { 
                Text(
                    if (transaction.type == TransactionType.CREDIT) "Gave Udari" else "Received Payment",
                    fontWeight = FontWeight.Bold
                ) 
            },
            supportingContent = { 
                Column {
                    if (transaction.note.isNotEmpty()) Text(transaction.note, style = MaterialTheme.typography.bodyMedium)
                    Text(dateString, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            },
            trailingContent = {
                Text(
                    "₹${"%,.0f".format(transaction.amount)}",
                    color = if (transaction.type == TransactionType.CREDIT) Color(0xFFD32F2F) else Color(0xFF388E3C),
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

fun sendWhatsAppReminder(context: Context, phoneNumber: String, amount: Double) {
    val message = "Hello, this is a reminder from Namma-Santhe Ledger. Your pending due is ₹${"%,.2f".format(amount)}. Please clear it soon. Thank you!"
    val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    context.startActivity(intent)
}
