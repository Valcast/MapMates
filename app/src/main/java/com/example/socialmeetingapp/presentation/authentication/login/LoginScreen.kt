package com.example.socialmeetingapp.presentation.authentication.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.socialmeetingapp.R
import com.example.socialmeetingapp.domain.model.AuthenticationState
import com.example.socialmeetingapp.presentation.authentication.components.AuthenticationError
import com.example.socialmeetingapp.presentation.authentication.components.AuthenticationSubmitButton
import com.example.socialmeetingapp.presentation.authentication.components.AuthenticationTextField
import com.example.socialmeetingapp.presentation.authentication.components.Description
import com.example.socialmeetingapp.presentation.authentication.components.ThirdPartyGoogle
import com.example.socialmeetingapp.presentation.authentication.components.Title
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    innerPadding: PaddingValues,
    navigateToMap: () -> Unit,
    navigateToRegister: () -> Unit,
    navigateToForgotPassword: () -> Unit
) {
    val viewModel = hiltViewModel<LoginViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle().value

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(state) {
        if (state is AuthenticationState.Success) {
            navigateToMap()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(top = 64.dp, start = 32.dp, end = 32.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Title(stringResource = R.string.login_title)
        Description(
            stringResource = R.string.login_description,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )
        
        if (state is AuthenticationState.Error) {
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
                onClickListener = {
                    coroutineScope.launch {
                        viewModel.loginUser(email, password)
                    }
                },
                enabled = state !is AuthenticationState.Loading,
                isLoading = state is AuthenticationState.Loading,
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