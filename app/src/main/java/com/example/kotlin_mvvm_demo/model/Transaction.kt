package com.example.kotlin_mvvm_demo.model

/**
 *
 * @author wenzhiming
 * @date 2024/07/10
 *
 */
data class Transaction(
    val description: String,
    val transactionDate: String,
    val category: String,
    val debit: Double?,
    val credit: Double?,
    val id: Int
)
