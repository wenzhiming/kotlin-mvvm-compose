package com.example.kotlin_mvvm_demo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.kotlin_mvvm_demo.model.Transaction
import com.example.kotlin_mvvm_demo.network.ResultData
import com.example.kotlin_mvvm_demo.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    // 使用 StateFlow 来存储错误消息，初始值为 null 表示没有错误
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun getPageData():Flow<PagingData<Transaction>>{
        return repository.getPagedTransactions(transactions.value).cachedIn(viewModelScope)
    }

    // 加载交易数据的方法
    fun loadTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.value = true
            _error.value = null // 在开始加载前清除之前的错误消息
            try {
                // repository.getTransactions() 返回 Flow<ResultData<List<Transaction>>>
                repository.getTransactions().collect { result ->
                    withContext(Dispatchers.Main) {
                        _isRefreshing.value = false
                        when (result) {
                            is ResultData.Loading -> {
                                // 处理加载状态，例如更新 isLoading 状态
                            }
                            is ResultData.Success -> {
                                // 处理成功状态，更新 transactions 状态
                                _transactions.value = result.data
                            }
                            is ResultData.Error -> {
                                // 捕获到错误时，更新 error 状态
                                _error.value = result.exception.message
                                Log.e("wzm", "error - " + result.exception.message)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // 处理可能发生的异常
                withContext(Dispatchers.Main) {
                    _error.value = e.message
                }
                Log.e("wzm", "error - " + e.message)
            }
        }
    }


    fun refreshTransactions() {
//        _transactions.value = emptyList()
        loadTransactions()
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