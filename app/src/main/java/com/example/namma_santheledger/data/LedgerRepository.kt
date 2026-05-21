package com.example.namma_santheledger.data

import com.example.namma_santheledger.data.dao.LedgerDao
import com.example.namma_santheledger.data.entity.Customer
import com.example.namma_santheledger.data.entity.LedgerTransaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LedgerRepository @Inject constructor(
    private val ledgerDao: LedgerDao
) {
    fun getAllCustomers() = ledgerDao.getAllCustomers()

    fun searchCustomers(query: String) = ledgerDao.searchCustomers(query)

    suspend fun addCustomer(customer: Customer) = ledgerDao.insertCustomer(customer)

    suspend fun updateCustomer(customer: Customer) = ledgerDao.updateCustomer(customer)

    suspend fun deleteCustomer(customer: Customer) = ledgerDao.deleteCustomer(customer)

    fun getTransactionsForCustomer(customerId: Long) = ledgerDao.getTransactionsForCustomer(customerId)

    fun getAllTransactionsAfter(startTime: Long) = ledgerDao.getAllTransactionsAfter(startTime)

    fun getAllTransactions() = ledgerDao.getAllTransactions()

    suspend fun addTransaction(transaction: LedgerTransaction) = ledgerDao.addTransactionAndUpdateCustomer(transaction)

    fun getDailyCredit(startOfDay: Long) = ledgerDao.getDailyCredit(startOfDay)

    fun getDailyPayment(startOfDay: Long) = ledgerDao.getDailyPayment(startOfDay)

    fun getTotalOutstanding() = ledgerDao.getTotalOutstanding()
}
