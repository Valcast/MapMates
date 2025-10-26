package mapmates.feature.login.impl.ui.forgotpassword

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mapmates.feature.login.impl.R as LoginR

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        IconButton(
            onClick = viewModel::onBack
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(64.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.large
                    )
                    .padding(16.dp)
            )
            Text(
                text = stringResource(id = LoginR.string.forgot_password_title),
                style = MaterialTheme.typography.displayLargeEmphasized,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
            )
            AnimatedContent(
                targetState = state.resultMessageResId,
                transitionSpec = {
                    fadeIn().togetherWith(fadeOut())
                },
                label = "TextSwitch",
            ) { targetState ->
                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    Text(
                        text = when (targetState) {
                            null -> stringResource(id = LoginR.string.login_forgot_password_description)
                            else -> stringResource(state.resultMessageResId!!)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = when (targetState) {
                            null -> MaterialTheme.colorScheme.onBackground
                            else -> MaterialTheme.colorScheme.error
                        },
                        minLines = 2,
                    )
                }
            }

            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChanged,
                shape = MaterialTheme.shapes.medium,
                label = {
                    Text(
                        text = stringResource(LoginR.string.email_placeholder),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Email, contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = viewModel::onResetPassword,
                shapes = ButtonShapes(
                    shape = MaterialTheme.shapes.extraLarge,
                    pressedShape = MaterialTheme.shapes.medium
                ),
                enabled = !state.isLoading && state.timerSecondsLeft == 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            ) {
                if (state.isLoading) {
                    CircularWavyProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = if (state.timerSecondsLeft > 0) state.timerSecondsLeft.toString()
                        else stringResource(id = LoginR.string.forgot_password_button),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
    }
}