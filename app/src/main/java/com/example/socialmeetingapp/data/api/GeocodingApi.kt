package com.example.socialmeetingapp.data.api

import com.example.socialmeetingapp.data.api.model.GeocodingResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {

    @GET("/maps/api/geocode/json")
    suspend fun getAddressFromLatLng(@Query("latlng") latLng: String, @Query("key") apiKey: String): Response<GeocodingResult>
}