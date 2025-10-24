package com.example.socialmeetingapp.presentation.profile.editprofile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.example.socialmeetingapp.domain.model.User
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun EditProfileScreen(
    user: User?,
    username: String,
    bio: String,
    profilePictureUri: Uri?,
    isLoading: Boolean,
    onUpdateProfilePictureUri: (Uri) -> Unit,
    onSaveProfilePicture: () -> Unit,
    onUpdateBio: (String) -> Unit,
    onSaveBio: () -> Unit,
    onUpdateUsername: (String) -> Unit,
    onUpdateDateOfBirth: (LocalDateTime) -> Unit,
    onSaveUsernameAndDateOfBirth: () -> Unit,
    onBack: () -> Unit,
) {
    var isEditUsernameAndAgeDialogVisible by remember { mutableStateOf(false) }
    var isEditBioDialogVisible by remember { mutableStateOf(false) }
    var showProfilePictureEditDialog by remember { mutableStateOf(false) }

    val pickPhoto = rememberLauncherForActivityResult(
        PickVisualMedia(
        )
    ) { uri ->
        if (uri != null) {
            onUpdateProfilePictureUri(uri)
        }
    }


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
            onClick = { showProfilePictureEditDialog = true },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "Edit Profile Picture")
        }

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
    }

    if (showProfilePictureEditDialog) {
        Dialog(onDismissRequest = { showProfilePictureEditDialog = false }) {
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Edit Profile Picture",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(0.6f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(100))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            pickPhoto.launch(
                                PickVisualMediaRequest(
                                    mediaType = PickVisualMedia.SingleMimeType("image/jpeg")
                                )
                            )
                        }
                ) {
                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(profilePictureUri)
                            .build(),
                    )
                    val state by painter.state.collectAsState()

                    when (state) {
                        is AsyncImagePainter.State.Empty -> {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Profile Picture",
                            )
                        }

                        is AsyncImagePainter.State.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(48.dp)
                                    .align(Alignment.Center),
                                strokeWidth = 4.dp,
                            )
                        }

                        is AsyncImagePainter.State.Success -> {
                            Image(
                                painter = painter,
                                contentScale = ContentScale.Crop,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(100)),
                            )
                        }

                        is AsyncImagePainter.State.Error -> {
                            // Show some error UI.
                        }
                    }
                }

                Button(
                    onClick = {
                        onSaveProfilePicture()
                        showProfilePictureEditDialog = false
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    enabled = !isLoading && profilePictureUri != user?.profilePictureUri
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text(text = "Save", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }

    if (isEditUsernameAndAgeDialogVisible) {
        var datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = user?.dateOfBirth?.toInstant(TimeZone.currentSystemDefault())
                ?.toEpochMilliseconds()
        )

        LaunchedEffect(datePickerState) {
            datePickerState.selectedDateMillis?.let {
                onUpdateDateOfBirth(
                    Instant.fromEpochMilliseconds(datePickerState.selectedDateMillis!!)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                )
            }
        }

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

                Text(
                    text = "Username",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.Start)
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = onUpdateUsername,
                    singleLine = true,
                    trailingIcon = {
                        Text(
                            text = "${username.length}/30",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        )
                    },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                )


                Text(
                    text = "Date of Birth",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.Start)
                )
                BoxWithConstraints {
                    val scale =
                        remember(this.maxWidth) { if (this.maxWidth > 360.dp) 1f else (this.maxWidth / 360.dp) }
                    Box(
                        modifier = Modifier
                            .requiredWidthIn(min = 360.dp)
                            .aspectRatio(1f)
                    ) {
                        DatePicker(
                            state = datePickerState,
                            title = null,
                            headline = null,
                            showModeToggle = false,
                            colors = DatePickerDefaults.colors().copy(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                            modifier = Modifier.scale(scale),
                        )
                    }
                }
                Button(
                    onClick = {
                        onSaveUsernameAndDateOfBirth()
                        isEditUsernameAndAgeDialogVisible = false
                    }, modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    enabled = username != user?.username || datePickerState.selectedDateMillis != user.dateOfBirth.toInstant(
                        TimeZone.currentSystemDefault()
                    )
                        .toEpochMilliseconds()
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
                    modifier = Modifier
                        .padding(bottom = 4.dp, top = 16.dp)
                        .align(Alignment.End)
                )

                OutlinedTextField(
                    value = bio,
                    onValueChange = onUpdateBio,
                    minLines = 3,
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Button(
                    onClick = {
                        onSaveBio()
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