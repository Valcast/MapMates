package com.example.socialmeetingapp.presentation.authentication.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.presentation.authentication.components.AuthenticationError
import com.example.socialmeetingapp.presentation.authentication.components.AuthenticationTextField
import com.example.socialmeetingapp.presentation.authentication.components.ThirdPartyGoogle
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    navigateToLogin: () -> Unit,
    navigateToRegisterProfileInfo: () -> Unit
) {
    val viewModel = hiltViewModel<RegisterViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle().value

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(state) {
        if (state is Result.Success) {
            navigateToRegisterProfileInfo()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp, start = 32.dp, end = 32.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(id = R.string.register_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(id = R.string.register_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
        )

        if (state is Result.Error) {
            AuthenticationError(message = state.message)
        }


        AuthenticationTextField(
            value = email,
            onValueChange = { email = it },
            labelStringResource = R.string.email_hint
        )
        AuthenticationTextField(
            value = password,
            onValueChange = { password = it },
            labelStringResource = R.string.password_hint,
            isSensitiveData = true
        )
        AuthenticationTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            labelStringResource = R.string.register_confirm_password_hint,
            isSensitiveData = true
        )
        

        Button(
            onClick = {
                coroutineScope.launch {
                    viewModel.registerUser(email, password, confirmPassword)
                }
            },
            enabled = state !is Result.Loading,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            if (state is Result.Loading) {
                CircularProgressIndicator()
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(id = R.string.register_button),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Play Arrow"
                    )
                }

            }
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .width(120.dp)
        )

        ThirdPartyGoogle()

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(id = R.string.already_have_account),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )

        TextButton(onClick = { navigateToLogin() }) {
            Text(
                text = stringResource(id = R.string.login_here),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

        }
    }
}
