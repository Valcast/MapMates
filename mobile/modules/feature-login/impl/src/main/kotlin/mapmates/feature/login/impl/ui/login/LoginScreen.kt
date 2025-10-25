package mapmates.feature.login.impl.ui.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.merge
import mapmates.feature.login.impl.R as LoginR

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val emailTextFieldState = rememberTextFieldState()
    val emailInteractionSource = remember { MutableInteractionSource() }

    val passwordTextFieldState = rememberTextFieldState()
    val passwordInteractionSource = remember { MutableInteractionSource() }

    LaunchedEffect(emailInteractionSource, passwordInteractionSource) {
        merge(
            emailInteractionSource.interactions,
            passwordInteractionSource.interactions
        ).collect { interaction ->
            if (interaction is FocusInteraction.Focus && state.showCredentialManager) {
                viewModel.requestCredential()
            }
        }
    }

    val baseWeights = listOf(2f, 1f, 0.4f)
    val expandedWeight = 3.2f
    val collapsedWeight = 0.6f

    val imageIds = listOf(
        LoginR.drawable.image1,
        LoginR.drawable.image2,
        LoginR.drawable.image3
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            imageIds.forEachIndexed { index, imageId ->
                val targetWeight = when (state.selectedWelcomeImageIndex) {
                    null -> baseWeights[index]
                    index -> expandedWeight
                    else -> collapsedWeight
                }
                val animatedWeight by animateFloatAsState(
                    targetValue = targetWeight,
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "weight$index"
                )

                Image(
                    painter = painterResource(imageId),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(animatedWeight)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    viewModel.onSelectedWelcomeImage(index)
                                    try {
                                        tryAwaitRelease()
                                    } finally {
                                        viewModel.onSelectedWelcomeImage(null)
                                    }
                                }
                            )
                        }

                )
            }
        }

        AnimatedContent(
            targetState = state.selectedWelcomeImageIndex,
            transitionSpec = {
                fadeIn().togetherWith(fadeOut())
            },
            label = "TextSwitch",
            modifier = Modifier.padding(vertical = 16.dp)
        ) { targetSelected ->
            Text(
                text = when (targetSelected) {
                    0 -> stringResource(id = LoginR.string.image1_description)
                    1 -> stringResource(id = LoginR.string.image2_description)
                    2 -> stringResource(id = LoginR.string.image3_description)
                    else -> stringResource(id = LoginR.string.welcome_title)
                },
                style = MaterialTheme.typography.headlineLargeEmphasized,
                minLines = 2,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        OutlinedTextField(
            state = emailTextFieldState,
            placeholder = {
                Text(
                    text = stringResource(id = LoginR.string.email_placeholder),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            },
            shape = MaterialTheme.shapes.medium,
            interactionSource = emailInteractionSource,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedSecureTextField(
            state = passwordTextFieldState,
            placeholder = {
                Text(
                    text = stringResource(id = LoginR.string.password_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            },
            shape = MaterialTheme.shapes.medium,
            interactionSource = passwordInteractionSource,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = viewModel::onGoogleLogin,
                shapes = ButtonShapes(
                    shape = MaterialTheme.shapes.extraLarge,
                    pressedShape = MaterialTheme.shapes.small
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(LoginR.drawable.google),
                        contentDescription = "Google icon",
                        tint = Color.Unspecified
                    )
                    Text(
                        text = stringResource(LoginR.string.login_google),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }

            Button(
                onClick = {
                    viewModel.onSignIn(
                        email = emailTextFieldState.text.toString(),
                        password = passwordTextFieldState.text.toString()
                    )
                },
                enabled = !state.isLoading,
                shapes = ButtonShapes(
                    shape = MaterialTheme.shapes.extraLarge,
                    pressedShape = MaterialTheme.shapes.small
                ),
                modifier = Modifier.weight(0.5f)
            ) {
                if (state.isLoading) {
                    CircularWavyProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = stringResource(id = LoginR.string.login_button),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

            }
        }

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = viewModel::onForgotPassword) {
                Text(
                    text = stringResource(id = LoginR.string.login_forgot_password_button),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

            }

            TextButton(onClick = viewModel::onRegister) {
                Text(
                    text = stringResource(id = LoginR.string.login_no_account),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}