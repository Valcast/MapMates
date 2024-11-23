package com.example.socialmeetingapp.presentation.navigation

import android.net.Uri
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.socialmeetingapp.presentation.common.Routes

@Composable
fun NavigationBar(currentRoute: Routes, onItemClicked: (Routes) -> Unit, profileID: String, profileImageUrl: Uri) {
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

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (currentRoute == Routes.Settings) Icons.Default.Settings else Icons.Outlined.Settings,
                    contentDescription = "Settings"
                )
            },
            selected = currentRoute == Routes.Settings,
            onClick = {
                onItemClicked(Routes.Settings)
            }
        )
    }
}

