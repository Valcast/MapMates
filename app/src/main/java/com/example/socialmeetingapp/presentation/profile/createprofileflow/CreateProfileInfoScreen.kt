package com.example.socialmeetingapp.presentation.profile.createprofileflow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.socialmeetingapp.R
import com.example.socialmeetingapp.domain.user.model.User
import com.example.socialmeetingapp.presentation.authentication.components.AuthenticationTextField

@Composable
fun CreateProfileInfoScreen(
    user: User,
    onUpdateUsername: (String) -> Unit,
    onUpdateBio: (String) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(
            text = stringResource(id = R.string.register_profile_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )


        OutlinedTextField(
            value = user.username,
            onValueChange = { onUpdateUsername(it) },
            singleLine = true,
            label = {
                Text(
                    text = stringResource(R.string.profile_name_hint),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            trailingIcon = {
                Text(
                    text = "${user.username.length}/30",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                )
            },
            modifier = Modifier.padding(top = 16.dp)
        )



        Text(
            text = "This is how you'll appear to others on MapMates",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        Text(
            text = "Tell us something about yourself",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )



        OutlinedTextField(
            value = user.bio,
            onValueChange = { onUpdateBio(it) },
            minLines = 3,
            maxLines = 3,
            label = {
                Text(
                    text = stringResource(R.string.profile_bio_hint),
                    style = MaterialTheme.typography.labelSmall
                )
            },
        )

        Text(
            text = "You can leave this blank if you want and add bio later.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

    }
}