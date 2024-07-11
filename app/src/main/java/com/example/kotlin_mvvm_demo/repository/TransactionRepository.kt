package com.example.kotlin_mvvm_demo.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.kotlin_mvvm_demo.model.Transaction
import com.example.kotlin_mvvm_demo.network.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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

    fun getTransactions(): Flow<List<Transaction>> = flow {
        emit(apiService.getTransactions(apiKey))
    }.catch {
        emit(emptyList())
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

