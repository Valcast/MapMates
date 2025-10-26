package com.valcast.mapmates.presentation.profile.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valcast.mapmates.R
import com.valcast.mapmates.domain.model.AppConfig
import com.valcast.mapmates.domain.model.Theme

@Composable
fun SettingsScreen(
    settings: AppConfig, onSignOut: () -> Unit, onBack: () -> Unit, onThemeChange: (Theme) -> Unit
) {
    var isThemeMenuExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ElevatedButton(
                onClick = onBack,
                shape = RoundedCornerShape(24.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
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

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text(
                text = "Application Theme", style = MaterialTheme.typography.bodyLarge
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.TopEnd)
            ) {
                TextButton(onClick = { isThemeMenuExpanded = !isThemeMenuExpanded }) {
                    Text(
                        text = when (settings.theme) {
                            Theme.SYSTEM -> "System"
                            Theme.LIGHT -> "Light"
                            Theme.DARK -> "Dark"
                        }
                    )
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = isThemeMenuExpanded,
                    onDismissRequest = { isThemeMenuExpanded = false }) {
                    Theme.entries.forEach {
                        DropdownMenuItem(text = {
                            Text(
                                text = when (it) {
                                    Theme.SYSTEM -> "System"
                                    Theme.LIGHT -> "Light"
                                    Theme.DARK -> "Dark"

                                }, style = MaterialTheme.typography.bodyMedium
                            )
                        }, onClick = {
                            onThemeChange(it)
                            isThemeMenuExpanded = false
                        })
                    }
                }


            }


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
            modifier = Modifier.fillMaxWidth()
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Delete Account",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}