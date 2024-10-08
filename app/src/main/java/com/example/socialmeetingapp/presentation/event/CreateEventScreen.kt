package com.example.socialmeetingapp.presentation.event

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun CreateEventScreen() {

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            val title = rememberTextFieldState()
            val description = rememberTextFieldState()
            val location = rememberTextFieldState()
            val date = rememberTextFieldState()
            val time = rememberTextFieldState()
        }
    }



}