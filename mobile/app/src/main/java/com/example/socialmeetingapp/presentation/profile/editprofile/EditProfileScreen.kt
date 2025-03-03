package com.example.socialmeetingapp.presentation.profile.editprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.datetime.LocalDateTime

@Composable
fun EditProfileScreen(onBack: () -> Unit, onUpdateBio: (String) -> Unit, onUpdateUsernameAndDateOfBirth: (String, LocalDateTime) -> Unit) {

    var isEditUsernameAndAgeDialogVisible by remember { mutableStateOf(false) }
    var isEditBioDialogVisible by remember { mutableStateOf(false) }

    var bio by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    var day by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }


    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            ElevatedButton(
                onClick = onBack,
                shape = RoundedCornerShape(24.dp),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",
                    modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary
                )
            }


            Text(
                text = "Edit Profile",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }


        HorizontalDivider()

        Button(
            onClick = { isEditUsernameAndAgeDialogVisible = true },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "Edit Username and Age")
        }

        Button(
            onClick = { isEditBioDialogVisible = true },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "Edit Bio")
        }

        if (isEditUsernameAndAgeDialogVisible) {
            Dialog(onDismissRequest = { isEditUsernameAndAgeDialogVisible = false }) {
                Column(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Edit Username and Age",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        singleLine = true,
                        label = {
                            Text(
                                text = "Username",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        trailingIcon = {
                            Text(
                                text = "${username.length}/30",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            )
                        },
                        modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = day,
                            onValueChange = {
                                if (it.isEmpty()) {
                                    day = ""
                                } else {
                                    val newValue = when {
                                        it.length == 1 && it.toIntOrNull() in 4..9 -> "0$it"
                                        it.length == 2 && it.startsWith("3") && (it[1].toString()
                                            .toIntOrNull() ?: 0) > 1 -> "31"
                                        it.toIntOrNull() in 1..31 -> it

                                        else -> day
                                    }
                                    day = newValue
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = {
                                Text(
                                    text = "Day",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "dd",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.padding(top = 16.dp, end = 8.dp).width(70.dp)
                        )

                        OutlinedTextField(
                            value = month,
                            onValueChange = {
                                if (it.toInt() in 1..12) month = it
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = {
                                Text(
                                    text = "Month",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "mm",
                                    style = MaterialTheme.typography.labelSmall
                                )},
                            modifier = Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp).width(70.dp)
                        )

                        OutlinedTextField(
                            value = year,
                            onValueChange = {
                                if (it.toInt() in 1900..2024) year = it
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = {
                                Text(
                                    text = "Year",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "yyyy",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.padding(top = 16.dp, start = 8.dp).fillMaxWidth()
                        )


                    }

                    Button(
                        onClick = {
                            onUpdateUsernameAndDateOfBirth(username, LocalDateTime(year.toInt(), month.toInt(), day.toInt(), 1, 0, 0))
                            isEditUsernameAndAgeDialogVisible = false
                        }, modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = "Save", style = MaterialTheme.typography.bodyMedium)
                    }
                }

            }
        }

        if (isEditBioDialogVisible) {
            Dialog(onDismissRequest = { isEditBioDialogVisible = false }) {
                Column(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Edit Bio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${bio.length}/250",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 4.dp, top = 16.dp).align(Alignment.End)
                    )

                    OutlinedTextField(
                        value = bio,
                        onValueChange = { if (it.length <= 250) bio = it },
                        minLines = 3,
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            onUpdateBio(bio)
                            isEditBioDialogVisible = false
                        }, modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = "Save", style = MaterialTheme.typography.bodyMedium)
                    }


                }
            }
        }
    }
}