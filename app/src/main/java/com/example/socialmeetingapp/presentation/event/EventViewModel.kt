package com.example.socialmeetingapp.presentation.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Resource
import com.example.socialmeetingapp.domain.event.model.Event
import com.example.socialmeetingapp.domain.event.usecase.GetEventUseCase
import com.example.socialmeetingapp.domain.event.usecase.JoinEventUseCase
import com.example.socialmeetingapp.domain.event.usecase.LeaveEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val getEventUseCase: GetEventUseCase,
    private val joinEventUseCase: JoinEventUseCase,
    private val leaveEventUseCase: LeaveEventUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<Resource<Event>>(Resource.Loading)
    val state = _state.asStateFlow()


    fun getEvent(id: String) {
        viewModelScope.launch {
            when (val eventResult = getEventUseCase(id)) {
                is Resource.Success<Event> -> {
                    _state.value = Resource.Success(eventResult.data)
                }
                is Resource.Error -> {
                    _state.value = Resource.Error(eventResult.message)
                }
                else -> {}
            }
        }
    }
    suspend fun joinEvent(id: String) = joinEventUseCase(id)
    suspend fun leaveEvent(id: String) = leaveEventUseCase(id)

}