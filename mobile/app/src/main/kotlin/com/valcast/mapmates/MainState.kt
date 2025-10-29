package com.valcast.mapmates

import kotlinx.coroutines.flow.SharedFlow
import mapmates.core.navigation.api.AppNavGraphBuilder
import mapmates.core.navigation.api.Navigator

data class MainState(
    val navGraphBuilders: Set<AppNavGraphBuilder>,
    val isUserAuthenticated: Boolean,
    val navigatorFlow: SharedFlow<Navigator.Event>
)