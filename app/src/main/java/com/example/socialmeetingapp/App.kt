package com.example.socialmeetingapp

import android.app.Application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.compose.SocialMeetingAppTheme
import com.example.socialmeetingapp.data.utils.PermissionManager
import com.example.socialmeetingapp.presentation.navigation.Routes
import com.example.socialmeetingapp.presentation.navigation.NavGraph
import com.example.socialmeetingapp.presentation.navigation.NavigationBar
import com.example.socialmeetingapp.presentation.snackbar.SnackbarManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltAndroidApp
class SocialMeetingApplication : Application()

@Composable
fun SocialMeetingApp(splashScreen: SplashScreen, permissionManager: PermissionManager, lifecycleScope: CoroutineScope) {
    val viewModel = hiltViewModel<MainViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle().value

    splashScreen.setKeepOnScreenCondition { state is MainState.Loading }

    val navController = rememberNavController()
    var selectedNavItem by remember { mutableIntStateOf(0) }
    var currentRoute by remember { mutableStateOf<Routes>(Routes.Map) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        lifecycleScope.launch {
            SnackbarManager.messages.collectLatest { message ->
                snackbarHostState.showSnackbar(message)
            }
        }
    }


    SocialMeetingAppTheme {
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
            bottomBar = {
                if (currentRoute in listOf(
                        Routes.Map,
                        Routes.Profile,
                        Routes.Settings
                    )
                ) {
                    NavigationBar(modifier = Modifier,
                        selected = selectedNavItem,
                        onItemSelected = { index ->
                            selectedNavItem = index

                            navController.navigate(
                                listOf(
                                    Routes.Map,
                                    Routes.Profile,
                                    Routes.Settings
                                )[index]
                            ) {
                                popUpTo(navController.graph.id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        })
                } else {
                    return@Scaffold
                }
            }) { innerPadding ->
            NavGraph(
                navController,
                state,
                permissionManager,
                { viewModel.disableFirstTimeLaunch() },
                {
                    currentRoute = it
                },
                innerPadding
            )

        }
    }
}