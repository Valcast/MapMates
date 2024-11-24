package com.example.socialmeetingapp.presentation.settings

import androidx.lifecycle.ViewModel
import com.example.socialmeetingapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    fun signOut() = userRepository.signOut()
}