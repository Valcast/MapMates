package com.example.socialmeetingapp

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.data.repository.FirebaseUserRepositoryImpl
import com.example.socialmeetingapp.domain.model.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    firebaseUserRepository: FirebaseUserRepositoryImpl
): ViewModel() {

    private var _state = MutableStateFlow<MainState>(MainState.Loading)
    val state = _state.asStateFlow().onStart {
        if (firebaseUserRepository.ifUserIsLoggedIn()) {
            _state.value = MainState.LoggedIn
        } else {
            _state.value = MainState.LoggedOut
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, MainState.Loading)

    val routes = listOf(Routes.Map, Routes.Profile, Routes.Settings)
    var selectedNavItem = mutableIntStateOf(0)
        private set

    fun onItemSelected(index: Int) {
        selectedNavItem.intValue = index
    }



}

sealed class MainState {
    data object Loading: MainState()
    data object LoggedIn: MainState()
    data object LoggedOut: MainState()

}
