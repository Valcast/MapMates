package com.example.socialmeetingapp

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.compose.SocialMeetingAppTheme
import com.example.socialmeetingapp.data.utils.PermissionManager
import com.example.socialmeetingapp.presentation.activities.ActivitiesScreen
import com.example.socialmeetingapp.presentation.activities.ActivitiesViewModel
import com.example.socialmeetingapp.presentation.authentication.forgot.ForgotPasswordScreen
import com.example.socialmeetingapp.presentation.authentication.forgot.ForgotPasswordViewModel
import com.example.socialmeetingapp.presentation.authentication.login.LoginScreen
import com.example.socialmeetingapp.presentation.authentication.login.LoginViewModel
import com.example.socialmeetingapp.presentation.authentication.register.RegisterScreen
import com.example.socialmeetingapp.presentation.authentication.register.RegisterViewModel
import com.example.socialmeetingapp.presentation.authentication.register.createprofileflow.CreateProfileScreen
import com.example.socialmeetingapp.presentation.authentication.register.createprofileflow.CreateProfileViewModel
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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var permissionManager: PermissionManager
    private lateinit var splashScreen: SplashScreen
    private val viewModel: MainViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume")
        viewModel.refreshUser()
    }


    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        permissionManager = PermissionManager(this)



        enableEdgeToEdge()
        setContent {
            val state = viewModel.state.collectAsStateWithLifecycle().value

            splashScreen.setKeepOnScreenCondition { state is MainState.Loading }

            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }

            val startDestination = when {
                state is MainState.Content && state.isFirstTimeLaunch -> Routes.Introduction
                state is MainState.Content && state.user == null -> Routes.Login
                state is MainState.Content && state.user != null -> Routes.Map
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
                    topBar =  {
                        if (state.isEmailVerified == false) {
                            Row(modifier = Modifier.fillMaxWidth().padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text = "Your account is not verified yet.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )

                                Button(onClick = { viewModel.resendVerificationEmail() }) {
                                    Text(
                                        text = "Verify",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                    )
                                }
                            }
                        }
                    },
                    bottomBar = {
                        if (currentRoute == Routes.Map || currentRoute == Routes.Activities || currentRoute == Routes.Profile(state.user?.id ?: "") || currentRoute == Routes.Settings) {

                            NavigationBar(
                                currentRoute = currentRoute,
                                onItemClicked = { NavigationManager.navigateTo(it) },
                                profileImageUrl = state.user?.profilePictureUri ?: Uri.EMPTY,
                                profileID = state.user?.id ?: ""
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
                                onLogout = { viewModel.logout()
                                NavigationManager.navigateTo(Routes.Login) }
                            )
                        }

                        composable<Routes.Settings> { SettingsScreen() }

                        composable<Routes.Activities> {
                            val viewModel = hiltViewModel<ActivitiesViewModel>()

                            ActivitiesScreen(
                                events = viewModel.events.collectAsStateWithLifecycle().value,
                                onCardClick = { NavigationManager.navigateTo(Routes.Event(it)) }
                            )
                        }

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

                        composable<Routes.Register> {
                            val viewModel = hiltViewModel<RegisterViewModel>()
                            RegisterScreen(
                                state = viewModel.state.collectAsStateWithLifecycle().value,
                                onGoToLogin = { NavigationManager.navigateTo(Routes.Login) },
                                registerUser = viewModel::registerUser
                            )
                        }

                        ///////////////////////////
                        //    CREATE PROFILE    //
                        /////////////////////////

                        composable<Routes.CreateProfile> {
                            val viewModel = hiltViewModel<CreateProfileViewModel>()

                            CreateProfileScreen(
                                user = viewModel.user.collectAsStateWithLifecycle().value,
                                uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
                                isNextButtonEnabled = viewModel.isNextButtonEnabled.collectAsStateWithLifecycle().value,
                                isRulesAccepted = viewModel.isRulesAccepted.collectAsStateWithLifecycle().value,
                                onNext = { viewModel.nextStep() },
                                onPrevious = { viewModel.previousStep() },
                                onUpdateUsername = { viewModel.updateUsername(it) },
                                onUpdateBio = { viewModel.updateBio(it) },
                                onUpdateProfilePicture = { viewModel.updateProfilePicture(it) },
                                onUpdateDateOfBirth = { viewModel.updateDateOfBirth(it) },
                                onUpdateGender = { viewModel.updateGender(it) },
                                onUpdateRules = { viewModel.updateRulesAccepted() }
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