package com.example.socialmeetingapp.presentation.profile.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialmeetingapp.R

@Composable
fun SettingsScreen(onSignOut: () -> Unit, onBack: () -> Unit) {

    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            ElevatedButton(
                onClick = onBack,
                shape = RoundedCornerShape(24.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",
                    modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary
                )
            }


            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))



        ElevatedButton(
            onClick = onSignOut,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonColors(
                containerColor = ButtonDefaults.elevatedButtonColors().containerColor,
                contentColor = MaterialTheme.colorScheme.error,
                disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 0.dp),
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.profile_logout),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        ElevatedButton(
            onClick = onSignOut,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonColors(
                containerColor = ButtonDefaults.elevatedButtonColors().containerColor,
                contentColor = MaterialTheme.colorScheme.error,
                disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 0.dp),
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Text(
                text = "Delete Account",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}