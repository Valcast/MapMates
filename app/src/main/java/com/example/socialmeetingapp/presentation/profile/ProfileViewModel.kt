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
    data class Content(
        val user: User,
        val userEvents: List<Event> = emptyList(),
        val isMyProfile: Boolean = false,
        val isObservedUser: Boolean = false,
        val followers: List<User> = emptyList(),
        val following: List<User> = emptyList()
    ) : ProfileState()
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
        val currentUser = userRepository.currentUser.value
        val userEvents = eventRepository.eventsStateFlow.value.filter { it.author.id == userID }

        viewModelScope.launch {
            val isMyProfile = currentUser is Result.Success && currentUser.data!!.id == userID
            val userResult = if (isMyProfile) currentUser else userRepository.getUser(userID)

            if (userResult is Result.Success) {
                val user = userResult.data!!
                val followers = user.followers.mapNotNull {
                    (userRepository.getUser(it) as? Result.Success<User>)?.data
                }
                val following = user.following.mapNotNull {
                    (userRepository.getUser(it) as? Result.Success<User>)?.data
                }
                val isObservedUser = !isMyProfile && currentUser is Result.Success && currentUser.data!!.following.contains(userID)

                _userData.update {
                    ProfileState.Content(
                        user = user,
                        followers = followers,
                        following = following,
                        userEvents = userEvents,
                        isMyProfile = isMyProfile,
                        isObservedUser = isObservedUser
                    )
                }
            } else {
                _userData.update {
                    ProfileState.Error("Failed to load user")
                }
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


