package com.example.socialmeetingapp.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.data.repository.FirebaseUserRepositoryImpl
import com.example.socialmeetingapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    fun logout() {
        userRepository.logout()
    }
}

class ProfileState {
}

