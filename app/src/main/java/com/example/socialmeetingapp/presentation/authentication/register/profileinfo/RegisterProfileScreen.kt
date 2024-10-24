package com.example.socialmeetingapp.presentation.authentication.register.profileinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.presentation.authentication.components.AuthenticationTextField

@Composable
fun RegisterProfileScreen(state: Result<Unit>, onNextClick: (String, String) -> Unit) {

    var name by rememberSaveable { mutableStateOf("") }
    var bio by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(top = 64.dp, start = 32.dp, end = 32.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(id = R.string.register_profile_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(id = R.string.register_profile_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
        
        if (state is Result.Error) {
            Text(
                text = state.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        AuthenticationTextField(value = name, onValueChange = {name = it}, labelStringResource = R.string.profile_name_hint)

        Text(
            text = stringResource(id = R.string.profile_bio_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )

        AuthenticationTextField(value = bio, onValueChange = {bio = it}, labelStringResource = R.string.profile_bio_hint)

        Button(onClick = { onNextClick(name, bio) }, enabled = state !is Result.Loading, modifier = Modifier.padding(top = 16.dp)) {
            if (state is Result.Loading) {
                CircularProgressIndicator()
            } else {
                Text(text = stringResource(id = R.string.next_button))
            }
        }
    }
}