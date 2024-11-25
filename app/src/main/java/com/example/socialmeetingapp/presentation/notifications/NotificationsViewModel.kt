package com.example.socialmeetingapp.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.Notification
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed class NotificationsState {
    data object Loading : NotificationsState()
    data class Content(val notifications: List<Notification>) : NotificationsState()
    data class Error(val message: String) : NotificationsState()
}

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    userRepository: UserRepository
): ViewModel() {
    val state = userRepository.currentUser.map { userResult ->
        if (userResult is Result.Success && userResult.data != null) {
            NotificationsState.Content(userResult.data.notifications.sortedByDescending { it.createdAt })
        } else {
            NotificationsState.Error("Failed to load notifications")
        }

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotificationsState.Loading)
}