package mapmates.feature.event.api.interactor

import mapmates.feature.event.api.GetEventsResult
import mapmates.feature.event.api.filters.Filter

interface GetAllEventsPreviewInteractor {
    suspend operator fun invoke(filters: Set<Filter>): GetEventsResult
}