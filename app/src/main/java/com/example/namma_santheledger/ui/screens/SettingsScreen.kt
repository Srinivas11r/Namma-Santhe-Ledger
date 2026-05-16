package com.example.namma_santheledger.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
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
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
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
                        Text("Namma-Santhe Vendor", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        Text("Market Ledger Profile", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
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

            SettingsItem(icon = Icons.Default.Person, title = "Account Details", subtitle = "Your name and phone number")
            SettingsItem(icon = Icons.Default.Share, title = "Share with Friends", subtitle = "Invite other vendors to use Digital Khata")
            SettingsItem(icon = Icons.Default.Info, title = "About App", subtitle = "Version 1.0.0 (Top-Tier Build)")
            
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
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth(),
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
