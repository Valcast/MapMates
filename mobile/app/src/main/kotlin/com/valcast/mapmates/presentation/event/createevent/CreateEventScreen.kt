package com.valcast.mapmates.presentation.event.createevent

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.valcast.mapmates.domain.model.Category
import com.google.android.gms.maps.model.LatLng
import kotlinx.datetime.LocalDateTime


@Composable
fun CreateEventScreen(
    uiState: CreateEventUiState,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onUpdateCategory: (Category) -> Unit,
    onUpdateTitle: (String) -> Unit,
    onUpdateDescription: (String) -> Unit,
    onUpdateIsPrivate: (Boolean) -> Unit,
    onUpdateIsOnline: (Boolean) -> Unit,
    onUpdateMaxParticipants: (Int) -> Unit,
    onSetStartTime: (LocalDateTime) -> Unit,
    onSetEndTime: (LocalDateTime) -> Unit,
    onUpdateLocation: (LatLng) -> Unit,
    onUpdateMeetingLink: (String) -> Unit,
    onChatRoomShouldCreate: (Boolean) -> Unit,
    onChatRoomNameChange: (String) -> Unit,
    onChatRoomAuthorOnlyWriteChange: (Boolean) -> Unit,
    onUserSelected: (String) -> Unit,
    onUpdateRules: () -> Unit,
    onCancel: () -> Unit
) {
    var isForward by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        TextButton(
            onClick = onCancel, shape = RoundedCornerShape(10.dp), modifier = Modifier.align(
                Alignment.Start
            )
        ) {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        CreateEventProgressBar(uiState.createEventFlowState)

        AnimatedContent(
            targetState = uiState.createEventFlowState, label = "", modifier = Modifier.weight(1f),
            transitionSpec = {
                if (isForward) {
                    (fadeIn() + slideInHorizontally(
                        animationSpec = tween(200),
                        initialOffsetX = { fullWidth -> fullWidth })).togetherWith(
                        fadeOut(animationSpec = tween(200))
                    )
                } else {
                    (fadeIn() + slideInHorizontally(
                        animationSpec = tween(200),
                        initialOffsetX = { fullWidth -> -fullWidth })).togetherWith(
                        fadeOut(animationSpec = tween(200))
                    )
                }
            }) { createEventFlow ->
            when (createEventFlow) {

                CreateEventFlow.INFO -> EventInfoScreen(
                    event = uiState.event,
                    onUpdateTitle = onUpdateTitle,
                    onUpdateDescription = onUpdateDescription,
                    onUpdateIsPrivate = onUpdateIsPrivate,
                    onUpdateMaxParticipants = onUpdateMaxParticipants,
                )

                CreateEventFlow.TIME -> EventDateScreen(
                    event = uiState.event,
                    onSetStartTime = onSetStartTime,
                    onSetEndTime = onSetEndTime
                )

                CreateEventFlow.LOCATION -> EventLocationScreen(
                    event = uiState.event,
                    onUpdateLocation = onUpdateLocation,
                    onUpdateIsOnline = onUpdateIsOnline,
                    onUpdateMeetingLink = onUpdateMeetingLink,
                )

                CreateEventFlow.CHAT -> EventChatScreen(
                    chatRoom = uiState.chatRoom,
                    onChatRoomShouldCreate = onChatRoomShouldCreate,
                    onChatRoomNameChange = onChatRoomNameChange,
                    onChatRoomAuthorOnlyWriteChange = onChatRoomAuthorOnlyWriteChange
                )

                CreateEventFlow.RULES -> EventRulesScreen(
                    isRulesAccepted = uiState.isRulesAccepted,
                    onUpdateRules = onUpdateRules
                )

                CreateEventFlow.CATEGORY -> EventCategoryScreen(
                    selectedCategory = uiState.event.category,
                    onUpdateCategory = onUpdateCategory
                )

                CreateEventFlow.INVITE -> EventInviteScreen(
                    followersAndFollowing = uiState.followersAndFollowing,
                    onUserSelected = onUserSelected
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
        ) {
            AnimatedVisibility(
                visible = uiState.createEventFlowState != CreateEventFlow.CATEGORY,
            ) {
                OutlinedButton(
                    onClick = {
                        isForward = false
                        onPrevious()
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(end = 10.dp)
                ) {
                    Text(text = "Previous")
                }
            }
            Button(
                onClick = {
                    isForward = true
                    onNext()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                enabled = uiState.isNextButtonEnabled
            ) {
                Text(text = if (uiState.createEventFlowState == CreateEventFlow.RULES) "Create" else "Next")
            }
        }
    }
}

@Composable
fun CreateEventProgressBar(currentStep: CreateEventFlow) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp - 16.dp
    val itemWidth = (screenWidth / 2) - 8.dp
    val density = LocalDensity.current

    val scrollState = rememberScrollState()

    LaunchedEffect(currentStep) {
        val index = CreateEventFlow.entries.indexOf(currentStep)
        val scrollPosition = with(density) { (index * itemWidth.toPx()).toInt() }
        scrollState.animateScrollTo(scrollPosition)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(vertical = 16.dp)
    ) {
        CreateEventFlow.entries.forEach { step ->
            Column(
                modifier = Modifier
                    .width(itemWidth)
                    .padding(horizontal = 3.dp)
            ) {
                HorizontalDivider(
                    thickness = 4.dp,
                    color = if (step == currentStep) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(
                        0.5f
                    ),
                )
                Text(
                    text = step.name.lowercase().replaceFirstChar { it.uppercase() },
                    color = if (step == currentStep) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(
                        0.5f
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

        }
    }
}

