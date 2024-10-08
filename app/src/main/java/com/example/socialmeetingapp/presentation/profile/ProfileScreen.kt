package com.example.socialmeetingapp.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.socialmeetingapp.R
import com.example.socialmeetingapp.presentation.authentication.components.Title

@Composable
fun ProfileScreen(innerPadding: PaddingValues, navigateToLogin: () -> Unit) {
    val viewModel = hiltViewModel<ProfileViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier.padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            viewModel.logout()
            navigateToLogin()
        }) {
            Text(text = "Logout")
        }
    }

}