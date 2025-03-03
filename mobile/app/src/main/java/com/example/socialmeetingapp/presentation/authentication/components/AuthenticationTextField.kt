package com.example.socialmeetingapp.presentation.authentication.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation


@Composable
fun AuthenticationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelStringResource: Int,
    modifier: Modifier = Modifier,
    isSensitiveData: Boolean = false,
    interactionSource: MutableInteractionSource? = null
) {
    var isSensitiveDataVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        label = {
            Text(
                text = stringResource(id = labelStringResource),
                style = MaterialTheme.typography.labelSmall
            )
        },
        visualTransformation = if (isSensitiveData) {
            if (isSensitiveDataVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            }
        } else {
            VisualTransformation.None
        },
        trailingIcon = {
            if (!isSensitiveData) {
                return@OutlinedTextField
            }

            IconButton(onClick = { isSensitiveDataVisible = !isSensitiveDataVisible }) {
                Icon(
                    imageVector = if (isSensitiveDataVisible) Icons.Outlined.Close else Icons.Outlined.Search,
                    contentDescription = "Close"
                )
            }
        },
        interactionSource = interactionSource,
        modifier = modifier
    )
}