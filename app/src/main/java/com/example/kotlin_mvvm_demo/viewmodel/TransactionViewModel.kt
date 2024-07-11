package com.example.kotlin_mvvm_demo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.kotlin_mvvm_demo.model.Transaction
import com.example.kotlin_mvvm_demo.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 *
 * @author wenzhiming
 * @date 2024/07/10
 *
 */

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    private val transactions: StateFlow<List<Transaction>> = _transactions

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    fun getPageData():Flow<PagingData<Transaction>>{
        return repository.getPagedTransactions(transactions.value).cachedIn(viewModelScope)
    }

    fun loadTransactions(apiKey: String) {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.getTransactions(apiKey)
                .collect { transactions ->
                    _transactions.value = transactions
                }
            _isRefreshing.value = false
        }
    }

    fun refreshTransactions(apiKey: String) {
        loadTransactions(apiKey)
    }
}

class TransactionViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}