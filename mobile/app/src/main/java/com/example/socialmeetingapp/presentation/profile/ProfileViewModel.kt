package com.example.socialmeetingapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.User
import com.example.socialmeetingapp.domain.model.onFailure
import com.example.socialmeetingapp.domain.model.onSuccess
import com.example.socialmeetingapp.domain.repository.EventRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    data object Loading : ProfileState()
    data class Error(val message: String) : ProfileState()
    data class Content(
        val user: User,
        val userEventDetails: List<Event> = emptyList(),
        val isMyProfile: Boolean = false,
        val isObservedUser: Boolean = false,
    ) : ProfileState()
}

@HiltViewModel(assistedFactory = ProfileViewModel.Factory::class)
class ProfileViewModel @AssistedInject constructor(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
    @Assisted private val userId: String
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(userId: String): ProfileViewModel
    }

    private val _userData = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val userData = _userData.asStateFlow()

    private val pagingConfig = PagingConfig(
        pageSize = 20, enablePlaceholders = false, prefetchDistance = 5
    )

    val followers = Pager(pagingConfig) {
        userRepository.getFollowersPagingSource(userId)
    }.flow.cachedIn(viewModelScope)

    val following = Pager(pagingConfig) {
        userRepository.getFollowingPagingSource(userId)
    }.flow.cachedIn(viewModelScope)

    init {
        getUserByID(userId)
    }

    fun getUserByID(userID: String) {
        viewModelScope.launch {
            val currentUserId = userRepository.getCurrentUserId()

            when (val userResult = userRepository.getUser(userID)) {
                is Result.Success -> {
                    val user = userResult.data

                    val userEvents = eventRepository.getEventsByAuthor(user.id)

                    val isMyProfile = user.id == currentUserId

                    if (userEvents is Result.Success) {
                        _userData.value = ProfileState.Content(
                            user,
                            userEvents.data,
                            isMyProfile,
                        )
                    } else {
                        _userData.value = ProfileState.Error("Error loading followers or following")
                    }
                }

                is Result.Failure -> {
                    _userData.value = ProfileState.Error(userResult.message)
                }
            }
        }
    }

    fun followUser(friendID: String) {
        viewModelScope.launch {
            userRepository.followUser(friendID)
                .onSuccess {
                    SnackbarManager.showMessage("You are now following this user")
                    if (_userData.value is ProfileState.Content && (_userData.value as ProfileState.Content).isMyProfile) {
                        getUserByID((_userData.value as ProfileState.Content).user.id)
                    } else {
                        getUserByID(friendID)
                    }
                }
                .onFailure { error ->
                    SnackbarManager.showMessage(error)
                }
        }
    }

    fun unfollowUser(friendID: String) {
        viewModelScope.launch {
            userRepository.unfollowUser(friendID)
                .onSuccess {
                    SnackbarManager.showMessage("You have unfollowed this user")
                    if (_userData.value is ProfileState.Content && (_userData.value as ProfileState.Content).isMyProfile) {
                        getUserByID((_userData.value as ProfileState.Content).user.id)
                    } else {
                        getUserByID(friendID)
                    }
                }
                .onFailure { error ->
                    SnackbarManager.showMessage(error)
                }
        }
    }

    fun deleteFollower(friendID: String) {
        viewModelScope.launch {
            userRepository.deleteFollower(friendID)
                .onSuccess {
                    SnackbarManager.showMessage("Follower deleted")
                    if (_userData.value is ProfileState.Content && (_userData.value as ProfileState.Content).isMyProfile) {
                        getUserByID((_userData.value as ProfileState.Content).user.id)
                    } else {
                        getUserByID(friendID)
                    }
                }
                .onFailure { error ->
                    SnackbarManager.showMessage(error)
                }
        }
    }

}


