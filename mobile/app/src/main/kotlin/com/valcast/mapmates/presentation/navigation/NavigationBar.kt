package com.valcast.mapmates.presentation.navigation

/*
@Composable
fun NavigationBar(
    profileID: String,
    profileImageUrl: Uri,
    notReadNotifications: Int = 0
) {
    Column {
        HorizontalDivider()
        androidx.compose.material3.NavigationBar(
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (currentRoute is Routes.Map) Icons.Default.Home else Icons.Outlined.Home,
                        contentDescription = "Home"
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
                        contentDescription = "Activities"
                    )
                },
                selected = currentRoute == Routes.Activities,
                onClick = {
                    onItemClicked(Routes.Activities)
                }
            )

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(
                            if (currentRoute == Routes.Chat) {
                                R.drawable.chat_filled
                            } else {
                                R.drawable.chat_outlined
                            }
                        ),
                        contentDescription = "Chat",
                        modifier = Modifier.size(24.dp)
                    )
                },
                selected = currentRoute == Routes.Chat,
                onClick = {
                    onItemClicked(Routes.Chat)
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
                            contentDescription = "Notifications"
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
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(12.dp)),
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
}

*/
