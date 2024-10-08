package com.example.socialmeetingapp.presentation.authentication.register.locationinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.socialmeetingapp.R
import com.example.socialmeetingapp.presentation.authentication.components.Description
import com.example.socialmeetingapp.presentation.authentication.components.Title

@Composable
fun RegisterLocationScreen(
    innerPadding: PaddingValues,
    handleLocationPermission: ((Boolean) -> Unit) -> Unit,
    navigateToMap: () -> Unit
) {
    var locationPermissionGranted by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        handleLocationPermission { locationPermissionGranted = it }
    }

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(top = 64.dp, start = 32.dp, end = 32.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Title(stringResource = R.string.register_location_title)
        Description(
            stringResource = R.string.register_location_description,
            modifier = Modifier.padding(top = 8.dp)
        )

        Button(onClick = {
            handleLocationPermission { locationPermissionGranted = it }
        }, modifier = Modifier.padding(vertical = 16.dp), enabled = !locationPermissionGranted) {
            Text(text = stringResource(id = R.string.register_location_button))
        }

        if (!locationPermissionGranted) {
            Text(text = stringResource(id = R.string.register_location_permission_not_granted),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error)
        }

        TextButton(onClick = { navigateToMap() }) {
            Text(text = stringResource(id = R.string.register_location_skip),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold)
        }

    }

}