package com.example.socialmeetingapp.presentation.authentication.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ThirdPartyGoogle() {
    OutlinedButton(
        onClick = { /*TODO*/ },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(10),
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Outlined.MailOutline, contentDescription = "Google icon")
            Text(
                text = "Sign in with Google",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}