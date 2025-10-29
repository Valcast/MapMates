package mapmates.feature.event.impl.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mapmates.feature.event.api.interactor.GetAllEventsPreviewInteractor
import mapmates.feature.event.impl.data.EventRepository
import mapmates.feature.event.impl.interactor.GetAllEventsPreviewInteractorImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object EventModule {

    @Provides
    @Singleton
    fun provideGetAllEventsPreviewInteractor(
        eventRepository: EventRepository
    ): GetAllEventsPreviewInteractor {
        return GetAllEventsPreviewInteractorImpl(eventRepository)
    }
}