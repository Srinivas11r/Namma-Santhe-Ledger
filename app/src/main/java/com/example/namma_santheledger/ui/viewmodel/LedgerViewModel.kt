package com.example.namma_santheledger.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.namma_santheledger.data.LedgerRepository
import com.example.namma_santheledger.data.entity.Customer
import com.example.namma_santheledger.data.entity.LedgerTransaction
import com.example.namma_santheledger.data.entity.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LedgerViewModel @Inject constructor(
    private val repository: LedgerRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val customers = searchQuery
        .debounce(300L)
        .flatMapLatest { query ->
            if (query.isEmpty()) repository.getAllCustomers()
            else repository.searchCustomers(query)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val totalOutstanding = repository.getTotalOutstanding()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    private val _startOfDay = MutableStateFlow(getStartOfDayTimestamp())
    
    val dailyCredit = _startOfDay.flatMapLatest { repository.getDailyCredit(it) }
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val dailyPayment = _startOfDay.flatMapLatest { repository.getDailyPayment(it) }
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val todayTransactions = _startOfDay.flatMapLatest { repository.getAllTransactionsAfter(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun getTransactionsForCustomer(customerId: Long) = repository.getTransactionsForCustomer(customerId)

    fun addCustomer(name: String, phoneNumber: String) {
        viewModelScope.launch {
            repository.addCustomer(Customer(name = name, phoneNumber = phoneNumber))
        }
    }

    fun addTransaction(customerId: Long, amount: Double, type: TransactionType, note: String = "") {
        viewModelScope.launch {
            repository.addTransaction(
                LedgerTransaction(
                    customerId = customerId,
                    amount = amount,
                    type = type,
                    note = note
                )
            )
        }
    }

    private fun getStartOfDayTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
