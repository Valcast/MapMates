package mapmates.feature.account.impl.ui.createaccount

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mapmates.feature.account.impl.ui.createaccount.stages.CreateAccountAdditionalScreen
import mapmates.feature.account.impl.ui.createaccount.stages.CreateAccountInfoScreen
import mapmates.feature.account.impl.ui.createaccount.stages.CreateAccountPictureScreen
import mapmates.feature.account.impl.ui.createaccount.stages.CreateAccountRulesScreen
import mapmates.feature.account.impl.R as AccountR

@Composable
internal fun CreateAccountScreen(
    viewModel: CreateAccountViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .padding(horizontal = 40.dp, vertical = 16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

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
                        onUpdateUsername = viewModel::onUpdateUsername,
                        onUpdateBio = viewModel::onUpdateBio,
                    )
                }

                CreateAccountUiState.PICTURE -> {
                    CreateAccountPictureScreen(
                        state = state,
                        onUpdateProfilePicture = viewModel::onUpdateProfilePicture,
                    )
                }

                CreateAccountUiState.ADDITIONAL -> {
                    CreateAccountAdditionalScreen(
                        state = state,
                        onUpdateDateOfBirth = viewModel::onUpdateDateOfBirth,
                        onUpdateGender = viewModel::onUpdateGender,
                    )
                }

                CreateAccountUiState.RULES -> {
                    CreateAccountRulesScreen(
                        state = state,
                        onUpdateRules = viewModel::onUpdateRules,
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
        ) {
            if (state.uiState != CreateAccountUiState.INFO) {
                OutlinedButton(
                    onClick = viewModel::onPrevious,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(end = 10.dp)
                ) {
                    Text(text = stringResource(AccountR.string.account_previous_button))
                }
            }

            Button(
                onClick = viewModel::onNext,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                enabled = state.isNextButtonEnabled,
            ) {
                Text(
                    text = if (state.uiState == CreateAccountUiState.RULES) {
                        stringResource(AccountR.string.account_submit_button)
                    } else stringResource(AccountR.string.account_next_button)
                )
            }
        }
    }
}

