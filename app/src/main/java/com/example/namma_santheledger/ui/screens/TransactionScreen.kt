package com.example.namma_santheledger.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.namma_santheledger.data.entity.TransactionType
import com.example.namma_santheledger.ui.viewmodel.LedgerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    customerId: Long,
    viewModel: LedgerViewModel,
    onTransactionAdded: () -> Unit,
    onBack: () -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Entry") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Enter Amount", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "₹ ${amountText.ifEmpty { "0" }}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("What did they buy? (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            NumericKeypad(
                onNumberClick = { num ->
                    if (num == "." && amountText.contains(".")) return@NumericKeypad
                    if (amountText.length < 9) amountText += num
                },
                onDeleteClick = { if (amountText.isNotEmpty()) amountText = amountText.dropLast(1) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // CLEAR ACTION BUTTONS
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            viewModel.addTransaction(customerId, amount, TransactionType.CREDIT, note)
                            onTransactionAdded()
                        }
                    },
                    modifier = Modifier.weight(1f).height(72.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(16.dp),
                    enabled = amountText.isNotEmpty()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("GAVE GOODS", fontWeight = FontWeight.Black)
                        Text("(Udari/Credit)", fontSize = 12.sp)
                    }
                }

                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            viewModel.addTransaction(customerId, amount, TransactionType.PAYMENT, note)
                            onTransactionAdded()
                        }
                    },
                    modifier = Modifier.weight(1f).height(72.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                    shape = RoundedCornerShape(16.dp),
                    enabled = amountText.isNotEmpty()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("GOT CASH", fontWeight = FontWeight.Black)
                        Text("(Payment)", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun NumericKeypad(onNumberClick: (String) -> Unit, onDeleteClick: () -> Unit) {
    val keys = listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"), listOf(".", "0", "⌫"))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        keys.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { key ->
                    Surface(
                        modifier = Modifier.weight(1f).aspectRatio(1.8f).clip(RoundedCornerShape(16.dp)).clickable { if (key == "⌫") onDeleteClick() else onNumberClick(key) },
                        color = if (key == "⌫") MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (key == "⌫") Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = null)
                            else Text(text = key, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
