package com.example.socialmeetingapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.compose.SocialMeetingAppTheme
import com.example.socialmeetingapp.domain.model.Theme
import com.example.socialmeetingapp.presentation.activities.ActivitiesScreen
import com.example.socialmeetingapp.presentation.activities.ActivitiesViewModel
import com.example.socialmeetingapp.presentation.authentication.forgot.ForgotPasswordScreen
import com.example.socialmeetingapp.presentation.authentication.forgot.ForgotPasswordViewModel
import com.example.socialmeetingapp.presentation.authentication.login.LoginScreen
import com.example.socialmeetingapp.presentation.authentication.login.LoginViewModel
import com.example.socialmeetingapp.presentation.authentication.register.RegisterScreen
import com.example.socialmeetingapp.presentation.authentication.register.RegisterViewModel
import com.example.socialmeetingapp.presentation.chat.ChatRoomListScreen
import com.example.socialmeetingapp.presentation.chat.ChatRoomListViewModel
import com.example.socialmeetingapp.presentation.chat.ChatRoomScreen
import com.example.socialmeetingapp.presentation.chat.ChatRoomViewModel
import com.example.socialmeetingapp.presentation.common.NavigationManager
import com.example.socialmeetingapp.presentation.common.Routes
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import com.example.socialmeetingapp.presentation.event.EditEventScreen
import com.example.socialmeetingapp.presentation.event.EditEventViewModel
import com.example.socialmeetingapp.presentation.event.EventScreen
import com.example.socialmeetingapp.presentation.event.EventViewModel
import com.example.socialmeetingapp.presentation.event.createevent.CreateEventScreen
import com.example.socialmeetingapp.presentation.event.createevent.CreateEventViewModel
import com.example.socialmeetingapp.presentation.home.HomeScreen
import com.example.socialmeetingapp.presentation.home.HomeViewModel
import com.example.socialmeetingapp.presentation.introduction.IntroductionScreen
import com.example.socialmeetingapp.presentation.navigation.NavigationBar
import com.example.socialmeetingapp.presentation.notifications.NotificationScreen
import com.example.socialmeetingapp.presentation.notifications.NotificationsViewModel
import com.example.socialmeetingapp.presentation.profile.ProfileScreen
import com.example.socialmeetingapp.presentation.profile.ProfileViewModel
import com.example.socialmeetingapp.presentation.profile.createprofileflow.CreateProfileScreen
import com.example.socialmeetingapp.presentation.profile.createprofileflow.CreateProfileViewModel
import com.example.socialmeetingapp.presentation.profile.editprofile.EditProfileScreen
import com.example.socialmeetingapp.presentation.profile.editprofile.EditProfileViewModel
import com.example.socialmeetingapp.presentation.profile.settings.SettingsScreen
import com.example.socialmeetingapp.presentation.profile.settings.SettingsViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.initialize
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

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
            val mainViewModel by viewModels<MainViewModel>()

            val startDestination =
                mainViewModel.startDestination.collectAsStateWithLifecycle().value
            val user = mainViewModel.user.collectAsStateWithLifecycle().value
            val theme = mainViewModel.settings.collectAsStateWithLifecycle().value.theme
            val isLoading = mainViewModel.isLoading.collectAsStateWithLifecycle().value
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

            LaunchedEffect(Unit) {
                NavigationManager.route.collect { screen ->
                    when (screen) {
                        is Routes.Map, Routes.Login, Routes.CreateProfile -> {
                            navController.navigate(screen) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }

                        Routes.Activities, Routes.Notifications -> {
                            navController.navigate(screen) {
                                popUpTo(Routes.Map()) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        }

                        is Routes.Event -> {
                            if (navController.currentBackStackEntry?.destination?.route?.contains("CreateEvent") == true) {
                                navController.navigate(screen) {
                                    popUpTo(Routes.Map())
                                }
                            } else navController.navigate(screen)
                        }


                        else -> {
                            navController.navigate(screen) {
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }


            val currentRoute = navController.currentBackStackEntryAsState().value?.run {
                val route = destination.route!!

                Log.i("NavigationManager", "Current route: $route")

                when {
                    route.contains("Map") -> toRoute<Routes.Map>()
                    route.contains("Login") -> toRoute<Routes.Login>()
                    route.contains("Register") -> toRoute<Routes.Register>()
                    route.contains("Settings") -> toRoute<Routes.Settings>()
                    route.contains("Activities") -> toRoute<Routes.Activities>()
                    route.contains("Profile") -> toRoute<Routes.Profile>()
                    route.contains("CreateProfile") -> toRoute<Routes.CreateProfile>()
                    route.contains("EditProfile") -> toRoute<Routes.EditProfile>()
                    route.contains("Introduction") -> toRoute<Routes.Introduction>()
                    route.contains("ForgotPassword") -> toRoute<Routes.ForgotPassword>()
                    route.contains("CreateEvent") -> toRoute<Routes.CreateEvent>()
                    route.contains("Event") -> toRoute<Routes.Event>()
                    route.contains("Notifications") -> toRoute<Routes.Notifications>()
                    route.contains("ChatRoom") -> toRoute<Routes.ChatRoom>()
                    route.contains("Chat") -> toRoute<Routes.Chat>()
                    else -> null
                }

            }

            SocialMeetingAppTheme(theme) {
                Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }, bottomBar = {
                    if (currentRoute is Routes.Map || currentRoute == Routes.Activities || currentRoute == Routes.Notifications || currentRoute == Routes.Chat || currentRoute == Routes.Profile(
                            user?.id ?: ""
                        )
                    ) {

                        NavigationBar(
                            currentRoute = currentRoute,
                            onItemClicked = { NavigationManager.navigateTo(it) },
                            profileImageUrl = user?.profilePictureUri ?: return@Scaffold,
                            profileID = user.id,
                            notReadNotifications = mainViewModel.notReadNotificationsCount.collectAsStateWithLifecycle().value
                        )
                    }

                }) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = startDestination ?: return@Scaffold,
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
                                mainViewModel.onIntroductionFinished()
                                NavigationManager.navigateTo(Routes.Login)
                            })
                        }

                        //////////////////////////
                        //   MAIN NAVIGATION   //
                        ////////////////////////

                        composable<Routes.Map> {
                            val viewModel = hiltViewModel<HomeViewModel>()
                            val args = it.toRoute<Routes.Map>()

                            HomeScreen(
                                events = viewModel.events.collectAsStateWithLifecycle().value,
                                isLoading = viewModel.isLoadingEvents.collectAsStateWithLifecycle().value,
                                currentLocation = viewModel.currentLocation.collectAsStateWithLifecycle().value,
                                locationCoordinates = if (args.latitude != null && args.longitude != null) LatLng(
                                    args.latitude, args.longitude
                                ) else null,
                                filters = viewModel.filters.collectAsStateWithLifecycle().value,
                                onEventClick = { NavigationManager.navigateTo(Routes.Event(it)) },
                                onFiltersApplied = viewModel::applyFilters,
                                onLocationRequested = viewModel::getLocation,
                            )

                        }

                        composable<Routes.Profile> {
                            val args = it.toRoute<Routes.Profile>()
                            val viewModel =
                                hiltViewModel<ProfileViewModel, ProfileViewModel.Factory>(
                                    creationCallback = { factory -> factory.create(args.userId) })

                            if (it.savedStateHandle.get<Boolean>("refresh") == true) {
                                viewModel.getUserByID(args.userId)
                            }

                            ProfileScreen(
                                state = viewModel.userData.collectAsStateWithLifecycle().value,
                                followers = viewModel.followers.collectAsLazyPagingItems(),
                                following = viewModel.following.collectAsLazyPagingItems(),
                                onFollowUser = { viewModel.followUser(it) },
                                onUnfollowUser = { viewModel.unfollowUser(it) },
                                onDeleteFollower = { viewModel.deleteFollower(it) },
                                onEditProfile = {
                                    NavigationManager.navigateTo(
                                        Routes.EditProfile(
                                            args.userId
                                        )
                                    )
                                },
                                onGoToSettings = { NavigationManager.navigateTo(Routes.Settings) },
                                onCardClick = { NavigationManager.navigateTo(Routes.Event(it)) })
                        }


                        composable<Routes.Activities> {
                            val viewModel = hiltViewModel<ActivitiesViewModel>()

                            ActivitiesScreen(
                                state = viewModel.state.collectAsStateWithLifecycle().value,
                                onCardClick = { NavigationManager.navigateTo(Routes.Event(it)) },
                                onCreateEventClick = {
                                    NavigationManager.navigateTo(
                                        Routes.CreateEvent(
                                            0.0, 0.0
                                        )
                                    )
                                },
                                onExploreEventClick = { NavigationManager.navigateTo(Routes.Map()) },
                                onAcceptJoinRequest = { eventID, userID ->
                                    viewModel.acceptJoinRequest(
                                        eventID, userID
                                    )
                                },
                                onDeclineJoinRequest = { eventID, userID ->
                                    viewModel.declineJoinRequest(
                                        eventID, userID
                                    )
                                })
                        }

                        composable<Routes.Chat> {
                            val viewModel = hiltViewModel<ChatRoomListViewModel>()

                            ChatRoomListScreen(
                                chatRooms = viewModel.roomPreviews.collectAsStateWithLifecycle().value,
                                onChatRoomClick = { NavigationManager.navigateTo(Routes.ChatRoom(it)) })
                        }

                        composable<Routes.ChatRoom> {
                            val args = it.toRoute<Routes.ChatRoom>()
                            val viewModel =
                                hiltViewModel<ChatRoomViewModel, ChatRoomViewModel.Factory>(
                                    creationCallback = { factory -> factory.create(args.chatRoomId) })

                            ChatRoomScreen(
                                messages = viewModel.messages.collectAsLazyPagingItems(),
                                chatRoom = viewModel.chatRoom.collectAsStateWithLifecycle().value,
                                latestMessages = viewModel.latestMessages.collectAsStateWithLifecycle().value,
                                onSendMessage = viewModel::sendMessage,
                                onBackClick = { navController.navigateUp() },
                            )
                        }

                        composable<Routes.Notifications> {
                            val viewModel = hiltViewModel<NotificationsViewModel>()

                            NotificationScreen(
                                notifications = viewModel.notifications.collectAsStateWithLifecycle(
                                    emptyList()
                                ).value,
                                onMarkAllAsRead = viewModel::markAllAsRead,
                                onMarkAsRead = viewModel::markAsRead
                            )
                        }

                        ///////////////////////////
                        //    AUTHENTICATION    //
                        /////////////////////////

                        composable<Routes.Login> {
                            val viewModel = hiltViewModel<LoginViewModel>()
                            LoginScreen(
                                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                                onSignIn = { email, password ->
                                    viewModel.signIn(
                                        email, password
                                    )
                                },
                                onSignInWithGoogle = viewModel::signInWithGoogle,
                                onGoToRegister = { NavigationManager.navigateTo(Routes.Register) },
                                onGoToForgotPassword = { NavigationManager.navigateTo(Routes.ForgotPassword) },
                                requestCredential = viewModel::requestCredential
                            )
                        }

                        composable<Routes.ForgotPassword> {
                            val viewModel = hiltViewModel<ForgotPasswordViewModel>()
                            ForgotPasswordScreen(
                                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                                onResetPassword = viewModel::resetPassword,
                                onGoToLogin = { navController.popBackStack() })
                        }

                        composable<Routes.Register> {
                            val viewModel = hiltViewModel<RegisterViewModel>()
                            RegisterScreen(
                                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                                onGoToLogin = { NavigationManager.navigateTo(Routes.Login) },
                                registerUser = viewModel::registerUser,
                                onSignUpWithGoogle = viewModel::signUpWithGoogle
                            )
                        }

                        ////////////////////
                        //    PROFILE    //
                        //////////////////

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
                                onUpdateRules = { viewModel.updateRulesAccepted() })
                        }

                        composable<Routes.EditProfile> {
                            val viewModel = hiltViewModel<EditProfileViewModel>()

                            BackHandler {
                                navController.previousBackStackEntry?.savedStateHandle?.set(
                                    "refresh", true
                                )
                                mainViewModel.refresh()
                                navController.popBackStack()
                            }

                            EditProfileScreen(
                                user = viewModel.user.collectAsStateWithLifecycle().value,
                                username = viewModel.username.collectAsStateWithLifecycle().value,
                                bio = viewModel.bio.collectAsStateWithLifecycle().value,
                                profilePictureUri = viewModel.profilePictureUri.collectAsStateWithLifecycle().value,
                                isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
                                onUpdateProfilePictureUri = viewModel::updateProfilePicture,
                                onSaveProfilePicture = viewModel::saveProfilePicture,
                                onUpdateBio = viewModel::updateBio,
                                onSaveBio = viewModel::saveBio,
                                onUpdateUsername = viewModel::updateUsername,
                                onUpdateDateOfBirth = viewModel::updateDateOfBirth,
                                onSaveUsernameAndDateOfBirth = viewModel::saveUsernameAndDateOfBirth,
                                onBack = {
                                    navController.previousBackStackEntry?.savedStateHandle?.set(
                                        "refresh", true
                                    )
                                    mainViewModel.refresh()
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable<Routes.Settings> {
                            val viewModel = hiltViewModel<SettingsViewModel>()
                            val settings = viewModel.settings.collectAsStateWithLifecycle().value

                            SettingsScreen(
                                settings = settings,
                                onBack = { navController.navigateUp() },
                                onSignOut = viewModel::signOut,
                                onThemeChange = { viewModel.updateSettings(settings.copy(theme = it)) },
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
                                uiState = viewModel.createEventUiState.collectAsStateWithLifecycle().value,
                                onNext = viewModel::nextStep,
                                onPrevious = viewModel::previousStep,
                                onUpdateCategory = viewModel::updateCategory,
                                onUpdateTitle = viewModel::updateTitle,
                                onUpdateDescription = viewModel::updateDescription,
                                onUpdateIsPrivate = viewModel::updateIsPrivate,
                                onUpdateIsOnline = viewModel::updateIsOnline,
                                onUpdateMaxParticipants = viewModel::updateMaxParticipants,
                                onSetStartTime = viewModel::setStartTime,
                                onSetEndTime = viewModel::setEndTime,
                                onUpdateLocation = viewModel::updateLocation,
                                onUpdateMeetingLink = viewModel::updateMeetingLink,
                                onChatRoomShouldCreate = viewModel::updateChatRoomShouldCreate,
                                onChatRoomNameChange = viewModel::updateChatRoomName,
                                onChatRoomAuthorOnlyWriteChange = viewModel::updateChatRoomAuthorOnlyWrite,
                                onUserSelected = {},
                                onUpdateRules = viewModel::updateRulesAccepted,
                                onCancel = { NavigationManager.navigateTo(Routes.Map()) })
                        }

                        composable<Routes.Event> { it ->
                            val args = it.toRoute<Routes.Event>()
                            val viewModel = hiltViewModel<EventViewModel, EventViewModel.Factory>(
                                creationCallback = { factory -> factory.create(args.id) })

                            if (it.savedStateHandle.get<Boolean>("refresh") == true) {
                                viewModel.refresh()
                            }

                            EventScreen(
                                state = viewModel.state.collectAsStateWithLifecycle().value,
                                isRefresh = viewModel.isRefresh.collectAsStateWithLifecycle().value,
                                onJoinEvent = { viewModel.joinEvent(args.id) },
                                onBack = { navController.popBackStack() },
                                onGoToAuthor = { NavigationManager.navigateTo(Routes.Profile(it)) },
                                onLeaveEvent = { viewModel.leaveEvent(args.id) },
                                onGoToEditEvent = {
                                    NavigationManager.navigateTo(
                                        Routes.EditEvent(
                                            args.id
                                        )
                                    )
                                },
                                onSendJoinRequest = { viewModel.sendJoinRequest(args.id) },
                            )
                        }

                        composable<Routes.EditEvent> { it ->
                            val args = it.toRoute<Routes.EditEvent>()
                            val viewModel =
                                hiltViewModel<EditEventViewModel, EditEventViewModel.Factory>(
                                    creationCallback = { factory -> factory.create(args.id) })

                            BackHandler {
                                navController.previousBackStackEntry?.savedStateHandle?.set(
                                    "refresh", true
                                )
                                navController.popBackStack()
                            }

                            EditEventScreen(
                                event = viewModel.event.collectAsStateWithLifecycle().value,
                                chatRoom = viewModel.chatRoom.collectAsStateWithLifecycle().value,
                                onBack = {
                                    navController.previousBackStackEntry?.savedStateHandle?.set(
                                        "refresh", true
                                    )
                                    navController.popBackStack()
                                },
                                onDeleteEvent = viewModel::deleteEvent,
                                newEventDescription = viewModel.newDescription.collectAsStateWithLifecycle().value,
                                newChatRoom = viewModel.newChatRoom.collectAsStateWithLifecycle().value,
                                onUpdateEventDescription = viewModel::updateEventDescription,
                                onSaveEventDescription = viewModel::saveEventDescription,
                                newMeetingLink = viewModel.newMeetingLink.collectAsStateWithLifecycle().value,
                                onUpdateMeetingLink = viewModel::updateEventMeetingLink,
                                onSaveMeetingLink = viewModel::saveEventMeetingLink,
                                onRemoveParticipant = viewModel::removeParticipant,
                                onNavigateToChatRoom = {
                                    NavigationManager.navigateTo(
                                        Routes.ChatRoom(it)
                                    )
                                },
                                onUpdateNewChatRoomName = viewModel::updateNewChatRoomName,
                                onUpdateNewChatRoomAuthorOnlyWrite = viewModel::updateNewChatRoomAuthorOnlyWrite,
                                onCreateNewChatRoom = viewModel::createChatRoom
                            )
                        }
                    }

                }


            }


        }
    }
}