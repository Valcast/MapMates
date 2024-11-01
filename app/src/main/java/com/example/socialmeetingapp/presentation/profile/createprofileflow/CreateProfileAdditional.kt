package com.example.socialmeetingapp.presentation.profile.createprofileflow

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.socialmeetingapp.domain.user.model.User
import com.example.socialmeetingapp.presentation.event.createventflow.DatePickerModalInput
import kotlinx.datetime.LocalDateTime
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileAdditional(user: User, onUpdateDateOfBirth: (LocalDateTime) -> Unit, onUpdateGender: (String) -> Unit) {

    var isDatePickerVisible by rememberSaveable { mutableStateOf(false) }

    Column {
        Text(
            text = "Tell us more about yourself",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "When is your birthday?",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )

        OutlinedTextField(
            value = String.format(
                Locale.ROOT,
                "%02d.%02d.%d",
                user.dateOfBirth.dayOfMonth,
                user.dateOfBirth.monthNumber,
                user.dateOfBirth.year
            ),
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                //For Icons
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant),
            onValueChange = {  },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            modifier = Modifier
                .clickable { isDatePickerVisible = true }
        )

        Text(
            text = "You need to be at least 18 years old to use MapMates",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 8.dp),
        )

        Text(
            text = "What is your gender?",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )

        val radioOptions = listOf("Man", "Woman", "Other")
        Row(Modifier.selectableGroup()) {
            radioOptions.forEach { text ->
                Row(
                    Modifier
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .height(32.dp)
                        .selectable(
                            selected = (text == user.gender),
                            onClick = { onUpdateGender(text) },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == user.gender),
                        onClick = null // null recommended for accessibility with screenreaders
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        if (isDatePickerVisible) {
            DatePickerModalInput(
                onDateSelected = { date ->
                    if (date != null) {
                        onUpdateDateOfBirth(date)
                    }
                },
                onDismiss = { isDatePickerVisible = false },
            )
        }
    }

}