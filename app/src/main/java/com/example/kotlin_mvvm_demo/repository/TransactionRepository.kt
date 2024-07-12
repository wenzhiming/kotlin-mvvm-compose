package com.example.kotlin_mvvm_demo.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.kotlin_mvvm_demo.model.Transaction
import com.example.kotlin_mvvm_demo.network.ApiService
import com.example.kotlin_mvvm_demo.network.HttpException
import com.example.kotlin_mvvm_demo.network.ResultData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.min

/**
 *
 * @author wenzhiming
 * @date 2024/07/10
 *
 */

class TransactionRepository(private val apiService: ApiService) {

    private val apiKey = "PMAK-668cefa85d9ee800012eef9d-7d7956c21099fa61f71001096a29b28fe7"

    fun getTransactions(): Flow<ResultData<List<Transaction>>> = flow {
        emit(ResultData.Loading) // 发出加载状态
        try {
            val response = apiService.getTransactions(apiKey)
            if (response.isSuccessful) {
                emit(ResultData.Success(response.body() ?: emptyList())) // 发出成功的结果
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                throw HttpException(errorBody, response.code(), errorBody)
            }
        } catch (e: HttpException) {
            emit(ResultData.Error(e)) // 发出包含HttpException的错误结果
        } catch (e: Throwable) {
            emit(ResultData.Error(HttpException("Network or unknown error", -1, ""))) // 发出一般错误
        }
    }

    fun getPagedTransactions(data: List<Transaction>): Flow<PagingData<Transaction>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = { ListPagingSource(data) }
        ).flow
    }
}

class ListPagingSource(
    private val data: List<Transaction>
) : PagingSource<Int, Transaction>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transaction> {
        val currentPage = params.key ?: 1
        val pageSize = 5
        val response = data
        val responseSize = response.size
        val endIndex = currentPage * pageSize

        return try {
            val nextPage = if (endIndex < responseSize) currentPage + 1 else null
            if (currentPage > 1) {
                delay(1500)
            }
            LoadResult.Page(
                data = response.subList((currentPage - 1) * pageSize, min(endIndex, responseSize)),
                prevKey = if (currentPage > 1) currentPage - 1 else null,
                nextKey = nextPage,
            )
        } catch (e: Exception) {
            LoadResult.Error(throwable = e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Transaction>): Int? {
        return null
    }
}

