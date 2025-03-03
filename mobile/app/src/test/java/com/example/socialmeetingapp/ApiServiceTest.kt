package com.example.socialmeetingapp

import com.example.socialmeetingapp.data.api.GeocodingApi
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var apiService: GeocodingApi

    @Before
    fun setup() {
        server = MockWebServer()
        apiService = Retrofit.Builder().baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(GeocodingApi::class.java)
    }

    @Test
    fun `getAddressFromLatLng should return Geocoding Result`() = runTest {

        val mockResponse = MockResponse().setBody(
            """
        {
            "plus_code": {
                "compound_code": "V9G5+P5 Paris, France",
                "global_code": "8FW4V9G5+P5"
            },
            "results": [
                {
                    "address_components": [
                        {
                            "long_name": "Paris",
                            "short_name": "Paris",
                            "types": [
                                "locality",
                                "political"
                            ]
                        },
                        {
                            "long_name": "Paris",
                            "short_name": "Paris",
                            "types": [
                                "administrative_area_level_2",
                                "political"
                            ]
                        },
                        {
                            "long_name": "Île-de-France",
                            "short_name": "Île-de-France",
                            "types": [
                                "administrative_area_level_1",
                                "political"
                            ]
                        },
                        {
                            "long_name": "France",
                            "short_name": "FR",
                            "types": [
                                "country",
                                "political"
                            ]
                        }
                    ],
                    "formatted_address": "Paris, France",
                    "geometry": {
                        "bounds": {
                            "northeast": {
                                "lat": 48.9021449,
                                "lng": 2.4699209
                            },
                            "southwest": {
                                "lat": 48.815573,
                                "lng": 2.224199
                            }
                        },
                        "location": {
                            "lat": 48.856613,
                            "lng": 2.352222
                        },
                        "location_type": "APPROXIMATE",
                        "viewport": {
                            "northeast": {
                                "lat": 48.9021449,
                                "lng": 2.4699209
                            },
                            "southwest": {
                                "lat": 48.815573,
                                "lng": 2.224199
                            }
                        }
                    },
                    "place_id": "ChIJD7fiBh9u5kcRYJSMaMOCCwQ",
                    "plus_code": {
                        "compound_code": "V9G5+P5 Paris, France",
                        "global_code": "8FW4V9G5+P5"
                    },
                    "types": [
                        "locality",
                        "political"
                    ]
                }
            ],
            "status": "OK"
        }
        """.trimIndent()
        ).setResponseCode(200)

        server.enqueue(mockResponse)

        val response = apiService.getAddressFromLatLng("48.856613,2.352222", "dummy_api_key")

        assertEquals("OK", response.body()?.status)
        assertEquals("Paris, France", response.body()?.results?.first()?.formatted_address)
    }

    @Test
    fun `getAddressFromLatLng should handle 404 not found`() = runTest {
        server.enqueue(MockResponse().setResponseCode(404))

        val response = apiService.getAddressFromLatLng("48.856613,2.352222", "dummy_api_key")

        assertEquals(404, response.code())
    }

    @Test
    fun `getAddressFromLatLng should handle invalid API key`() = runTest {
        val mockResponse = MockResponse().setBody(
            """
        {
            "error_message": "The provided API key is invalid.",
            "results": [],
            "status": "REQUEST_DENIED"
        }
        """.trimIndent()
        ).setResponseCode(200)

        server.enqueue(mockResponse)

        val response = apiService.getAddressFromLatLng("48.856613,2.352222", "invalid_api_key")

        assertEquals("REQUEST_DENIED", response.body()?.status)
        assertEquals("The provided API key is invalid.", response.body()?.error_message)
        assertEquals(0, response.body()?.results?.size)
    }

    @After
    fun cleanup() {
        server.shutdown()
    }


}