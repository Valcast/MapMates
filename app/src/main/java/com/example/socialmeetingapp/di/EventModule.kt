package com.example.socialmeetingapp.di

import com.example.socialmeetingapp.data.repository.FirebaseEventRepositoryImpl
import com.example.socialmeetingapp.domain.event.repository.EventRepository
import com.example.socialmeetingapp.domain.event.usecase.CreateEventUseCase
import com.example.socialmeetingapp.domain.event.usecase.DeleteEventUseCase
import com.example.socialmeetingapp.domain.event.usecase.GetAllEventsUseCase
import com.example.socialmeetingapp.domain.event.usecase.GetEventUseCase
import com.example.socialmeetingapp.domain.event.usecase.JoinEventUseCase
import com.example.socialmeetingapp.domain.event.usecase.LeaveEventUseCase
import com.example.socialmeetingapp.domain.event.usecase.UpdateEventUseCase
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import com.example.socialmeetingapp.domain.user.usecase.GetCurrentUserUseCase
import com.example.socialmeetingapp.domain.user.usecase.GetUserByIDUseCase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object EventModule {
    @Provides
    @Singleton
    fun provideEventRepository(firestoreDatabase: FirebaseFirestore, getCurrentUserUseCase: GetCurrentUserUseCase, getUserByIDUseCase: GetUserByIDUseCase): EventRepository {
        return FirebaseEventRepositoryImpl(firestoreDatabase, getCurrentUserUseCase, getUserByIDUseCase )
    }


}