package com.valcast.mapmates.di

import android.content.ContentResolver
import android.content.Context
import android.net.ConnectivityManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.valcast.mapmates.data.api.GeocodingApi
import com.valcast.mapmates.data.repository.ChatRepositoryImpl
import com.valcast.mapmates.data.repository.FirebaseEventRepositoryImpl
import com.valcast.mapmates.data.repository.FirebaseUserRepositoryImpl
import com.valcast.mapmates.data.repository.LocationRepositoryImpl
import com.valcast.mapmates.data.repository.NotificationRepositoryImpl
import com.valcast.mapmates.data.repository.SettingsRepositoryImpl
import com.valcast.mapmates.domain.repository.ChatRepository
import com.valcast.mapmates.domain.repository.EventRepository
import com.valcast.mapmates.domain.repository.LocationRepository
import com.valcast.mapmates.domain.repository.NotificationRepository
import com.valcast.mapmates.domain.repository.SettingsRepository
import com.valcast.mapmates.domain.repository.UserRepository
import com.valcast.mapmates.presentation.common.CredentialManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val USER_PREFERENCES_NAME = "user_preferences"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return Firebase.storage
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    @Singleton
    fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager {
        return CredentialManager(context)
    }

    @Provides
    @Singleton
    fun provideFusedLocationClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver =
        context.contentResolver

    @Provides
    @Singleton
    fun provideLocationRepository(
        fusedLocationProviderClient: FusedLocationProviderClient,
        geocodingApi: GeocodingApi,
        @ApplicationContext context: Context
    ): LocationRepository {
        return LocationRepositoryImpl(fusedLocationProviderClient, context, geocodingApi)
    }

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun provideEventRepository(
        firestoreDatabase: FirebaseFirestore,
        userRepository: UserRepository,
        firebaseAuth: FirebaseAuth
    ): EventRepository {
        return FirebaseEventRepositoryImpl(firestoreDatabase, userRepository, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(USER_PREFERENCES_NAME)
        }
    }


    @Provides
    @Singleton
    fun provideUserRepository(
        firebaseAuth: FirebaseAuth,
        firestoreDatabase: FirebaseFirestore,
        firebaseStorage: FirebaseStorage,
    ): UserRepository {
        return FirebaseUserRepositoryImpl(
            firebaseAuth,
            firestoreDatabase,
            firebaseStorage,
        )
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        db: FirebaseFirestore,
        auth: FirebaseAuth
    ): NotificationRepository {
        return NotificationRepositoryImpl(db, auth)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        dataStore: DataStore<Preferences>
    ): SettingsRepository {
        return SettingsRepositoryImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideGeocodingApi(): GeocodingApi {
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        db: FirebaseFirestore,
        userRepository: UserRepository

    ): ChatRepository {
        return ChatRepositoryImpl(db, userRepository)
    }

}



