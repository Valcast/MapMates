package com.example.socialmeetingapp.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.socialmeetingapp.data.utils.getLocalDateTime
import com.example.socialmeetingapp.data.utils.toUserPreview
import com.example.socialmeetingapp.domain.model.Relationship
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class FollowersPagingSource(private val db: FirebaseFirestore, private val userId: String) :
    PagingSource<Long, Relationship>() {
    override fun getRefreshKey(state: PagingState<Long, Relationship>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.followedAt
                ?.toInstant(TimeZone.currentSystemDefault())
                ?.toEpochMilliseconds()
        }
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Relationship> {
        return try {
            val pageSize = params.loadSize
            val startAfterInstantSeconds = params.key

            val query = db.collection("users").document(userId).collection("followers")
                .orderBy("followedAt", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())

            if (startAfterInstantSeconds != null) {
                query.startAfter(arrayOf(Timestamp(startAfterInstantSeconds, 0)))
            }

            val followers = query.get().await().documents

            val userPreviewsIds = followers.map { it.id }


            val userPreviewsMap = db.collection("users")
                .whereIn(FieldPath.documentId(), userPreviewsIds)
                .get().await().documents
                .associateBy { it.id }

            val relationships = followers.mapNotNull { follower ->
                userPreviewsMap[follower.id]?.let { userDocument ->
                    Relationship(
                        userDocument.toUserPreview(),
                        follower.getLocalDateTime("followedAt")
                    )
                }
            }


            val prevKey = if (startAfterInstantSeconds != null) {
                relationships.firstOrNull()?.followedAt?.toInstant(TimeZone.currentSystemDefault())
                    ?.toEpochMilliseconds()
            } else {
                null
            }

            val nextKey = if (relationships.size == pageSize && relationships.isNotEmpty()) {
                relationships.last().followedAt.toInstant(TimeZone.currentSystemDefault())
                    .toEpochMilliseconds()
            } else {
                null
            }

            LoadResult.Page(
                data = relationships,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}