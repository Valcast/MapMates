package com.example.socialmeetingapp.presentation.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil3.compose.AsyncImage
import com.example.socialmeetingapp.presentation.common.NavigationManager
import com.example.socialmeetingapp.presentation.common.Routes
import kotlinx.coroutines.launch

@Composable
fun NavigationBar(currentRoute: Routes, onItemClicked: (Routes) -> Unit, profileID: String, profileImageUrl: Uri) {
    androidx.compose.material3.NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (currentRoute == Routes.Map) Icons.Default.Home else Icons.Outlined.Home,
                    contentDescription = "Search"
                )
            },
            selected = currentRoute == Routes.Map,
            onClick = {
                onItemClicked(Routes.Map)
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (currentRoute == Routes.Activities) Icons.AutoMirrored.Filled.List else Icons.AutoMirrored.Outlined.List,
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
            selected = currentRoute == Routes.MyProfile,
            onClick = {
                onItemClicked(Routes.MyProfile)
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

