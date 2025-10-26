package com.valcast.mapmates.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.valcast.mapmates.data.utils.toMessage
import com.valcast.mapmates.domain.model.Message
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class MessagesPagingSource(private val db: FirebaseFirestore, private val chatRoomId: String) :
    PagingSource<Long, Message>() {
    override fun getRefreshKey(state: PagingState<Long, Message>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.createdAt?.toInstant(TimeZone.currentSystemDefault())
                ?.toEpochMilliseconds()
        }
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Message> {
        return try {
            val pageSize = params.loadSize
            val startAfterInstantSeconds = params.key

            val query = db.collection("chatRooms").document(chatRoomId).collection("messages")
                .whereLessThan("createdAt", Timestamp.now())
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())

            if (startAfterInstantSeconds != null) {
                query.startAfter(arrayOf(Timestamp(startAfterInstantSeconds, 0)))
            }

            val messages = query.get(Source.CACHE).await().documents.map {
                it.toMessage()
            }

            val prevKey = if (startAfterInstantSeconds != null) {
                messages.firstOrNull()?.createdAt?.toInstant(TimeZone.currentSystemDefault())
                    ?.toEpochMilliseconds()
            } else {
                null
            }

            val nextKey = if (messages.size == pageSize && messages.isNotEmpty()) {
                messages.last().createdAt.toInstant(TimeZone.currentSystemDefault())
                    .toEpochMilliseconds()
            } else {
                null
            }

            LoadResult.Page(
                data = messages,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}