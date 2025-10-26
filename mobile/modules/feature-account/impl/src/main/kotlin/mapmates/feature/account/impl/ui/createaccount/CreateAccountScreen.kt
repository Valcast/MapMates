package mapmates.feature.account.impl.ui.createaccount

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import mapmates.core.ui.MapMatesPreviewTheme
import mapmates.core.ui.ThemePreviews
import mapmates.core.ui.components.DashedProgressIndicator
import mapmates.feature.account.impl.ui.createaccount.stages.CreateAccountAdditionalScreen
import mapmates.feature.account.impl.ui.createaccount.stages.CreateAccountInfoScreen
import mapmates.feature.account.impl.ui.createaccount.stages.CreateAccountPictureScreen
import mapmates.feature.account.impl.ui.createaccount.stages.CreateAccountRulesScreen
import kotlin.math.max
import mapmates.feature.account.impl.R as AccountR

@Composable
internal fun CreateAccountScreen(
    viewModel: CreateAccountViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    CreateAccountScreenUi(
        state = state,
        onUpdateFirstName = viewModel::onUpdateFirstName,
        onUpdateMiddleName = viewModel::onUpdateMiddleName,
        onUpdateLastName = viewModel::onUpdateLastName,
        onUpdateBio = viewModel::onUpdateBio,
        onAddPicture = viewModel::onAddPicture,
        onDeletePicture = viewModel::onDeletePicture,
        onPreviewPicture = viewModel::onPreviewPicture,
        onUpdateDateOfBirth = viewModel::onUpdateDateOfBirth,
        onUpdateGender = viewModel::onUpdateGender,
        onUpdatePrivacyPolicy = viewModel::onUpdatePrivacyPolicy,
        onUpdateTermsOfService = viewModel::onUpdateTermsOfService,
        onPrevious = viewModel::onPrevious,
        onNext = viewModel::onNext
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CreateAccountScreenUi(
    state: CreateAccountState,
    onUpdateFirstName: (String) -> Unit = {},
    onUpdateMiddleName: (String) -> Unit = {},
    onUpdateLastName: (String) -> Unit = {},
    onUpdateBio: (String) -> Unit = {},
    onAddPicture: (Uri) -> Unit = {},
    onDeletePicture: () -> Unit = {},
    onPreviewPicture: (Uri) -> Unit = {},
    onUpdateDateOfBirth: (LocalDate) -> Unit = {},
    onUpdateGender: (Gender) -> Unit = {},
    onUpdatePrivacyPolicy: () -> Unit = {},
    onUpdateTermsOfService: () -> Unit = {},
    onPrevious: () -> Unit = {},
    onNext: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .padding(top = 24.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        DashedProgressIndicator(
            progress = when (state.uiState) {
                CreateAccountUiState.INFO -> 1
                CreateAccountUiState.PICTURE -> 2
                CreateAccountUiState.ADDITIONAL -> 3
                CreateAccountUiState.RULES -> 4
            },
            totalNumberOfBars = 4,
            gapWidth = 4.dp,
            activeStrokeWidth = 8.dp,
            inactiveStrokeWidth = 6.dp,
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedContent(
            targetState = state.uiState,
            label = "CreateAccountFlow",
            modifier = Modifier.weight(1f),
            transitionSpec = {
                (fadeIn() + slideInHorizontally(
                    animationSpec = tween(400),
                    initialOffsetX = { fullWidth -> fullWidth })).togetherWith(
                    fadeOut(animationSpec = tween(200))
                )
            }) { uiState ->
            when (uiState) {
                CreateAccountUiState.INFO -> {
                    CreateAccountInfoScreen(
                        state = state,
                        onUpdateFirstName = onUpdateFirstName,
                        onUpdateMiddleName = onUpdateMiddleName,
                        onUpdateLastName = onUpdateLastName,
                        onUpdateBio = onUpdateBio,
                    )
                }

                CreateAccountUiState.PICTURE -> {
                    CreateAccountPictureScreen(
                        state = state,
                        onAddPicture = onAddPicture,
                        onDeletePicture = onDeletePicture,
                        onPreviewPicture = onPreviewPicture
                    )
                }

                CreateAccountUiState.ADDITIONAL -> {
                    CreateAccountAdditionalScreen(
                        state = state,
                        onUpdateDateOfBirth = onUpdateDateOfBirth,
                        onUpdateGender = onUpdateGender,
                    )
                }

                CreateAccountUiState.RULES -> {
                    CreateAccountRulesScreen(
                        state = state,
                        onUpdatePrivacyPolicy = onUpdatePrivacyPolicy,
                        onUpdateTermsOfService = onUpdateTermsOfService
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
        ) {
            AnimatedContent(
                targetState = state.uiState, transitionSpec = {
                    fadeIn().togetherWith(fadeOut())
                }, label = "PreviousButtonVisibility"
            ) { uiState ->
                if (uiState != CreateAccountUiState.INFO) {
                    OutlinedButton(
                        onClick = onPrevious, shapes = ButtonShapes(
                            shape = MaterialTheme.shapes.extraLarge,
                            pressedShape = MaterialTheme.shapes.medium
                        ), modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(end = 10.dp)
                    ) {
                        Text(
                            text = stringResource(AccountR.string.account_previous_button),
                            style = MaterialTheme.typography.titleMediumEmphasized,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
            }

            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                shapes = ButtonShapes(
                    shape = MaterialTheme.shapes.extraLarge,
                    pressedShape = MaterialTheme.shapes.medium
                ),
                enabled = state.isNextButtonEnabled && !state.isLoading,
            ) {
                if (state.isLoading){
                    CircularWavyProgressIndicator()
                } else {
                    Text(
                        text = if (state.uiState == CreateAccountUiState.RULES) {
                            stringResource(AccountR.string.account_submit_button)
                        } else stringResource(AccountR.string.account_next_button),
                        style = MaterialTheme.typography.titleMediumEmphasized,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun CreateAccountScreenUiPreview() = MapMatesPreviewTheme {

    var state by remember {
        mutableStateOf(
            CreateAccountState(
                uiState = CreateAccountUiState.INFO, isNextButtonEnabled = true
            )
        )
    }

    CreateAccountScreenUi(state = state, onPrevious = {
        state = state.copy(uiState = state.uiState.previous())
    }, onNext = {
        state = state.copy(uiState = state.uiState.next())
    })
}

@ThemePreviews
@Composable
private fun CreateAccountScreenPictureUiPreview() = MapMatesPreviewTheme {
    CreateAccountScreenUi(
        state = CreateAccountState(
            uiState = CreateAccountUiState.PICTURE
        ),
    )
}

@ThemePreviews
@Composable
private fun CreateAccountScreenAdditionalUiPreview() = MapMatesPreviewTheme {
    CreateAccountScreenUi(
        state = CreateAccountState(
            uiState = CreateAccountUiState.ADDITIONAL,
            gender = Gender.MAN
        ),
    )
}

@ThemePreviews
@Composable
private fun CreateAccountScreenRulesUiPreview() = MapMatesPreviewTheme {
    CreateAccountScreenUi(
        state = CreateAccountState(
            uiState = CreateAccountUiState.RULES,
            isPrivacyPolicyAccepted = true,
            isLoading = true
        ),
    )
}

