package com.example.socialmeetingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.SocialMeetingAppTheme
import com.example.socialmeetingapp.domain.model.navigation.Routes
import com.example.socialmeetingapp.presentation.PermissionManager
import com.example.socialmeetingapp.presentation.authentication.login.LoginScreen
import com.example.socialmeetingapp.presentation.authentication.register.RegisterScreen
import com.example.socialmeetingapp.presentation.map.MapScreen
import com.example.socialmeetingapp.presentation.navigation.NavigationBar
import com.example.socialmeetingapp.presentation.profile.ProfileScreen
import com.example.socialmeetingapp.presentation.settings.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionManager = PermissionManager(this)


        permissionManager.checkPermissions(
            PermissionManager.FINE_LOCATION_PERMISSION
        ) {
            if (it) {
                // Permissions granted
            } else {
                // Permissions denied

        }

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val viewModel = hiltViewModel<MainViewModel>()


            var currentRoute by remember { mutableStateOf<Routes>(Routes.Map) }

            val state = viewModel.state.collectAsStateWithLifecycle().value

            SocialMeetingAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = { NavigationBar(modifier = Modifier, selected = if (currentRoute in listOf(
                            Routes.Login,
                            Routes.Register
                        )
                    ) null else viewModel.selectedNavItem.intValue,
                        onItemSelected = {
                            viewModel.onItemSelected(it)

                            navController.navigate(viewModel.routes[it]) {
                                popUpTo(navController.graph.id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }) }) { innerPadding ->
                    NavHost(navController = navController, startDestination = Routes.Map) {
                            composable<Routes.Map> {
                                currentRoute = Routes.Map
                                MapScreen(innerPadding = innerPadding) }
                            composable<Routes.Login>{
                                currentRoute = Routes.Login
                                LoginScreen(innerPadding = innerPadding) }
                            composable<Routes.Register> {
                                currentRoute = Routes.Register
                                RegisterScreen(innerPadding = innerPadding) }
                            composable<Routes.Settings> {
                                currentRoute = Routes.Settings
                                SettingsScreen(innerPadding = innerPadding) }
                            composable<Routes.Profile>{
                                if (state is MainState.LoggedOut) {
                                    navController.navigate(Routes.Login) {
                                        popUpTo(navController.graph.id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }

                                currentRoute = Routes.Profile
                                ProfileScreen(innerPadding = innerPadding) }
                        }
                    }
                }
            }
        }
    }
}







