package com.example.kotlin_mvvm_demo.network

import com.example.kotlin_mvvm_demo.model.Transaction
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

/**
 *
 * @author wenzhiming
 * @date 2024/07/10
 *
 */
interface ApiService {
    @GET("bank/transactions")
    suspend fun getTransactions(@Header("x-api-key") apiKey: String): Response<List<Transaction>>

}

