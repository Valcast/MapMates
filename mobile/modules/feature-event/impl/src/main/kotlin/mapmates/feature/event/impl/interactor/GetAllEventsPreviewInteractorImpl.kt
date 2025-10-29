package mapmates.feature.event.impl.interactor

import mapmates.feature.event.api.filters.Filter
import mapmates.feature.event.api.interactor.GetAllEventsPreviewInteractor
import mapmates.feature.event.impl.data.EventRepository
import javax.inject.Inject

internal class GetAllEventsPreviewInteractorImpl @Inject constructor(
    private val eventRepository: EventRepository
) : GetAllEventsPreviewInteractor {
    override suspend fun invoke(filters: Set<Filter>) = eventRepository.getAllEventsPreview(filters)
}