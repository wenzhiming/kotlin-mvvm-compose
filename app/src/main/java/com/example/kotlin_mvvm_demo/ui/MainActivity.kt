package com.example.kotlin_mvvm_demo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.kotlin_mvvm_demo.network.ApiServiceFactory
import com.example.kotlin_mvvm_demo.repository.TransactionRepository
import com.example.kotlin_mvvm_demo.viewmodel.TransactionViewModel
import com.example.kotlin_mvvm_demo.viewmodel.TransactionViewModelFactory

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
    }
}