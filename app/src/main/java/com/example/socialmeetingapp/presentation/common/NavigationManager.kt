package com.example.socialmeetingapp.presentation.common

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NavigationManager {
    private var _route = MutableSharedFlow<Routes>(replay = 1)
    val route = _route.asSharedFlow()

    fun navigateTo(event: Routes) {
        _route.tryEmit(event)
    }

}
