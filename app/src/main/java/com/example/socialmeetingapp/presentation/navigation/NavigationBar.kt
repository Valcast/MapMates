package com.example.socialmeetingapp.presentation.navigation

import android.net.Uri
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.socialmeetingapp.presentation.common.Routes

@Composable
fun NavigationBar(currentRoute: Routes, onItemClicked: (Routes) -> Unit, profileID: String, profileImageUrl: Uri, notReadNotifications: Int = 0) {
    androidx.compose.material3.NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (currentRoute is Routes.Map) Icons.Default.Home else Icons.Outlined.Home,
                    contentDescription = "Search"
                )
            },
            selected = currentRoute is Routes.Map,
            onClick = {
                onItemClicked(Routes.Map())
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (currentRoute == Routes.Activities) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Features"
                )
            },
            selected = currentRoute == Routes.Activities,
            onClick = {
                onItemClicked(Routes.Activities)
            }
        )

        NavigationBarItem(
            icon = {
                BadgedBox(badge = {
                    if (notReadNotifications > 0) {
                        Badge {
                            Text(text = notReadNotifications.toString())
                        }
                    }
                }) {
                    Icon(
                        imageVector = if (currentRoute == Routes.Notifications) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                        contentDescription = "Settings"
                    )
                }
            },
            selected = currentRoute == Routes.Notifications,
            onClick = {
                onItemClicked(Routes.Notifications)
            }
        )


        NavigationBarItem(
            icon = {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(24.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,

                )
            },
            selected = currentRoute == Routes.Profile(profileID),
            onClick = {
                onItemClicked(Routes.Profile(profileID))
            }
        )
    }
}

