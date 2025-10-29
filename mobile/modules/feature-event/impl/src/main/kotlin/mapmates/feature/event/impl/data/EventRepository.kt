package mapmates.feature.event.impl.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import mapmates.core.firebase.getList
import mapmates.core.firebase.getRequiredString
import mapmates.feature.event.api.EventError
import mapmates.feature.event.api.GetEventsResult
import mapmates.feature.event.api.UserPreview
import mapmates.feature.event.api.filters.DateRange
import mapmates.feature.event.api.filters.Filter
import mapmates.feature.event.api.filters.SortOrder
import mapmates.feature.event.api.filters.get
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant

@OptIn(ExperimentalTime::class)
internal class EventRepository @Inject constructor(
    private val database: FirebaseFirestore
) {

    suspend fun getAllEventsPreview(filters: Set<Filter>) = withContext(Dispatchers.IO) {
        try {
            val query = createQueryWithFilters(filters)

            val events = query.get().await().documents.mapNotNull { document ->
                    val author =
                        getUsersPreviews(listOf(document.getRequiredString("author"))).first()
                    val participants = getUsersPreviews(document.getList("participants"))

                    document.toEvent(author, participants)
                }

            GetEventsResult.Success(events)
        } catch (e: Exception) {
            GetEventsResult.Failure(EventError.Unknown)
        }
    }

    private suspend fun getUsersPreviews(userIds: List<String>): List<UserPreview> {
        return userIds.map { userId ->
            val userDocument =
                database.collection(USERS_DATABASE_PATH).document(userId).get().await()

            userDocument.toUserPreview()
        }
    }

    private fun createQueryWithFilters(filters: Set<Filter>): Query {
        var q: Query = database.collection(EVENTS_DATABASE_PATH)

        filters.get<Filter.ByCategory>()?.let { filter ->
            q = q.whereEqualTo("category", filter.category.name)
        }

        filters.get<Filter.BySortOrder>()?.let { filter ->
            when (filter.sortOrder) {
                SortOrder.NEXT_DATE -> {
                    q = q.orderBy("startTime", Query.Direction.ASCENDING)
                }

                SortOrder.DISTANCE -> {}
                SortOrder.POPULARITY -> {
                    q = q.orderBy("participantsCount", Query.Direction.DESCENDING)
                }
            }
        }

        filters.get<Filter.ByDateRange>()?.let { filter ->
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val timeZone = TimeZone.currentSystemDefault()

            when (val dateRange = filter.dateRange) {
                is DateRange.Today -> {
                    val start = now.date.atStartOfDayIn(timeZone)
                    val end = start.plus(1, DateTimeUnit.DAY, timeZone)

                    q = q.whereGreaterThanOrEqualTo("startTime", start.toTimestamp())
                        .whereLessThan("startTime", end.toTimestamp())
                }

                is DateRange.Tomorrow -> {
                    val start = now.date.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone)
                    val end = start.plus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault())

                    q = q.whereGreaterThanOrEqualTo("startTime", start.toTimestamp())
                        .whereLessThan("startTime", end.toTimestamp())
                }

                is DateRange.ThisWeek -> {
                    val start = now.date.atStartOfDayIn(timeZone)
                    val daysUntilNextMonday = 7 - now.date.dayOfWeek.ordinal
                    val end = start.plus(daysUntilNextMonday.toLong(), DateTimeUnit.DAY, timeZone)

                    q = q.whereGreaterThanOrEqualTo("startTime", start.toTimestamp())
                        .whereLessThan("startTime", end.toTimestamp())
                }

                is DateRange.Custom -> {
                    val start = dateRange.startTime.toInstant(TimeZone.currentSystemDefault())
                    val end = dateRange.endTime.toInstant(TimeZone.currentSystemDefault())

                    q = q.whereGreaterThanOrEqualTo("startTime", start.toTimestamp())
                        .whereLessThan("startTime", end.toTimestamp())
                }
            }
        }

        return q
    }

    private fun Instant.toTimestamp() = Timestamp(this.toJavaInstant())

    companion object {
        private const val EVENTS_DATABASE_PATH = "events"
        private const val USERS_DATABASE_PATH = "users"
    }
}