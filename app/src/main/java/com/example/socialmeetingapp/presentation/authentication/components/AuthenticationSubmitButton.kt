package com.example.socialmeetingapp.presentation.authentication.components

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun AuthenticationSubmitButton(
    onClickListener: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
    textStringResource: Int,
    modifier: Modifier = Modifier
) {
    Button(onClick = onClickListener, enabled = enabled, modifier = modifier) {
        if (isLoading) {
            CircularProgressIndicator( )
        } else {
            Text(
                text = stringResource(id = textStringResource),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}