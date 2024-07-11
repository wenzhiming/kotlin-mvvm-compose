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

        setContent {
            val apiKey = "PMAK-668cefa85d9ee800012eef9d-7d7956c21099fa61f71001096a29b28fe7"
            transactionViewModel.loadTransactions(apiKey)
            TransactionListScreen(transactionViewModel, apiKey)
        }
    }
}