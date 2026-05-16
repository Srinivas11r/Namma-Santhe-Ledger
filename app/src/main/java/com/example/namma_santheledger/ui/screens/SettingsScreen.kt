package com.example.namma_santheledger.ui.screens

import android.content.Context
import android.content.Intent
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var showAboutDialog by remember { mutableStateOf(false) }
    var shopName by remember { mutableStateOf("Namma-Santhe Vendor") }
    var vendorPhone by remember { mutableStateOf("Not Set") }
    var showEditShopDialog by remember { mutableStateOf(false) }
    var showAccountDetailsDialog by remember { mutableStateOf(false) }

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
                .background(Color(0xFFF1F3F4))
        ) {
            // Profile Section
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clickable { showEditShopDialog = true },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(60.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Store, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(shopName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        Text("Phone: $vendorPhone", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Text(
                "GENERAL SETTINGS",
                modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            SettingsItem(
                icon = Icons.Default.Person, 
                title = "Account Details", 
                subtitle = "Manage your shop and phone",
                onClick = { showAccountDetailsDialog = true }
            )
            
            SettingsItem(
                icon = Icons.Default.Share, 
                title = "Share with Friends", 
                subtitle = "Invite other vendors to use Digital Khata",
                onClick = { shareApp(context) }
            )
            
            SettingsItem(
                icon = Icons.Default.Info, 
                title = "About App", 
                subtitle = "Version 1.0.0 (Top-Tier Build)",
                onClick = { showAboutDialog = true }
            )
            
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
            text = {
                Text("This app is a professional Digital Khata solution designed to help small vendors track their daily Udari and cash payments with ease.\n\nDeveloped for the Digital India Internship program.")
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text("Close") }
            }
        )
    }

    if (showAccountDetailsDialog || showEditShopDialog) {
        var tempName by remember { mutableStateOf(shopName) }
        var tempPhone by remember { mutableStateOf(if (vendorPhone == "Not Set") "" else vendorPhone) }
        
        AlertDialog(
            onDismissRequest = { 
                showAccountDetailsDialog = false
                showEditShopDialog = false 
            },
            title = { Text("Update Shop Details") },
            text = {
                Column {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Shop/Vendor Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = tempPhone,
                        onValueChange = { tempPhone = it },
                        label = { Text("Your Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                        )
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (tempName.isNotBlank()) {
                        shopName = tempName
                        vendorPhone = if (tempPhone.isBlank()) "Not Set" else tempPhone
                        showAccountDetailsDialog = false
                        showEditShopDialog = false
                    }
                }, shape = RoundedCornerShape(8.dp)) { Text("Save Changes") }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAccountDetailsDialog = false
                    showEditShopDialog = false 
                }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        ListItem(
            headlineContent = { Text(title, fontWeight = FontWeight.Bold) },
            supportingContent = { Text(subtitle) },
            leadingContent = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
        )
    }
}

private fun shareApp(context: Context) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Manage your Udari professionally with Namma-Santhe Ledger! Download now.")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}
