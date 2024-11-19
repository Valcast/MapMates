package com.example.socialmeetingapp.presentation.profile.createprofileflow

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.example.socialmeetingapp.domain.model.User

@Composable
fun CreateProfilePictureScreen(user: User, onUpdateProfilePicture: (Uri) -> Unit) {

    val pickPhoto =
        rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
            Log.d("PhotoPicker", "Selected URI: $uri")
            if (uri != null) {
                onUpdateProfilePicture(uri)
            }
        }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Add a profile picture",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "You need a profile picture of yourself to join events",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 8.dp),
        )


        ElevatedButton(
            onClick = {
                pickPhoto.launch(PickVisualMediaRequest())
            },
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(
                text = "Upload a photo ->",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        SubcomposeAsyncImage(
            model = user.profilePictureUri.toString(),
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(90.dp)),
            contentScale = ContentScale.Crop,
            loading = {
                CircularProgressIndicator()
            }
        )





    }



}