package mapmates.feature.account.impl.ui.createaccount.stages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaMonth
import kotlinx.datetime.toLocalDateTime
import mapmates.feature.account.impl.ui.createaccount.CreateAccountState
import mapmates.feature.account.impl.ui.createaccount.Gender
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import mapmates.feature.account.impl.R as AccountR

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalTime::class)
@Composable
internal fun CreateAccountAdditionalScreen(
    state: CreateAccountState,
    onUpdateDateOfBirth: (LocalDate) -> Unit,
    onUpdateGender: (Gender) -> Unit
) {
    var isDatePickerVisible by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Text(
            text = stringResource(AccountR.string.account_additional_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = stringResource(AccountR.string.account_additional_age_title),
            style = MaterialTheme.typography.titleMediumEmphasized,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(0.5f)
                    .clickable {
                        isDatePickerVisible = true
                    }) {
                Text(
                    text = state.dateOfBirth?.day?.toString() ?: "DD",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                )
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        isDatePickerVisible = true
                    }) {
                Text(
                    text = state.dateOfBirth?.month?.toJavaMonth()?.getDisplayName(
                        TextStyle.FULL_STANDALONE,
                        Locale.getDefault()
                    )?.replaceFirstChar { it.uppercaseChar() } ?: "MM",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                )
            }

            Card(
                modifier = Modifier
                    .weight(0.5f)
                    .clickable {
                        isDatePickerVisible = true
                    }) {
                Text(
                    text = state.dateOfBirth?.year?.toString() ?: "YYYY",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                )
            }
        }

        Text(
            text = stringResource(AccountR.string.account_additional_age_description),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 8.dp),
        )

        Text(
            text = stringResource(AccountR.string.account_additional_gender_title),
            style = MaterialTheme.typography.titleMediumEmphasized,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )



        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            Gender.entries.forEachIndexed { index, gender ->
                Button(
                    onClick = { onUpdateGender(gender) }, shapes = ButtonShapes(
                        shape = if (gender == state.gender) MaterialTheme.shapes.extraLarge
                        else SegmentedButtonDefaults.itemShape(
                            index = index, count = Gender.entries.size
                        ), pressedShape = MaterialTheme.shapes.extraLarge
                    ), colors = ButtonDefaults.buttonColors(
                        containerColor = if (gender == state.gender) {
                            MaterialTheme.colorScheme.primary
                        } else MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = if (gender == state.gender) {
                            MaterialTheme.colorScheme.onPrimary
                        } else MaterialTheme.colorScheme.onBackground,
                    ), modifier = Modifier
                        .weight(1f)
                        .padding(1.dp)
                ) {
                    Text(
                        text = stringResource(gender.resId)
                    )
                }
            }
        }

        if (isDatePickerVisible) {
            DatePickerDialog(
                onDismissRequest = {
                    isDatePickerVisible = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        val dateTime = Instant.fromEpochMilliseconds(millis).toLocalDateTime(
                            timeZone = TimeZone.currentSystemDefault()
                        )
                        onUpdateDateOfBirth(dateTime.date)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            isDatePickerVisible = false
                            datePickerState.selectedDateMillis?.let { millis ->
                                val dateTime = Instant.fromEpochMilliseconds(millis).toLocalDateTime(
                                    timeZone = TimeZone.currentSystemDefault()
                                )
                                onUpdateDateOfBirth(dateTime.date)
                            }
                        },
                        modifier = Modifier.padding(bottom = 8.dp, end = 8.dp),
                    ) {
                        Text(
                            text = stringResource(AccountR.string.account_additional_date_picket_confirm),
                            style = MaterialTheme.typography.titleSmallEmphasized,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                },
            ) {
                DatePicker(
                    state = datePickerState
                )
            }
        }
    }

}