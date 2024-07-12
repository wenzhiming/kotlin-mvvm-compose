package com.example.kotlin_mvvm_demo.network

/**
 *
 * @author wenzhiming
 * @date 2024/07/12
 *
 */
sealed class ResultData<out T> {
    data class Success<out T>(val data: T) : ResultData<T>()
    data class Error(val exception: Exception) : ResultData<Nothing>()
    data object Loading : ResultData<Nothing>()
}