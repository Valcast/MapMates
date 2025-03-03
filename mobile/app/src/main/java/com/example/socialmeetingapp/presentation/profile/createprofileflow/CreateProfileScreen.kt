package com.example.socialmeetingapp.presentation.profile.createprofileflow

import android.net.Uri
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.socialmeetingapp.R
import com.example.socialmeetingapp.domain.model.User
import kotlinx.datetime.LocalDateTime

@Composable
fun CreateProfileScreen(
    user: User,
    uiState: CreateProfileFlow,
    isRulesAccepted: Boolean,
    isNextButtonEnabled: Boolean,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onUpdateProfilePicture: (Uri) -> Unit,
    onUpdateUsername: (String) -> Unit,
    onUpdateBio: (String) -> Unit,
    onUpdateDateOfBirth: (LocalDateTime) -> Unit,
    onUpdateGender: (String) -> Unit,
    onUpdateRules: () -> Unit
)
{
    Column(
        modifier = Modifier
            .padding(horizontal = 40.dp, vertical = 16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        AnimatedContent(targetState = uiState, label = "", modifier = Modifier.weight(1f),
            transitionSpec = {
                (fadeIn() + slideInHorizontally(
                    animationSpec = tween(400),
                    initialOffsetX = { fullWidth -> fullWidth })).togetherWith(
                    fadeOut(animationSpec = tween(200))
                )
            }) { uiState ->
            when (uiState) {
                CreateProfileFlow.ProfileInfo -> {
                    CreateProfileInfoScreen(
                        user = user,
                        onUpdateUsername = onUpdateUsername,
                        onUpdateBio = onUpdateBio
                    )
                }
                CreateProfileFlow.ProfilePicture -> {
                    CreateProfilePictureScreen(
                        user = user,
                        onUpdateProfilePicture = onUpdateProfilePicture
                    )
                }

                CreateProfileFlow.Additional -> {
                    CreateProfileAdditional(
                        user = user,
                        onUpdateDateOfBirth = onUpdateDateOfBirth,
                        onUpdateGender = onUpdateGender
                    )
                }

                CreateProfileFlow.Rules -> {
                    CreateProfileRulesScreen(
                        isRulesAccepted = isRulesAccepted,
                        onUpdateRules = onUpdateRules
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
        ) {
            if (uiState != CreateProfileFlow.ProfileInfo) {
                OutlinedButton(
                    onClick = onPrevious,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(end = 10.dp)
                ) {
                    Text(text = stringResource(R.string.previous))
                }
            }

            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                enabled = isNextButtonEnabled
            ) {
                Text(text = if (uiState == CreateProfileFlow.Rules) stringResource(R.string.create_profile_submit) else stringResource(
                    R.string.next
                )
                )
            }
        }
    }
}

