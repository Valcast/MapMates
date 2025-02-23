package com.example.socialmeetingapp.di

import android.content.ContentResolver
import android.content.Context
import android.net.ConnectivityManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.socialmeetingapp.data.api.GeocodingApi
import com.example.socialmeetingapp.data.remote.NotificationService
import com.example.socialmeetingapp.data.repository.FirebaseEventRepositoryImpl
import com.example.socialmeetingapp.data.repository.FirebaseUserRepositoryImpl
import com.example.socialmeetingapp.data.repository.LocationRepositoryImpl
import com.example.socialmeetingapp.data.repository.SettingsRepositoryImpl
import com.example.socialmeetingapp.domain.repository.EventRepository
import com.example.socialmeetingapp.domain.repository.LocationRepository
import com.example.socialmeetingapp.domain.repository.SettingsRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.presentation.common.CredentialManager
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
    fun provideNotificationService(firestoreDatabase: FirebaseFirestore): NotificationService {
        return NotificationService(firestoreDatabase)
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
        firebaseAuth: FirebaseAuth,
        notificationService: NotificationService
    ): EventRepository {
        return FirebaseEventRepositoryImpl(firestoreDatabase, userRepository, firebaseAuth, notificationService)
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
        dataStore: DataStore<Preferences>,
        notificationService: NotificationService
    ): UserRepository {
        return FirebaseUserRepositoryImpl(
            firebaseAuth,
            firestoreDatabase,
            firebaseStorage,
            dataStore,
            notificationService
        )
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

}



