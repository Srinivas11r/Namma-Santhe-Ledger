package com.example.namma_santheledger.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.namma_santheledger.data.dao.LedgerDao
import com.example.namma_santheledger.data.entity.Customer
import com.example.namma_santheledger.data.entity.LedgerTransaction

@Database(entities = [Customer::class, LedgerTransaction::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ledgerDao(): LedgerDao
}
