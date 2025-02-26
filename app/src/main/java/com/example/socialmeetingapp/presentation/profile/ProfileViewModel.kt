package com.example.socialmeetingapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.User
import com.example.socialmeetingapp.domain.model.UserPreview
import com.example.socialmeetingapp.domain.repository.EventRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileState {
    data object Loading : ProfileState()
    data class Error(val message: String) : ProfileState()
    data class Content(
        val user: User,
        val userEventDetails: List<Event> = emptyList(),
        val isMyProfile: Boolean = false,
        val isObservedUser: Boolean = false,
        val followers: List<UserPreview> = emptyList(),
        val following: List<UserPreview> = emptyList()
    ) : ProfileState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository
) : ViewModel() {
    private val _userData = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val userData = _userData.asStateFlow()

    fun getUserByID(userID: String) {
        viewModelScope.launch {
            val currentUser = userRepository.getCurrentUser()

            when (val userResult = userRepository.getUser(userID)) {
                is Result.Success -> {
                    val user = userResult.data

                    val userEvents = eventRepository.events.value.filter { it.author.id == user.id }

                    val isMyProfile = currentUser is Result.Success && currentUser.data.id == user.id
                    val isObservedUser = currentUser is Result.Success && currentUser.data.following.any { it == user.id }

                    val followersResult = userRepository.getUsersPreviews(user.followers)
                    val followingResult = userRepository.getUsersPreviews(user.following)

                    if (followersResult is Result.Success && followingResult is Result.Success) {
                        _userData.value = ProfileState.Content(user, userEvents, isMyProfile, isObservedUser, followersResult.data, followingResult.data)
                    } else {
                        _userData.value = ProfileState.Error("Error loading followers or following")
                    }
                }

                is Result.Error -> {
                    _userData.value = ProfileState.Error(userResult.message)
                }

                else -> {}
            }
        }
    }

    fun followUser(friendID: String) {
        viewModelScope.launch {
            when (val addFriendResult = userRepository.followUser(friendID)) {
                is Result.Success -> {
                    SnackbarManager.showMessage("You are now following this user")
                    if (_userData.value is ProfileState.Content && (_userData.value as ProfileState.Content).isMyProfile) {
                        getUserByID((_userData.value as ProfileState.Content).user.id)
                    } else {
                        getUserByID(friendID)
                    }
                }

                is Result.Error -> {
                    SnackbarManager.showMessage(addFriendResult.message)
                }

                else -> {}
            }
        }
    }

    fun unfollowUser(friendID: String) {
        viewModelScope.launch {
            when (val deleteFriendResult = userRepository.unfollowUser(friendID)) {
                is Result.Success -> {
                    SnackbarManager.showMessage("You have unfollowed this user")
                    if (_userData.value is ProfileState.Content && (_userData.value as ProfileState.Content).isMyProfile) {
                        getUserByID((_userData.value as ProfileState.Content).user.id)
                    } else {
                        getUserByID(friendID)
                    }
                }

                is Result.Error -> {
                    SnackbarManager.showMessage(deleteFriendResult.message)
                }

                else -> {}
            }
        }
    }

    fun deleteFollower(friendID: String) {
        viewModelScope.launch {
            when (val deleteFriendResult = userRepository.deleteFollower(friendID)) {
                is Result.Success -> {
                    SnackbarManager.showMessage("Follower deleted")
                    if (_userData.value is ProfileState.Content && (_userData.value as ProfileState.Content).isMyProfile) {
                        getUserByID((_userData.value as ProfileState.Content).user.id)
                    } else {
                        getUserByID(friendID)
                    }
                }

                is Result.Error -> {
                    SnackbarManager.showMessage(deleteFriendResult.message)
                }

                else -> {}
            }
        }
    }

}


