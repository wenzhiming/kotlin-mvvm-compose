package com.example.kotlin_mvvm_demo.ui


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.kotlin_mvvm_demo.model.Transaction
import com.example.kotlin_mvvm_demo.viewmodel.TransactionViewModel


/**
 *
 * @author wenzhiming
 * @date 2024/07/11
 *
 */

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TransactionListScreen(viewModel: TransactionViewModel) {
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pagedTransactions = viewModel.getPageData().collectAsLazyPagingItems()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refreshTransactions() }
    )

    Box(
        Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)) {
        LazyColumn {
            items(pagedTransactions.itemCount) { index ->
                pagedTransactions[index]?.let { TransactionItem(it) }
            }
            // Handle loading state at the end of the list
            pagedTransactions.apply {
                when {
                    loadState.append is LoadState.Loading -> {
                        item {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator(modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.Center))
                            }
                        }
                    }
                    loadState.append is LoadState.Error -> {
                        item {
                            Text(
                                text = "Error loading more items",
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
//                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(4.dp),
        elevation = 4.dp
    ) {
        Surface(
            modifier = Modifier.padding(10.dp,0.dp)
        ) {
            Column {
                Text(text = "Date: ${transaction.transactionDate}", style = MaterialTheme.typography.h6, color = Color.Blue)
//                Spacer(Modifier.height(8.dp))
                Text(text = "Description: ${transaction.description}", style = MaterialTheme.typography.body1, color = Color.Black)
//                Spacer(Modifier.height(8.dp))
                Text(text = "Category: ${transaction.category}", style = MaterialTheme.typography.body1, color = Color.Magenta)
//                Spacer(Modifier.height(8.dp))
                Row {
                    Text(text = "Debit: ", style = MaterialTheme.typography.body1, color = Color.Black)
                    Text(text = "${transaction.debit ?: "N/A"}", style = MaterialTheme.typography.body2, color = Color.Red)
                }
//                Spacer(Modifier.height(8.dp))
                Row {
                    Text(text = "Credit: ", style = MaterialTheme.typography.body1, color = Color.Black)
                    Text(text = "${transaction.credit ?: "N/A"}", style = MaterialTheme.typography.body2, color = Color.Green)
                }
            }
        }
    }
}
