package com.example.socialmeetingapp.presentation.authentication.register.createprofileflow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.socialmeetingapp.R

@Composable
fun RegisterLocationScreen(
    handleLocationPermission: ((Boolean) -> Unit) -> Unit,
    onSkip: () -> Unit
) {
    var locationPermissionGranted by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(top = 64.dp, start = 32.dp, end = 32.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.register_location_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = R.string.register_location_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )

        Button(onClick = {
            handleLocationPermission { locationPermissionGranted = it }
        }, modifier = Modifier.padding(vertical = 16.dp), enabled = !locationPermissionGranted) {
            Text(text = stringResource(id = R.string.register_location_button))
        }

        TextButton(onClick = { onSkip() }) {
            Text(text = stringResource(id = R.string.register_location_skip),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold)
        }

    }

}