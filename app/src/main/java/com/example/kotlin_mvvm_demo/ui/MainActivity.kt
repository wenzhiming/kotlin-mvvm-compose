package com.example.kotlin_mvvm_demo.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.kotlin_mvvm_demo.network.ApiServiceFactory
import com.example.kotlin_mvvm_demo.repository.TransactionRepository
import com.example.kotlin_mvvm_demo.viewmodel.TransactionViewModel
import com.example.kotlin_mvvm_demo.viewmodel.TransactionViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = ApiServiceFactory.apiService
        val repository = TransactionRepository(apiService)
        val viewModelFactory = TransactionViewModelFactory(repository)
        transactionViewModel = ViewModelProvider(this, viewModelFactory).get(TransactionViewModel::class.java)
        transactionViewModel.loadTransactions()

        setContent {
            TransactionListScreen(transactionViewModel)
        }

        // 观察错误消息状态
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                transactionViewModel.error.collect { errorMessage ->
                    // 在这里处理错误消息
                    if (errorMessage != null) {
                        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}