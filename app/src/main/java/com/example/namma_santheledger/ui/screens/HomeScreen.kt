package com.example.namma_santheledger.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.namma_santheledger.data.entity.Customer
import com.example.namma_santheledger.ui.viewmodel.LedgerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: LedgerViewModel,
    onCustomerClick: (Long) -> Unit,
    onAddTransactionClick: (Long) -> Unit
) {
    val customers by viewModel.customers.collectAsState()
    val totalOutstanding by viewModel.totalOutstanding.collectAsState()
    val dailyCredit by viewModel.dailyCredit.collectAsState()
    val dailyPayment by viewModel.dailyPayment.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showAddCustomerDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Namma-Santhe Ledger", 
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.headlineSmall
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddCustomerDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("New Customer") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF1F3F4))
        ) {
            SummarySection(totalOutstanding, dailyCredit, dailyPayment, customers.size)
            
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Text(
                text = "YOUR CUSTOMERS",
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )

            if (customers.isEmpty() && searchQuery.isEmpty()) {
                EmptyState(
                    title = "Digital Khata is Empty",
                    description = "Add your customers to start tracking their daily Udari and payments professionally.",
                    icon = Icons.Default.PersonAdd
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 88.dp, top = 4.dp)
                ) {
                    items(customers, key = { it.id }) { customer ->
                        CustomerItem(
                            customer = customer,
                            onClick = { onCustomerClick(customer.id) },
                            onQuickAddClick = { onAddTransactionClick(customer.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddCustomerDialog) {
        AddCustomerDialog(
            onDismiss = { showAddCustomerDialog = false },
            onConfirm = { name, phone ->
                viewModel.addCustomer(name, phone)
                showAddCustomerDialog = false
            }
        )
    }
}

@Composable
fun SummarySection(total: Double, dailyUdari: Double, dailyCash: Double, customerCount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(MaterialTheme.colorScheme.primary, Color(0xFF283593))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("NET BALANCE TO COLLECT", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                        Text("$customerCount Customers", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Text(
                    "₹${"%,.0f".format(total)}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SummaryMiniCard(
                        label = "Today's Udari", 
                        value = "₹${"%,.0f".format(dailyUdari)}", 
                        color = Color(0xFFFF8A80),
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryMiniCard(
                        label = "Today's Cash", 
                        value = "₹${"%,.0f".format(dailyCash)}", 
                        color = Color(0xFFB9F6CA),
                        icon = Icons.AutoMirrored.Filled.TrendingDown,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryMiniCard(label: String, value: String, color: Color, icon: ImageVector, modifier: Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = color)
                Spacer(modifier = Modifier.width(4.dp))
                Text(label, color = Color.White.copy(alpha = 0.6f), style = MaterialTheme.typography.labelSmall)
            }
            Text(value, color = color, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun CustomerItem(customer: Customer, onClick: () -> Unit, onQuickAddClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(modifier = Modifier.size(52.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Box(contentAlignment = Alignment.Center) {
                    Text(customer.name.take(1).uppercase(), fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(customer.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(
                    if (customer.totalOutstanding > 0) "Pending Due" else if (customer.totalOutstanding < 0) "Advance Paid" else "All Settled",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (customer.totalOutstanding > 0) Color(0xFFD32F2F) else if (customer.totalOutstanding < 0) Color(0xFF388E3C) else Color.Gray
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${"%,.0f".format(Math.abs(customer.totalOutstanding))}",
                    color = if (customer.totalOutstanding > 0) Color(0xFFD32F2F) else if (customer.totalOutstanding < 0) Color(0xFF388E3C) else Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
                IconButton(
                    onClick = onQuickAddClick, 
                    modifier = Modifier.size(32.dp).padding(top = 4.dp),
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun EmptyState(title: String, description: String, icon: ImageVector) {
    Column(modifier = Modifier.fillMaxSize().padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Surface(color = Color.White, shape = CircleShape, modifier = Modifier.size(100.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(description, textAlign = TextAlign.Center, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    TextField(
        value = query, onValueChange = onQueryChange, modifier = modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(16.dp)),
        placeholder = { Text("Search by name...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent, 
            unfocusedIndicatorColor = Color.Transparent, 
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = true
    )
}

@Composable
fun AddCustomerDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register New Customer", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            }
        },
        confirmButton = { 
            Button(onClick = { if (name.isNotBlank()) onConfirm(name, phone) }, shape = RoundedCornerShape(8.dp)) { 
                Text("Add to Ledger") 
            } 
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
