package com.bekircaglar.chatappbordo.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bekircaglar.chatappbordo.domain.model.Message
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await

private const val PAGE_SIZE = 15

class MessagePagingSource(
    private val messagesRef: DatabaseReference,
) : PagingSource<Int, Message>() {
    override fun getRefreshKey(state: PagingState<Int, Message>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> {
        return try {
            val currentPage = params.key ?: 0 // If key is null, start from the first page

            val snapshot = messagesRef.get().await()
            val messages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }.sortedBy { it.timestamp }.reversed()

            val dataMessages = when(currentPage){
                0 -> messages.subList(0, PAGE_SIZE)
                else -> {
                    val sorgu = messages.size-(currentPage* PAGE_SIZE)
                if (sorgu < PAGE_SIZE) messages.subList(currentPage * PAGE_SIZE, messages.lastIndex+1)
                else messages.subList(currentPage * PAGE_SIZE, (currentPage + 1) * PAGE_SIZE)
                }

            }
            LoadResult.Page(
                data = dataMessages,
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = if (messages.size < PAGE_SIZE) null else currentPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}