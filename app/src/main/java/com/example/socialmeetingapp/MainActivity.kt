package com.example.socialmeetingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.SocialMeetingAppTheme
import com.example.socialmeetingapp.domain.model.navigation.Routes
import com.example.socialmeetingapp.presentation.PermissionManager
import com.example.socialmeetingapp.presentation.authentication.forgot.ForgotPasswordScreen
import com.example.socialmeetingapp.presentation.authentication.login.LoginScreen
import com.example.socialmeetingapp.presentation.authentication.register.RegisterScreen
import com.example.socialmeetingapp.presentation.authentication.register.locationinfo.RegisterLocationScreen
import com.example.socialmeetingapp.presentation.authentication.register.profileinfo.RegisterProfileScreen
import com.example.socialmeetingapp.presentation.introduction.IntroductionScreen
import com.example.socialmeetingapp.presentation.map.MapScreen
import com.example.socialmeetingapp.presentation.navigation.NavigationBar
import com.example.socialmeetingapp.presentation.profile.ProfileScreen
import com.example.socialmeetingapp.presentation.settings.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var permissionManager: PermissionManager
    private lateinit var splashScreen: SplashScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        permissionManager = PermissionManager(this)

        enableEdgeToEdge()
        setContent {
            val viewModel = hiltViewModel<MainViewModel>()
            val state = viewModel.state.collectAsStateWithLifecycle().value

            splashScreen.setKeepOnScreenCondition { state is MainState.Loading }

            val navController = rememberNavController()
            var selectedNavItem by remember { mutableIntStateOf(0) }
            var currentRoute by remember { mutableStateOf<Routes>(Routes.Map) }

            SocialMeetingAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
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
                    NavHost(
                        navController = navController, startDestination = when {
                            state is MainState.Content && state.isFirstTimeLaunch -> Routes.Introduction
                            state is MainState.Content && !state.isLoggedIn -> Routes.Login
                            state is MainState.Content && state.isLoggedIn -> Routes.Map
                            else -> return@Scaffold
                        },
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None },
                        popEnterTransition = { EnterTransition.None },
                        popExitTransition = { ExitTransition.None }
                    ) {
                        composable<Routes.Map> {
                            currentRoute = Routes.Map
                            MapScreen(innerPadding = innerPadding)
                        }
                        composable<Routes.Login> {
                            currentRoute = Routes.Login
                            LoginScreen(innerPadding = innerPadding, navigateToRegister = {
                                navController.navigate(Routes.Register) {
                                    popUpTo(Routes.Login) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                }
                            }, navigateToForgotPassword = {
                                navController.navigate(Routes.ForgotPassword) {
                                    popUpTo(Routes.Login) {
                                        saveState = true
                                    }
                                    launchSingleTop = true

                                }

                            },
                                navigateToMap = {
                                    navController.navigate(Routes.Map) {
                                        popUpTo(Routes.Login) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                    }
                                })
                        }
                        composable<Routes.Register> {
                            currentRoute = Routes.Register
                            RegisterScreen(innerPadding = innerPadding, navigateToLogin = {
                                navController.navigate(Routes.Login) {
                                    popUpTo(Routes.Login) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                }
                            }, navigateToRegisterProfileInfo = {
                                navController.navigate(Routes.RegisterProfileInfo) {
                                    popUpTo(Routes.Register) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                }

                            })
                        }

                        composable<Routes.RegisterProfileInfo> {
                            currentRoute = Routes.RegisterProfileInfo
                            RegisterProfileScreen(
                                innerPadding = innerPadding,
                                navigateToRegisterLocation = {
                                    navController.navigate(Routes.RegisterLocation) {
                                        popUpTo(Routes.RegisterProfileInfo) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        composable<Routes.RegisterLocation> {
                            currentRoute = Routes.RegisterLocation
                            RegisterLocationScreen(
                                innerPadding = innerPadding,
                                handleLocationPermission = { updateLocationPermission ->
                                    permissionManager.checkPermissions(
                                        PermissionManager.FINE_LOCATION_PERMISSION,
                                    ) { updateLocationPermission(it) }
                                },
                                navigateToMap = {
                                    navController.navigate(Routes.Map) {
                                        popUpTo(Routes.Map) {

                                            saveState = true
                                        }
                                    }
                                }
                            )
                        }

                        composable<Routes.ForgotPassword> {
                            currentRoute = Routes.ForgotPassword
                            ForgotPasswordScreen(
                                innerPadding = innerPadding,
                                navigateToLogin = {
                                    navController.navigate(Routes.Login) {
                                        popUpTo(Routes.Login) {
                                            saveState = true
                                        }
                                        launchSingleTop = true

                                    }
                                })
                        }
                        composable<Routes.Settings> {
                            currentRoute = Routes.Settings
                            SettingsScreen(innerPadding = innerPadding)
                        }
                        composable<Routes.Profile> {
                            currentRoute = Routes.Profile
                            ProfileScreen(
                                innerPadding = innerPadding,
                                navigateToLogin = {
                                    navController.navigate(Routes.Login) {
                                        popUpTo(Routes.Profile) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                    }
                                })
                        }
                        composable<Routes.Introduction> {
                            currentRoute = Routes.Introduction
                            IntroductionScreen(innerPadding = innerPadding, onFinish = {
                                viewModel.disableFirstTimeLaunch()

                                navController.navigate(Routes.Login) {
                                    popUpTo(Routes.Introduction) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                }
                            })
                        }

                    }
                }
            }
        }
    }
}








