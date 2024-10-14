package com.example.socialmeetingapp.presentation.event.createevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Resource
import com.example.socialmeetingapp.domain.event.model.Event
import com.example.socialmeetingapp.domain.event.usecase.CreateEventUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val createEventUseCase: CreateEventUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<Unit>>(Resource.Initial)
    val state = _state.asStateFlow()

    fun createEvent(
        title: String,
        description: String,
        location: LatLng,
        isPrivate: Boolean,
        isOnline: Boolean,
        maxParticipants: Int,
        date: Date,
        time: Date,
        duration: Int
    ) {
        val event = Event(
            title = title,
            description = description,
            location = location,
            isPrivate = isPrivate,
            isOnline = isOnline,
            maxParticipants = maxParticipants,
            date = date,
            time = time,
            duration = duration
        )

        _state.value = Resource.Loading

        viewModelScope.launch {
            val result = createEventUseCase(event)

            when (result) {
                is Resource.Success<Unit> -> _state.value = Resource.Success<Unit>()
                is Resource.Error -> _state.value = Resource.Error(result.message)
                else -> {}
            }
        }
    }
}