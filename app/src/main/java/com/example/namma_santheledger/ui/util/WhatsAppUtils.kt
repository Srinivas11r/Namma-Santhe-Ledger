package com.example.namma_santheledger.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri

fun sendWhatsAppReminder(context: Context, name: String, phoneNumber: String, amount: Double) {
    if (phoneNumber.isBlank()) return
    
    val message = if (amount > 0) {
        "Hello $name, this is a reminder from Namma-Santhe Ledger. Your pending due is ₹${"%,.0f".format(amount)}. Please clear it soon. Thank you!"
    } else {
        "Hello $name, thank you for using Namma-Santhe Ledger. Your account is currently settled. Have a great day!"
    }
    
    val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle case where WhatsApp is not installed
    }
}
