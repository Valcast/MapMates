package com.example.socialmeetingapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.compose.SocialMeetingAppTheme
import com.example.socialmeetingapp.data.utils.PermissionManager
import com.example.socialmeetingapp.presentation.authentication.forgot.ForgotPasswordScreen
import com.example.socialmeetingapp.presentation.authentication.forgot.ForgotPasswordViewModel
import com.example.socialmeetingapp.presentation.authentication.login.LoginScreen
import com.example.socialmeetingapp.presentation.authentication.login.LoginViewModel
import com.example.socialmeetingapp.presentation.authentication.register.RegisterScreen
import com.example.socialmeetingapp.presentation.authentication.register.RegisterViewModel
import com.example.socialmeetingapp.presentation.authentication.register.locationinfo.RegisterLocationScreen
import com.example.socialmeetingapp.presentation.authentication.register.profileinfo.RegisterProfileScreen
import com.example.socialmeetingapp.presentation.authentication.register.profileinfo.RegisterProfileScreenViewModel
import com.example.socialmeetingapp.presentation.common.NavigationManager
import com.example.socialmeetingapp.presentation.common.Routes
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import com.example.socialmeetingapp.presentation.event.EventScreen
import com.example.socialmeetingapp.presentation.event.EventViewModel
import com.example.socialmeetingapp.presentation.event.createventflow.CreateEventScreen
import com.example.socialmeetingapp.presentation.event.createventflow.CreateEventViewModel
import com.example.socialmeetingapp.presentation.home.HomeScreen
import com.example.socialmeetingapp.presentation.home.HomeViewModel
import com.example.socialmeetingapp.presentation.introduction.IntroductionScreen
import com.example.socialmeetingapp.presentation.navigation.NavigationBar
import com.example.socialmeetingapp.presentation.profile.ProfileScreen
import com.example.socialmeetingapp.presentation.profile.ProfileViewModel
import com.example.socialmeetingapp.presentation.settings.SettingsScreen
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var permissionManager: PermissionManager
    private lateinit var splashScreen: SplashScreen


    @SuppressLint("RestrictedApi")
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
            val snackbarHostState = remember { SnackbarHostState() }

            val startDestination = when {
                state is MainState.Content && state.isFirstTimeLaunch -> Routes.Introduction
                state is MainState.Content && !state.isLoggedIn -> Routes.Login
                state is MainState.Content && state.isLoggedIn -> Routes.Map
                else -> return@setContent
            }

            LaunchedEffect("Snackbar Manager") {
                SnackbarManager.messages.collectLatest {
                    snackbarHostState.showSnackbar(
                        it
                    )
                }
            }

            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route?.let {
                    Routes.fromString(it)
                } ?: startDestination

            LaunchedEffect("Navigation Manager") {
                NavigationManager.route.collect { screen ->
                    navController.navigate(screen) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                    }

                }
            }

            SocialMeetingAppTheme {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        if (currentRoute == Routes.Map || currentRoute == Routes.Profile || currentRoute == Routes.Settings) {
                            NavigationBar(
                                currentRoute = currentRoute,
                                onItemClicked = { NavigationManager.navigateTo(it) }
                            )
                        }

                    }) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None },
                        popEnterTransition = { EnterTransition.None },
                        popExitTransition = { ExitTransition.None },
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        composable<Routes.Introduction> {
                            IntroductionScreen(onFinish = {
                                viewModel.onIntroductionFinished()
                                NavigationManager.navigateTo(Routes.Login)
                            })
                        }

                        //////////////////////////
                        //   MAIN NAVIGATION   //
                        ////////////////////////

                        composable<Routes.Map> {
                            val viewModel = hiltViewModel<HomeViewModel>()



                            HomeScreen(
                                eventsResult = viewModel.eventsData.collectAsStateWithLifecycle().value,
                                currentLocationResult = viewModel.locationData.collectAsStateWithLifecycle().value,
                                onMapLongClick = {
                                    NavigationManager.navigateTo(
                                        Routes.CreateEvent(
                                            it.latitude,
                                            it.longitude
                                        )
                                    )
                                },
                                onEventClick = { NavigationManager.navigateTo(Routes.Event(it)) }
                            )
                        }

                        composable<Routes.Profile> {
                            val args = it.toRoute<Routes.Profile>()
                            val viewModel = hiltViewModel<ProfileViewModel>()

                            viewModel.getUserByID(args.userID)

                            ProfileScreen(
                                userData = viewModel.userData.collectAsStateWithLifecycle().value,
                                onLogout = { viewModel.logout() }
                            )
                        }

                        composable<Routes.Settings> { SettingsScreen() }

                        ///////////////////////////
                        //    AUTHENTICATION    //
                        /////////////////////////

                        composable<Routes.Login> {
                            val viewModel = hiltViewModel<LoginViewModel>()
                            LoginScreen(
                                state = viewModel.state.collectAsStateWithLifecycle().value,
                                onLogin = { email, password -> viewModel.login(email, password) },
                                onGoToRegister = { NavigationManager.navigateTo(Routes.Register) }
                            )
                        }

                        composable<Routes.ForgotPassword> {
                            val viewModel = hiltViewModel<ForgotPasswordViewModel>()
                            ForgotPasswordScreen(
                                state = viewModel.state.collectAsStateWithLifecycle().value,
                                onResetPassword = { email -> viewModel.resetPassword(email) },
                                onGoToLogin = { navController.popBackStack() }
                            )
                        }

                        //////////////////////////
                        //    REGISTER FLOW    //
                        ////////////////////////

                        composable<Routes.Register> {
                            val viewModel = hiltViewModel<RegisterViewModel>()
                            RegisterScreen(
                                state = viewModel.state.collectAsStateWithLifecycle().value,
                                onGoToLogin = { NavigationManager.navigateTo(Routes.Login) },
                                registerUser = viewModel::registerUser
                            )
                        }

                        composable<Routes.RegisterProfileInfo> {
                            val viewModel = hiltViewModel<RegisterProfileScreenViewModel>()
                            RegisterProfileScreen(
                                state = viewModel.state.collectAsStateWithLifecycle().value,
                                onNextClick = { name, bio -> viewModel.modifyUser(name, bio) }
                            )
                        }

                        composable<Routes.RegisterLocation> {
                            RegisterLocationScreen(
                                handleLocationPermission = { updateLocationPermission ->
                                    permissionManager.checkPermissions(
                                        PermissionManager.FINE_LOCATION_PERMISSION,
                                    ) { updateLocationPermission(it) }
                                },
                                onSkip = { NavigationManager.navigateTo(Routes.Map) }
                            )
                        }

                        //////////////////
                        //    EVENT    //
                        ////////////////

                        composable<Routes.CreateEvent> {
                            val args = it.toRoute<Routes.CreateEvent>()
                            val viewModel = hiltViewModel<CreateEventViewModel>()

                            LaunchedEffect(Unit) {
                                viewModel.updateLocation(LatLng(args.latitude, args.longitude))
                            }


                            CreateEventScreen(
                                event = viewModel.eventData.collectAsStateWithLifecycle().value,
                                uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
                                isNextButtonEnabled = viewModel.isNextButtonEnabled.collectAsStateWithLifecycle().value,
                                isRulesAccepted = viewModel.isRulesAccepted.collectAsStateWithLifecycle().value,
                                onNext = { viewModel.nextStep() },
                                onPrevious = { viewModel.previousStep() },
                                onUpdateTitle = { viewModel.updateTitle(it) },
                                onUpdateDescription = { viewModel.updateDescription(it) },
                                onUpdateIsPrivate = { viewModel.updateIsPrivate(it) },
                                onUpdateIsOnline = { viewModel.updateIsOnline(it) },
                                onUpdateMaxParticipants = { viewModel.updateMaxParticipants(it) },
                                onSetStartTime = { viewModel.setStartTime(it) },
                                onSetEndTime = { viewModel.setEndTime(it) },
                                onUpdateLocation = { viewModel.updateLocation(it) },
                                onUpdateRules = { viewModel.updateRulesAccepted() },
                                onCancel = { NavigationManager.navigateTo(Routes.Map) }
                            )
                        }

                        composable<Routes.Event> {
                            val args = it.toRoute<Routes.Event>()
                            val viewModel = hiltViewModel<EventViewModel>()
                            viewModel.getEvent(args.id)

                            EventScreen(
                                state = viewModel.state.collectAsStateWithLifecycle().value,
                                onJoinEvent = { viewModel.joinEvent(args.id) },
                                onBack = { NavigationManager.navigateTo(Routes.Map) },
                                onGoToAuthor = { NavigationManager.navigateTo(Routes.Profile(it)) }
                            )
                        }
                    }
                }
            }
        }
    }
}