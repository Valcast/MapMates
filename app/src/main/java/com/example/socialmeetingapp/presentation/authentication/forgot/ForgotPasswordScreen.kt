package com.example.socialmeetingapp.presentation.authentication.forgot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.socialmeetingapp.R
import com.example.socialmeetingapp.domain.model.AuthenticationState
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(innerPadding: PaddingValues, navigateToLogin: () -> Unit) {

    val viewModel = hiltViewModel<ForgotPasswordViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle().value

    var email by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp)
            .padding(innerPadding), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.forgot_password_header),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = R.string.forgot_password_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "E-mail", style = MaterialTheme.typography.labelSmall) })

        Button(onClick = {
            coroutineScope.launch {
                viewModel.resetPassword(email)
            }
        }, enabled = state !is AuthenticationState.Loading, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
            if (state is AuthenticationState.Loading) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = stringResource(id = R.string.send_link_button),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        TextButton(onClick = { navigateToLogin() }) {
            Text(
                text = stringResource(id = R.string.back_to_login_button),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(0.5f)
            )
        }


    }
}