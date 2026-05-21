package com.example.namma_santheledger.data.dao

import androidx.room.*
import com.example.namma_santheledger.data.entity.Customer
import com.example.namma_santheledger.data.entity.LedgerTransaction
import com.example.namma_santheledger.data.entity.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface LedgerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchCustomers(query: String): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :customerId")
    suspend fun getCustomerById(customerId: Long): Customer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getTransactionsForCustomer(customerId: Long): Flow<List<LedgerTransaction>>

    @Query("SELECT * FROM transactions WHERE timestamp >= :startTime ORDER BY timestamp DESC")
    fun getAllTransactionsAfter(startTime: Long): Flow<List<LedgerTransaction>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<LedgerTransaction>>

    @Insert
    suspend fun insertTransaction(transaction: LedgerTransaction): Long

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'CREDIT' AND timestamp >= :startOfDay")
    fun getDailyCredit(startOfDay: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'PAYMENT' AND timestamp >= :startOfDay")
    fun getDailyPayment(startOfDay: Long): Flow<Double?>

    @Query("SELECT SUM(totalOutstanding) FROM customers")
    fun getTotalOutstanding(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM customers")
    fun getCustomerCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM transactions")
    fun getTransactionCount(): Flow<Int>

    @Transaction
    suspend fun addTransactionAndUpdateCustomer(transaction: LedgerTransaction) {
        insertTransaction(transaction)
        val customer = getCustomerById(transaction.customerId) ?: return
        val newBalance = if (transaction.type == TransactionType.CREDIT) {
            customer.totalOutstanding + transaction.amount
        } else {
            customer.totalOutstanding - transaction.amount
        }
        updateCustomer(customer.copy(totalOutstanding = newBalance))
    }
}
