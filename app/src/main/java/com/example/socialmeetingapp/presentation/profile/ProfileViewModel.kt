package com.example.socialmeetingapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.User
import com.example.socialmeetingapp.domain.repository.EventRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileState {
    data object Loading : ProfileState()
    data class Error(val message: String) : ProfileState()
    data class Content(val user: User, val userEvents: List<Event> = emptyList(), val isMyProfile: Boolean = false, val isObservedUser: Boolean = false) : ProfileState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository
) : ViewModel() {
    private val _userData = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val userData = _userData.asStateFlow()

    private val _newUser = MutableStateFlow(User.EMPTY)
    val newUser = _newUser.asStateFlow()

    fun getUserByID(userID: String) {
        viewModelScope.launch {
            val userResult = async { userRepository.getUser(userID) }.await()
            val currentUser = userRepository.currentUser.value
            val userEvents = eventRepository.eventsStateFlow.value.filter { it.author.id == userID }


            if (userResult is Result.Success && currentUser is Result.Success && currentUser.data != null) {
                _userData.update {
                    ProfileState.Content(
                        user = userResult.data,
                        userEvents = userEvents,
                        isMyProfile = userResult.data.id == currentUser.data.id,
                        isObservedUser = currentUser.data.following.contains(userResult.data.id)
                    )
                }
            } else {
                _userData.update { ProfileState.Error("Failed to load user") }
            }

        }
    }

    fun addFriend(friendID: String) {
        viewModelScope.launch {
            when (val addFriendResult = userRepository.addFriend(friendID)) {
                is Result.Success -> {
                    SnackbarManager.showMessage("Friend added")
                    getUserByID(friendID)
                }

                is Result.Error -> {
                    SnackbarManager.showMessage(addFriendResult.message)
                }

                else -> {}
            }
        }
    }

    fun deleteFriend(friendID: String) {
        viewModelScope.launch {
            when (val deleteFriendResult = userRepository.deleteFriend(friendID)) {
                is Result.Success -> {
                    SnackbarManager.showMessage("Friend deleted")
                    getUserByID(friendID)
                }

                is Result.Error -> {
                    SnackbarManager.showMessage(deleteFriendResult.message)
                }

                else -> {}
            }
        }
    }

}


