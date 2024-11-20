package com.example.socialmeetingapp

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.User
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val userRepository: UserRepository,
    firebaseAuth: FirebaseAuth
) : ViewModel() {
    private var _state = MutableStateFlow<MainState>(MainState.Loading)
    val state = _state.asStateFlow().onStart {

        if (isFirstTimeLaunch()) {
            _state.value = MainState.Welcome
            return@onStart
        }

        refreshUser()
    }.stateIn(viewModelScope, SharingStarted.Lazily, MainState.Loading)


    init { firebaseAuth.addAuthStateListener { refreshUser() } }

    private fun refreshUser() {
        viewModelScope.launch {
            when (val userResult = userRepository.getCurrentUser()) {
                is Result.Success -> {
                    val user = userResult.data
                    val isEmailVerified = userRepository.isCurrentUserVerified()
                    _state.value = MainState.Content(user, isEmailVerified)
                }
                is Result.Error -> {
                    _state.value = MainState.Content(null, false)
                }
                else -> { }
            }
        }
    }

    fun onIntroductionFinished() {
        viewModelScope.launch {
            Log.d("MainViewModel", "onIntroductionFinished")
            dataStore.edit {
                it[FIRST_TIME_LAUNCH] = false
            }
        }
    }

    fun resendVerificationEmail() {
        viewModelScope.launch {
            when (val result = userRepository.sendEmailVerification()) {
                is Result.Success -> {
                    SnackbarManager.showMessage("Verification email sent")
                }
                is Result.Error -> {
                    SnackbarManager.showMessage(result.message)
                }
                else -> {}
            }
        }
    }

    private suspend fun isFirstTimeLaunch(): Boolean {
        Log.d("MainViewModel", (dataStore.data.first()[FIRST_TIME_LAUNCH] != false).toString())
        return dataStore.data.first()[FIRST_TIME_LAUNCH] != false

    }

    companion object {
        val FIRST_TIME_LAUNCH = booleanPreferencesKey("FIRST_TIME_LAUNCH")
    }

}

sealed class MainState(
) {
    data object Loading : MainState()
    data object Welcome : MainState()
    data object CreateProfile : MainState()

    data class Content(
        val user: User? = null,
        val isEmailVerified: Boolean = false,
    ) : MainState()


}
