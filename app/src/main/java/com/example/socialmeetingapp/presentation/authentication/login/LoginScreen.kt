package com.example.socialmeetingapp.presentation.authentication.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.socialmeetingapp.R
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.presentation.authentication.components.AuthenticationError
import com.example.socialmeetingapp.presentation.authentication.components.AuthenticationSubmitButton
import com.example.socialmeetingapp.presentation.authentication.components.AuthenticationTextField
import com.example.socialmeetingapp.presentation.authentication.components.Description
import com.example.socialmeetingapp.presentation.authentication.components.ThirdPartyGoogle
import com.example.socialmeetingapp.presentation.authentication.components.Title

@Composable
fun LoginScreen(
    navigateToMap: () -> Unit,
    navigateToRegister: () -> Unit,
    navigateToForgotPassword: () -> Unit
) {
    val viewModel = hiltViewModel<LoginViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle().value

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is Result.Success) {
            navigateToMap()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp, start = 32.dp, end = 32.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Title(stringResource = R.string.login_title)
        Description(
            stringResource = R.string.login_description,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { navigateToForgotPassword() }) {
                Text(
                    text = stringResource(id = R.string.login_forgot_password_button),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )

            }

            AuthenticationSubmitButton(
                onClickListener = { viewModel.login(email, password) },
                enabled = state !is Result.Loading,
                isLoading = state is Result.Loading,
                textStringResource = R.string.login_button
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .width(120.dp)
        )

        ThirdPartyGoogle()

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(id = R.string.login_no_account),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )

        TextButton(
            onClick = { navigateToRegister() }
        ) {
            Text(
                text = stringResource(id = R.string.login_register_button),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }

}