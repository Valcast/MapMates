package com.example.socialmeetingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.example.compose.SocialMeetingAppTheme
import com.example.socialmeetingapp.domain.model.Theme
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.initialize
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import mapmates.core.navigation.api.Navigator

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var splashScreen: SplashScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance(),
        )

        enableEdgeToEdge()

        setContent {
            val viewModel = hiltViewModel<MainViewModel>()

            val state by viewModel.state.collectAsStateWithLifecycle()

            val theme = viewModel.settings.collectAsStateWithLifecycle().value.theme
            val isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value
            splashScreen.setKeepOnScreenCondition { isLoading }

            when (theme) {
                Theme.LIGHT -> WindowCompat.getInsetsController(
                    window, window.decorView
                ).isAppearanceLightStatusBars = true

                Theme.DARK, Theme.SYSTEM -> WindowCompat.getInsetsController(
                    window, window.decorView
                ).isAppearanceLightStatusBars = false
            }

            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(Unit) {
                SnackbarManager.messages.collectLatest { snackbarHostState.showSnackbar(it) }
            }

            LaunchedEffect(state.navigatorFlow) {
                state.navigatorFlow.collect { event ->
                    when (event) {
                        is Navigator.Event.Up -> navController.navigateUp()
                        is Navigator.Event.ToDestination -> {
                            navController.navigate(event.destination.value) {
                                event.builder(this)
                            }
                        }
                    }
                }
            }

            SocialMeetingAppTheme(theme) {
                Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, bottomBar = {

                }) { innerPadding ->
                    val navGraph = remember {
                        navController.createGraph(startDestination = "login") {
                            state.navGraphBuilders.forEach { it.build(this) }
                        }
                    }

                    NavHost(
                        navController = navController,
                        graph = navGraph,
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None },
                        popEnterTransition = { EnterTransition.None },
                        popExitTransition = { ExitTransition.None },
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}