package com.example.socialmeetingapp.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.socialmeetingapp.MainState
import com.example.socialmeetingapp.data.utils.PermissionManager
import com.example.socialmeetingapp.presentation.authentication.forgot.ForgotPasswordScreen
import com.example.socialmeetingapp.presentation.authentication.login.LoginScreen
import com.example.socialmeetingapp.presentation.authentication.register.RegisterScreen
import com.example.socialmeetingapp.presentation.authentication.register.locationinfo.RegisterLocationScreen
import com.example.socialmeetingapp.presentation.authentication.register.profileinfo.RegisterProfileScreen
import com.example.socialmeetingapp.presentation.event.EventScreen
import com.example.socialmeetingapp.presentation.event.createevent.CreateEventScreen
import com.example.socialmeetingapp.presentation.introduction.IntroductionScreen
import com.example.socialmeetingapp.presentation.home.HomeScreen
import com.example.socialmeetingapp.presentation.profile.ProfileScreen
import com.example.socialmeetingapp.presentation.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    state: MainState,
    permissionManager: PermissionManager,
    disableFirstTimeLaunch: () -> Unit,
    setCurrentRoute: (Routes) -> Unit,
    innerPadding: PaddingValues
) {

    NavHost(
        navController = navController,
        startDestination = when {
            state is MainState.Content && state.isFirstTimeLaunch -> Routes.Introduction
            state is MainState.Content && !state.isLoggedIn -> Routes.Login
            state is MainState.Content && state.isLoggedIn -> Routes.Map
            else -> return
        },
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable<Routes.Introduction> {
            setCurrentRoute(Routes.Introduction)

            IntroductionScreen(innerPadding = innerPadding, onFinish = {
                disableFirstTimeLaunch()

                navController.navigate(Routes.Login) {
                    popUpTo(Routes.Introduction) {
                        saveState = true
                    }
                    launchSingleTop = true
                }
            })
        }

        //////////////////////////
        //   MAIN NAVIGATION   //
        ////////////////////////

        composable<Routes.Map> {
            setCurrentRoute(Routes.Map)

            HomeScreen(innerPadding = innerPadding,
                goToCreateEventScreen = { latitute, longtitude ->
                    navController.navigate(Routes.CreateEvent(latitute, longtitude)) {
                        popUpTo(Routes.Map) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                },
                navigateToEvent = { eventId: String ->
                    navController.navigate(Routes.Event(eventId)) {
                        popUpTo(Routes.Map) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                })
        }

        composable<Routes.Profile> {
            setCurrentRoute(Routes.Profile)

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

        composable<Routes.Settings> {
            setCurrentRoute(Routes.Settings)

            SettingsScreen(innerPadding = innerPadding)
        }

        ///////////////////////////
        //    AUTHENTICATION    //
        /////////////////////////

        composable<Routes.Login> {
            setCurrentRoute(Routes.Login)

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

        composable<Routes.ForgotPassword> {
            setCurrentRoute(Routes.ForgotPassword)

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

        //////////////////////////
        //    REGISTER FLOW    //
        ////////////////////////

        composable<Routes.Register> {
            setCurrentRoute(Routes.Register)

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
            setCurrentRoute(Routes.RegisterProfileInfo)

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
            setCurrentRoute(Routes.RegisterLocation)

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

        composable<Routes.CreateEvent> {
            val args = it.toRoute<Routes.CreateEvent>()
            setCurrentRoute(Routes.CreateEvent(args.latitude, args.longitude))

            CreateEventScreen(args.latitude, args.longitude, navigateToMap = {
                navController.navigate(Routes.Map) {
                    popUpTo(Routes.CreateEvent(args.latitude, args.longitude)) {
                        saveState = true
                    }
                    launchSingleTop = true
                }
            }, innerPadding)
        }

        composable<Routes.Event> {
            val args = it.toRoute<Routes.Event>()
            setCurrentRoute(Routes.Event(args.id))

            EventScreen(args.id, navigateToMap = {
                navController.navigate(Routes.Map) {
                    popUpTo(Routes.Map) {
                        saveState = true
                    }
                    launchSingleTop = true
            }
            }, innerPadding = innerPadding)
        }
    }
}