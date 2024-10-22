package com.example.socialmeetingapp.presentation.event.createevent

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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.presentation.components.DashedProgressIndicator
import com.google.android.gms.maps.model.LatLng


@Composable
fun CreateEventScreen(
    latitude: Double,
    longtitude: Double,
    navigateToMap: () -> Unit
) {

    val viewModel = hiltViewModel<CreateEventViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle().value

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isNextButtonEnabled by viewModel.isNextButtonEnabled.collectAsStateWithLifecycle()


    LaunchedEffect(state) {
        if (state is Result.Success) {
            navigateToMap()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.updateLocation(LatLng(latitude, longtitude))
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 40.dp, vertical = 16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = navigateToMap, shape = RoundedCornerShape(10.dp)) {
                Text(text = "Cancel")
            }
            DashedProgressIndicator(
                modifier = Modifier
                    .padding(end = 40.dp)
                    .fillMaxWidth(0.5f),
                progress = uiState.toInt(),
                totalNumberOfBars = 2
            )
        }

        AnimatedContent(targetState = uiState, label = "", modifier = Modifier.weight(1f),
            transitionSpec = {
                (fadeIn() + slideInHorizontally(
                    animationSpec = tween(400),
                    initialOffsetX = { fullWidth -> fullWidth })).togetherWith(
                    fadeOut(animationSpec = tween(200))
                )
            }) { uiState ->
            when (uiState) {
                CreateEventFlow.Info -> EventInfoScreen()
                CreateEventFlow.Time -> EventDateScreen()
                CreateEventFlow.Location -> EventLocationScreen()
                CreateEventFlow.Rules -> EventRulesScreen()
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
        ) {
            if (uiState != CreateEventFlow.Info) {
                OutlinedButton(
                    onClick = viewModel::previousStep,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(end = 10.dp)
                ) {
                    Text(text = "Previous")
                }
            }

            Button(
                onClick = viewModel::nextStep,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                enabled = isNextButtonEnabled
            ) {
                Text(text = if (uiState == CreateEventFlow.Rules) "Create" else "Next")
            }
        }
    }
}

