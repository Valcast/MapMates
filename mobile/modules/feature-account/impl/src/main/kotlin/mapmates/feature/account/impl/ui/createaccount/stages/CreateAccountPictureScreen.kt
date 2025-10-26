package mapmates.feature.account.impl.ui.createaccount.stages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mapmates.feature.account.impl.ui.createaccount.CreateAccountState
import mapmates.feature.account.impl.R as AccountR

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CreateAccountPictureScreen(
    state: CreateAccountState,
    onAddPicture: (Uri) -> Unit,
    onDeletePicture: () -> Unit,
    onPreviewPicture: (Uri) -> Unit,
) {

    val pickPhoto = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            onAddPicture(uri)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = stringResource(AccountR.string.account_picture_title),
            style = MaterialTheme.typography.headlineMediumEmphasized,
            fontWeight = FontWeight.Bold,
        )

        AnimatedContent(
            targetState = !state.profilePictureUri.isEmpty(), transitionSpec = {
                fadeIn().togetherWith(fadeOut())
            }, label = "RowLayout", modifier = Modifier.weight(1f)
        ) { showImage ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (showImage) Arrangement.spacedBy(8.dp) else Arrangement.Start
            ) {
                Column(
                    modifier = Modifier.weight(if (showImage) 0.4f else 1f)
                ) {
                    Button(
                        onClick = { pickPhoto.launch(PickVisualMediaRequest()) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = MaterialTheme.shapes.medium,
                        enabled = state.profilePictureUri.size < 5,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Upload icon",
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = stringResource(
                                    if (state.profilePictureUri.isNotEmpty()) AccountR.string.account_picture_upload_more
                                    else AccountR.string.account_picture_upload_first
                                ),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleSmallEmphasized
                            )
                        }
                    }
                    if (state.profilePictureUri.isNotEmpty()) {
                        Button(
                            onClick = onDeletePicture,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            ),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 8.dp)
                                .fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Upload icon",
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = stringResource(AccountR.string.account_picture_remove),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleSmallEmphasized
                                )
                            }
                        }
                    }
                }

                if (showImage) {
                    AsyncImage(
                        model = state.selectedProfilePictureURi,
                        contentDescription = stringResource(AccountR.string.account_picture_description),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(0.6f)
                            .clip(MaterialTheme.shapes.medium)
                            .fillMaxHeight()
                    )
                }
            }
        }

        AnimatedContent(
            targetState = state.profilePictureUri.size > 1,
            transitionSpec = {
                (slideInVertically(initialOffsetY = { fullHeight -> fullHeight }) + fadeIn()).togetherWith(
                    slideOutVertically(targetOffsetY = { fullHeight -> fullHeight }) + fadeOut()
                )
            },
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxWidth()
        ) { showPreview ->
            if (!showPreview) return@AnimatedContent

            Column {
                Text(
                    text = stringResource(AccountR.string.account_picture_preview_title),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                )

                LazyRow(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(state.profilePictureUri.toList()) { uri ->
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .clickable { onPreviewPicture(uri) }
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = stringResource(AccountR.string.account_picture_description),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            if (uri == state.selectedProfilePictureURi) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(18.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(12.dp)

                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = when (state.profilePictureUri) {
                emptyList<Uri>() -> stringResource(AccountR.string.account_picture_description)
                else -> stringResource(AccountR.string.account_picture_max_count)
            },
            style = MaterialTheme.typography.titleSmallEmphasized,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
