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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MainState {
    data object Loading : MainState()
    data object Welcome : MainState()
    data object CreateProfile : MainState()
    data object NotAuthenticated : MainState()

    data class Content(
        val user: User,
        val isEmailVerified: Boolean = false,
    ) : MainState()


}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val userRepository: UserRepository,
) : ViewModel() {

    val state = userRepository.currentUser.map { userResult ->
        return@map when {
            isFirstTimeLaunch() -> MainState.Welcome
            userResult is Result.Success && userResult.data == null -> MainState.CreateProfile
            userResult is Result.Success && userResult.data != null -> MainState.Content(
                user = userResult.data,
                isEmailVerified = FirebaseAuth.getInstance().currentUser?.isEmailVerified == true
            )
            userResult is Result.Error -> MainState.NotAuthenticated
            else -> MainState.Loading
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MainState.Loading
    )


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


