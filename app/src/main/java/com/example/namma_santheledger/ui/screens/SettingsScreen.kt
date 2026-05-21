package com.example.namma_santheledger.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.namma_santheledger.ui.viewmodel.LedgerViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: LedgerViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var showAboutDialog by remember { mutableStateOf(false) }
    var shopName by remember { mutableStateOf("Namma-Santhe Vendor") }
    var vendorPhone by remember { mutableStateOf("Not Set") }
    var showAccountDetailsDialog by remember { mutableStateOf(false) }
    
    // Developer Stats
    val totalCust by viewModel.customerCount.collectAsState()
    val totalTrans by viewModel.transactionCount.collectAsState()
    val totalBalance by viewModel.totalOutstanding.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("App Settings", fontWeight = FontWeight.Black) },
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
            // Profile Section
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clickable { showAccountDetailsDialog = true },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(60.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Store, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(shopName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                        Text("Phone: $vendorPhone", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Text(
                "GENERAL SETTINGS",
                modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            SettingsItem(icon = Icons.Default.Person, title = "Account Details", subtitle = "Manage your shop and phone") { 
                showAccountDetailsDialog = true 
            }
            
            SettingsItem(icon = Icons.Default.Share, title = "Share with Friends", subtitle = "Invite other vendors") { 
                shareApp(context) 
            }

            SettingsItem(
                icon = Icons.Default.SystemUpdate,
                title = "Check for Updates",
                subtitle = "App Version v1.1.0 (Latest)",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Srinivas11r/Namma-Santhe-Ledger/releases/latest"))
                    context.startActivity(intent)
                }
            )
            
            SettingsItem(icon = Icons.Default.Info, title = "About App", subtitle = "Digital Khata v1.1.0") { 
                showAboutDialog = true 
            }

            // PROFESSIONAL DEVELOPER SECTION
            Text(
                "DEVELOPER & ADMIN OVERVIEW",
                modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Developer Info", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Developed By: Srinivasulu R", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text("Build Type: Internship Final Project (Production)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Storage, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("System Diagnostics", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    DiagnosticRow("Total Registered Customers", totalCust.toString())
                    DiagnosticRow("Total Ledger Transactions", totalTrans.toString())
                    DiagnosticRow("App System Balance", "₹${"%,.0f".format(totalBalance)}")
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                "Built for Digital India Internship",
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = Color.LightGray,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Namma-Santhe Ledger") },
            text = { Text("Professional Digital Khata designed for micro-entrepreneurs.\n\nBuilt using MVVM, Hilt, Room, and Jetpack Compose.") },
            confirmButton = { TextButton(onClick = { showAboutDialog = false }) { Text("Close") } }
        )
    }

    if (showAccountDetailsDialog) {
        var tempName by remember { mutableStateOf(shopName) }
        var tempPhone by remember { mutableStateOf(if (vendorPhone == "Not Set") "" else vendorPhone) }
        AlertDialog(
            onDismissRequest = { showAccountDetailsDialog = false },
            title = { Text("Update Profile") },
            text = {
                Column {
                    OutlinedTextField(value = tempName, onValueChange = { tempName = it }, label = { Text("Shop Name") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = tempPhone, onValueChange = { tempPhone = it }, label = { Text("Phone Number") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                }
            },
            confirmButton = {
                Button(onClick = {
                    shopName = tempName
                    vendorPhone = if (tempPhone.isBlank()) "Not Set" else tempPhone
                    showAccountDetailsDialog = false
                }, shape = RoundedCornerShape(8.dp)) { Text("Save") }
            }
        )
    }
}

@Composable
fun DiagnosticRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        ListItem(
            headlineContent = { Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
            supportingContent = { Text(subtitle, color = Color.Gray) },
            leadingContent = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

private fun shareApp(context: Context) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Manage your Udari professionally with Namma-Santhe Ledger! Built for Digital India.")
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, null))
}
