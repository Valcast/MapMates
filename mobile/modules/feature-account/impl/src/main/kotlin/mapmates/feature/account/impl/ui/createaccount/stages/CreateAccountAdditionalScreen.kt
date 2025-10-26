package mapmates.feature.account.impl.ui.createaccount.stages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import mapmates.feature.account.impl.ui.createaccount.CreateAccountState
import mapmates.feature.account.impl.ui.createaccount.Gender
import java.util.Locale
import mapmates.feature.account.impl.R as AccountR

@Composable
internal fun CreateAccountAdditionalScreen(
    state: CreateAccountState,
    onUpdateDateOfBirth: (LocalDateTime) -> Unit,
    onUpdateGender: (Gender) -> Unit
) {
    var isDatePickerVisible by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column {
        Text(
            text = stringResource(AccountR.string.account_additional_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = stringResource(AccountR.string.account_additional_age_title),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )

        OutlinedTextField(
            value = String.format(
                Locale.ROOT,
                "%02d.%02d.%d",
                state.dateOfBirth?.day,
                state.dateOfBirth?.month?.number,
                state.dateOfBirth?.year
            ),
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                //For Icons
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            onValueChange = { },
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
            text = stringResource(AccountR.string.account_additional_age_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 8.dp),
        )

        Text(
            text = stringResource(AccountR.string.account_additional_gender_title),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )

        val radioOptions = listOf(
            stringResource(AccountR.string.account_additional_gender_male),
            stringResource(AccountR.string.account_additional_gender_female),
            stringResource(AccountR.string.account_additional_gender_other)
        )

        Row(Modifier.selectableGroup()) {
            Gender.entries.forEach { gender ->
                Row(
                    Modifier
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .height(32.dp)
                        .selectable(
                            selected = (gender == state.gender),
                            onClick = { onUpdateGender(gender) },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (gender == state.gender),
                        onClick = null
                    )
                    Text(
                        text = stringResource(gender.resId),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        if (isDatePickerVisible) {
            DatePickerDialog(
                onDismissRequest = {
                    isDatePickerVisible = false
                },
                confirmButton = {

                },
                dismissButton = {

                }
            ) {
                DatePicker(
                    state = datePickerState
                )
            }
        }
    }

}