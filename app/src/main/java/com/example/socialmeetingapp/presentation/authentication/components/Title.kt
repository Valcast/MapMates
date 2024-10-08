package com.example.socialmeetingapp.presentation.authentication.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight

@Composable
fun Title(stringResource: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = stringResource),
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}