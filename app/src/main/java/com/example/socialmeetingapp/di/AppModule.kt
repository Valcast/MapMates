package com.example.socialmeetingapp.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.socialmeetingapp.data.api.GeocodingApi
import com.example.socialmeetingapp.data.repository.FirebaseUserRepositoryImpl
import com.example.socialmeetingapp.data.repository.LocationRepositoryImpl
import com.example.socialmeetingapp.data.utils.NetworkManager
import com.example.socialmeetingapp.domain.location.repository.LocationRepository
import com.example.socialmeetingapp.domain.user.repository.UserRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private val Context.dataStore by preferencesDataStore("settings")
    private val storage = Firebase.storage
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideFusedLocationClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(fusedLocationProviderClient: FusedLocationProviderClient, geocodingApi: GeocodingApi, @ApplicationContext context: Context): LocationRepository {
        return LocationRepositoryImpl(fusedLocationProviderClient, context, geocodingApi)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth() = auth

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun provideNetworkManager(connectivityManager: ConnectivityManager): NetworkManager {
        return NetworkManager(connectivityManager)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideFirestoreDatabase() = firestore

    @Provides
    @Singleton
    fun provideFirebaseStorage() = storage

    @Provides
    @Singleton
    fun provideUserRepository(firebaseAuth: FirebaseAuth, networkManager: NetworkManager, firestoreDatabase: FirebaseFirestore): UserRepository {
        return FirebaseUserRepositoryImpl(firebaseAuth, networkManager, firestoreDatabase)
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



